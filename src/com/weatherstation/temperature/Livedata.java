package com.weatherstation.temperature;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

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

import com.pi4j.wiringpi.SoftPwm;
import com.weatherstation.sip.SipController;

/**
 * Servlet implementation class livedata
 */
public class Livedata extends HttpServlet {
	private String currentTem = "20";
	public static int temprange = 0;
	static String effect = "range";
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	private Logger log = Logger.getLogger(Livedata.class.getSimpleName());

	public Livedata() {
		super();
		//this.sip = SipController.getInstance();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		System.out.println("New request made new one 5545555!.");
		int value = 0;
		int[] range = {4,0,5,1,6,2,7,4};
		//The case where the slider value is changed.
		if (request.getParameter("value") != null) {
			value = Integer.parseInt(request.getParameter("value"));
			temprange = setTemperatureRange(value - 20);
		}
		//The case where the effect is changed.
		else if(request.getParameter("effect") != null){
			if(Integer.parseInt(request.getParameter("effect")) == 0){
				effect = "range";
				temprange = 5;
				System.out.println("Range should be there........");
    			System.out.println(temprange);
    			
			}	
			else if(Integer.parseInt(request.getParameter("effect")) == 1){
				effect = "dim";
				value = 20;
				System.out.println("Dim should be there........");
    			System.out.println(value);
    			
			}
		}
		//The case where polling occurs.
		else{
			value = Integer.parseInt(currentTem);
			temprange = setTemperatureRange(Double.parseDouble(currentTem));
		}
		
		
//		LedController led = new LedController();
//		led.init();
//		led.pwm();
		
		
		
		
		
		
		
		
		
		
		// initialize wiringPi library
//        com.pi4j.wiringpi.Gpio.wiringPiSetup();
//        for(int j=0; j<8; j++){
//        	SoftPwm.softPwmCreate(j,0,100);
//		}
//        for(int j=0;j<8;j++){
//        	if(effect == "dim"){
//        		SoftPwm.softPwmWrite(j,value);
//        	}
//        	else{
//        		if(j<temprange + 1){
//        			SoftPwm.softPwmWrite(range[j],100);
//        		}
//        	}
//		}	
		
		
		
		
		
		 final com.pi4j.io.gpio.GpioController gpio = com.pi4j.io.gpio.GpioFactory.getInstance();  
	     
		 final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin0 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_00, "MyLED0", com.pi4j.io.gpio.PinState.LOW);
	     final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin1 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_01, "MyLED1", com.pi4j.io.gpio.PinState.LOW);
	     final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin2 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_02, "MyLED2", com.pi4j.io.gpio.PinState.LOW);
	     final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin3 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_03, "MyLED3", com.pi4j.io.gpio.PinState.LOW);
	     final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin4 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_04, "MyLED4", com.pi4j.io.gpio.PinState.LOW);
	     final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin5 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_05, "MyLED5", com.pi4j.io.gpio.PinState.LOW);
	     final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin6 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_06, "MyLED6", com.pi4j.io.gpio.PinState.LOW);
	     final com.pi4j.io.gpio.GpioPinDigitalOutput ledPin7 = gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_07, "MyLED7", com.pi4j.io.gpio.PinState.LOW);
	     
	     try  
	     {  
	       while(true)  
	       {  
	    	   ledPin0.high();  
	    	   ledPin1.high();
	    	   ledPin2.high();  
	    	   ledPin3.high();  
	    	   ledPin4.high();  
	    	   ledPin5.high(); 
	    	   ledPin6.high(); 
	    	   ledPin7.high(); 
	    	   
	         Thread.sleep(1);  
	         	ledPin1.low();  
	         	ledPin0.low();  
	         	ledPin1.low();
	         	ledPin2.low();  
	         	ledPin3.low();  
	         	ledPin4.low();  
	         	ledPin5.low();
	         	ledPin6.low();
	         	ledPin7.low();
	         Thread.sleep(0,10000);  
	       }  
	     }  
	     
	     catch(Exception ex)  
	     {  
	       gpio.shutdown();  
	       ex.printStackTrace();  
	     }  
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
	 * Function to set the temperature Range
	 * @param temperature
	 */
	public int setTemperatureRange(double temperature) {

		int range = 0;
		// -10 below
		if (temperature < -10) {
			range = 0;
		} else if (temperature <= 0 && temperature > -10) {
			range = 1;
		} else if (temperature <= 10 && temperature > 0) {
			range = 2;
		} else if (temperature <= 20 && temperature > 10) {
			range = 3;
		} else if (temperature <= 30 && temperature > 20) {
			range = 4;
		} else if (temperature <= 40 && temperature > 30) {
			range = 5;
		} else if (temperature <= 50 && temperature > 40) {
			range = 6;
		} else {
			range = 7;
		}
		return range;
	}

}
