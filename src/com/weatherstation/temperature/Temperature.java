package com.weatherstation.temperature;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class temperature
 */
public class Temperature extends HttpServlet {
	static String Interupt = "0";
	static String InLoop = "0";
	static String effect = "range";
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public Temperature() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.println("Loading Leds....... ");

		if (request.getParameter("value") != null) {
			LedController led = new LedController();
			int value = Integer.parseInt(request.getParameter("value"));
			//led.setTemperatureRange(value - 20);
			//writer.println(led.temperatureRange);
			//led.setLed();
			//led.pwm();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
