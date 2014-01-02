package com.weatherstation.temperature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class TempHumValue {

	public String temperatureinC = "30";
	public String temperatureinF;
	public String humidity;
	public String time;

	public static void main(String[] args) {
		// setValue();
	}

	public String getValue() {
		String Json = "";
		try {
			URL fhemserver = new URL(
					"http://192.168.0.10:8083/fhem?cmd=jsonlist&XHR=1");
			URLConnection get = fhemserver.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					get.getInputStream()));
			String InputLine;
			while ((InputLine = in.readLine()) != null)
				Json += InputLine;
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Json;
	}
}
