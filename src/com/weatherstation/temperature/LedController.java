package com.weatherstation.temperature;

import java.io.File;
import java.io.FileWriter;

import com.pi4j.wiringpi.SoftPwm;

/**
 * @author Danish shrestha
 **/

public class LedController {
	static final String GPIO_OUT = "out";
	static final String GPIO_ON = "1";
	static final String GPIO_OFF = "0";
	

	static String[] GpioChannels;
	String[] GpioChannelDefault = { "23","17", "24", "18", "25", "27", "4",
			"22" };
	int temperatureRange = 0;

	/**
	 * @param args
	 *            the command line arguments.
	 */
	// @SuppressWarnings("UserSpecificCatch")
	public static void main(String[] args) {
		// initialize wiringPi library
        com.pi4j.wiringpi.Gpio.wiringPiSetup();
	}

	/**
	 * @param range
	 * Function to set the GPIO Channel.
	 */
	private void setGpioChannel(int range) {
		GpioChannels = new String[range + 1];
		for (int i = 0; i <= range; i++) {
			GpioChannels[i] = GpioChannelDefault[i];
		}
	}

	/**
	 * Function to set the temperature Range
	 * @param temperature
	 */
	public void setTemperatureRange(double temperature) {

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
		temperatureRange = range;
	}

	/**
	 * Function to set the led to the value of the
	 * temperature range.
	 */
	public void setLed() {
		init();
		gpioWrite(GPIO_OFF, GpioChannelDefault);
		gpioWrite(GPIO_ON, GpioChannels);
	}
	
	
	/**
	 * Function to reset the led.
	 */
	public void resetLed(){
		init();
		gpioWrite(GPIO_OFF,GpioChannelDefault);
		System.out.println("ALL THE LEDS SHOULD BE OFF ..... ");
	}

	/**
	 * 	Function to set the files as required.
	 *  for the gpio channels.
	 */
	public void init() {
		try {
			/*** Init GPIO port for output ***/
			// Open file handles to GPIO port unexport and export controls
			// Loop through all ports if more than 1
			for (String gpioChannel : GpioChannelDefault) {
				FileWriter unexportFile = new FileWriter(
						"/sys/class/gpio/unexport");
				FileWriter exportFile = new FileWriter("/sys/class/gpio/export");
				// Reset the port, if needed
				File exportFileCheck = new File("/sys/class/gpio/gpio"
						+ gpioChannel);
				if (exportFileCheck.exists()) {
					unexportFile.write(gpioChannel);
					unexportFile.flush();
					unexportFile.close();
				}
				// set the port for use
				exportFile.write(gpioChannel);
				exportFile.flush();
				exportFile.close();
				// Open file handle to port input/output control
				FileWriter directionFile = new FileWriter(
						"/sys/class/gpio/gpio" + gpioChannel + "/direction");
				// Set port for output
				directionFile.write(GPIO_OUT);
				directionFile.flush();
				directionFile.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void range(){	
        //create the range for all the port of the gpio.
        //for(int j=0; j<temperatureRange; j++){
        //	SoftPwm.softPwmCreate(j, 0,100);
		//}
        for(int j=0;j<temperatureRange+1;j++){
			SoftPwm.softPwmWrite(j,100);
		}
        for(int j=temperatureRange;j<8;j++){
        	SoftPwm.softPwmWrite(j,0);
        }
	}
	/**
	 * @param value
	 */
	public void pwm() {
		int counter;
		int period = 12;
		int repeatLoop = 25;
		try{
		while(true){
			//create a pulse for repeatLoop number of cycles 
			//for(counter=0;counter<repeatLoop;counter++){
			
				//HIGH: set the GPIO port ON
				gpioWrite(GPIO_ON,GpioChannelDefault);

				
				//pulse width determined by the amount of sleep time while HIGH
				java.lang.Thread.sleep(0,50000);
				
				//LOW: set GPIO port OFF
				gpioWrite(GPIO_OFF,GpioChannelDefault);

				
				// Frequency determined by amount of sleep time while LOW
				java.lang.Thread.sleep(period);
			//}
		}
		}
		catch(Exception e){}
//		System.out.println("Creating PWM");
//		//System.out.println(Temperature.InLoop);
//		//System.out.println(Temperature.Interupt);
//		
//		// initialize wiringPi library
//        com.pi4j.wiringpi.Gpio.wiringPiSetup();
//
//        //create the range for all the port of the gpio.
//        //for(int j=0; j<8; j++){
//        //	SoftPwm.softPwmCreate(j, 0,100);
//		//}
//        for(int j=0;j<8;j++){
//			SoftPwm.softPwmWrite(j,(temperatureRange + 1)*7);
//		}
//        if(Temperature.Interupt == "0"){
//	        try{
//	        	// continuous loop
//	        	while (Temperature.Interupt == "0" && Temperature.InLoop == "0") {
//	        		
//	        		Temperature.InLoop = "1";
//	        		for(int j=0;j<8;j++){
//	        			SoftPwm.softPwmWrite(j,(temperatureRange +1)*7);
//	        		}
//	        	}
//	        }
//	        catch(Exception e){
//	        	e.printStackTrace();
//	        }
//	        System.out.println("Interupt occured.........");
//        }
//        Temperature.InLoop = "0";
        //System.out.println("no interupt found !!!!!!!");
        
	}

	public void gpioWrite(String Write, String[] channels) {
		try {
			for (String gpioChannel : channels) {
				// Set up a GPIO port as a command channel
				FileWriter commandChannel = new FileWriter(
						"/sys/class/gpio/gpio" + gpioChannel + "/value");
				// HIGH: set the GPIO port ON
				commandChannel.write(Write);
				commandChannel.flush();
				commandChannel.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}