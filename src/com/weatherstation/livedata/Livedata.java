package com.weatherstation.livedata;

import java.io.IOException;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weatherstation.sip.SipController;
import com.weatherstation.sip.SipUriFromUser;

/**
 * Servlet implementation class livedata
 */
public class Livedata extends HttpServlet {

	private String currentTem = "-5";
	public static int temprange = 0;
	static String effect = "range";
	
	private static final long serialVersionUID = 1L;
	private String lowTem;
	private String hiTem;
	private String hm;
	private String time;
	private SipController sip = null;
	private SipUriFromUser sipUri;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	private Logger log = Logger.getLogger(Livedata.class.getSimpleName());

	public Livedata() {
		super();
		this.sip = SipController.getInstance();
		this.sipUri = new SipUriFromUser();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		
		// get the temperature / humidity value from the fhem server.
		TempHumValue temp = new TempHumValue();
		String json = temp.getValue();

		// parse the JSON string and set the value of temperature, humidity and time or reading of their own private variable
		parseJson(json);
		
		//SIP PART
		if(Double.valueOf(lowTem)> Double.valueOf(currentTem)){
			this.lowTem = this.currentTem;
		}
		if(Double.valueOf(hiTem)< Double.valueOf(currentTem)){
			this.hiTem = this.currentTem;
		}
		// if the temperature difference is 2 then send sip Message
		double temDiff = Double.valueOf(hiTem) - Double.valueOf(lowTem);
		//To get exact difference
		DecimalFormat df = new DecimalFormat("###.##");
		log.info("Tem Differece is: " +df.format(temDiff));
		double exactDif = Double.valueOf(df.format(temDiff));
		if (Math.abs(exactDif) > sip.getTemperatureDifference()) {
			try {
				sip.sendMessage(sipUri.getSipUri(),
						"Current Temperature is: " + currentTem
								+ " and Humidity is: " + hm + " at the time "
								+ time);
				this.lowTem = this.hiTem = this.currentTem;
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (SipException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			}
		}
		
		//LED CONTROL.
		int value = 0;
		LedController led = new LedController();
		
		//The case where the slider value is changed.
		if (request.getParameter("value") != null) {
			value = Integer.parseInt(request.getParameter("value"));
			led.setTemperatureRange(value - 20);
		}
		
		//The case where the effect is changed.
		else if(request.getParameter("effect") != null){
			if(Integer.parseInt(request.getParameter("effect")) == 0){
				effect = "range";
				System.out.println("Range should be there........");
			}	
			else if(Integer.parseInt(request.getParameter("effect")) == 1){
				effect = "dim";
				System.out.println("Dim should be there........");
			}
		}
		
		//The case where polling occurs.
		else{
			value = Integer.parseInt(currentTem);
			led.setTemperatureRange(Double.parseDouble(currentTem));
		}
		
		if(effect == "dim"){
			while(led.InLoop == 1){
				led.Interupt = 1;
				try{
					java.lang.Thread.sleep(50);
					System.out.println("Waiting for the interupt to change ........");
				}catch(Exception e){
					
				}
			}	
			led.pwm();
		}
		else{
			led.range();
		}
		
		//RETURN THE JSON VALUE.
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	/** 
	 * JSON parser.
	 */
	private void parseJson(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray results = obj.getJSONArray("Results");
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = (JSONObject) results.get(i);
				if ("CUL_HM".equalsIgnoreCase(result.getString("list").trim())) {
					JSONArray devices = result.getJSONArray("devices");
					JSONObject device = (JSONObject) devices.get(1);
					JSONArray readings = device.getJSONArray("READINGS");
					JSONObject reading = (JSONObject) readings.get(readings.length()-1);
					this.currentTem = reading.getString("temperature");
					this.hm = ((JSONObject) readings.get(1))
							.getString("humidity");
					this.time = ((JSONObject) readings.get(1))
							.getString("measured");
					if (this.lowTem == null || lowTem.equals("")) {
						this.lowTem = this.hiTem = this.currentTem;
						//fist time when the server is starting at that time temperature and hm value
						sip.sendMessage(sipUri.getSipUri(),
						"Current Temperature is: " + currentTem
								+ " and Humidity is: " + hm + " at the time "
								+ time);
					}
					log.info("temp is:  " + currentTem + " HM value: " + hm
							+ " at " + time);
				}
			}
		} catch (JSONException e1) {
			// log.error(e1);
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
	}
}
