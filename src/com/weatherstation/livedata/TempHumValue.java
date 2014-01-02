package com.weatherstation.livedata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.weatherstation.utils.Utils;

public class TempHumValue {

	public String temperatureinC = "23";
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
					"http://"+Utils.getEthernetIpAdd().trim()+":8083/fhem?cmd=jsonlist&XHR=1");
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
