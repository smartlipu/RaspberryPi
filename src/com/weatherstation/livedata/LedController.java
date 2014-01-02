package com.weatherstation.livedata;

//import java.io.File;
//import java.io.FileWriter;
//
//import com.pi4j.wiringpi.SoftPwm;

/**
 * @author Danish Shrestha
 **/

public class LedController {
	
	//GPIO States
	static final int GPIO_ON = 1;
	static final int GPIO_OFF = 0;
	
	//GPIO channels.
	static String[] GpioChannels;
	String[] GpioChannelDefault = { "23","17", "24", "18", "25", "27", "4","22" };
	
	//Temperature Range defined for different level of intensity of the LEDS.
	int temperatureRange = 0;
	
	//Intensity of the LEDS. 5 levels of intensity levels and there
	//delay times while led are high and low.
	static int[] delay_off = {20,10,5,20,10,5};
	static int[] delay_on = {0,0,0,2,10,20};
	
	//GPIO controller.
	static com.pi4j.io.gpio.GpioController gpio = com.pi4j.io.gpio.GpioFactory.getInstance();  
	//GPIO PIN.
	static com.pi4j.io.gpio.GpioPinDigitalOutput ledPin[] = {
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_04, "MyLED4", com.pi4j.io.gpio.PinState.LOW),	
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_00, "MyLED0", com.pi4j.io.gpio.PinState.LOW),
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_05, "MyLED5", com.pi4j.io.gpio.PinState.LOW),
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_01, "MyLED1", com.pi4j.io.gpio.PinState.LOW),
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_06, "MyLED6", com.pi4j.io.gpio.PinState.LOW),
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_02, "MyLED2", com.pi4j.io.gpio.PinState.LOW),
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_07, "MyLED7", com.pi4j.io.gpio.PinState.LOW),
			gpio.provisionDigitalOutputPin(com.pi4j.io.gpio.RaspiPin.GPIO_03, "MyLED3", com.pi4j.io.gpio.PinState.LOW),
	};
	
	//Interrupt for the PWM to stop
	static int Interupt = 0;
	static int InLoop = 0;
	
	
	/**
	 * @param args
	 * the command line arguments.
	 */
	// @SuppressWarnings("UserSpecificCatch")
	public static void main(String[] args) {
		
	}
	
	/**
	 * Set LEDS on/off according to the 
	 * temperature range.
	 */
	public void range(){	
		//set pin value 1
        for(int j=0;j<=temperatureRange;j++){
			setGpioPin(j, 1);
		}
        if(temperatureRange!=7){
        	//set pin value 0
        	for(int j=temperatureRange;j<8;j++){
        		setGpioPin(j, 0);
        	}
        }
        
        
	}
	
	/**
	 * Generate the PWM.
	 */
	public int pwm() {
		System.out.println(temperatureRange);
		System.out.println(delay_on[temperatureRange]);
		System.out.println(delay_off[temperatureRange]);
		
		
		
		if(temperatureRange == 0 || temperatureRange == 7){
			range();
			return 1;
		}
		try{  
		       while(Interupt == 0)  
		       {  
		    	   InLoop = 1;
		    	   
		    	   //set all the pin On.
		    	   for(int j=0;j<8;j++){
		    		   setGpioPin(j, 1);
		    	   }
		    	   //delay the pin when it is On.
		    	   if(delay_on[temperatureRange]!=0){
		    		   Thread.sleep(delay_on[temperatureRange]);   
		    	   }
		    	   //set all the pin Off.
		    	   for(int j=0;j<8;j++){
		    		   setGpioPin(j, 0);
		    	   }
		    	   //delay the pin when it is Off.
		    	   if(delay_off[temperatureRange]!=0){
		    		   Thread.sleep(delay_off[temperatureRange]);
		    	   }
		       }  
	     }  
	     catch(Exception ex){  
	       gpio.shutdown();  
	       ex.printStackTrace();  
	     }  
		Interupt = 0;
		InLoop = 0;
		return 1;
	}
	
	/**
	 * Write value in the GPIO pin.
	 * @param key->gpio pin number,value
	 */
	public void setGpioPin(int key,int value){
		if(value == GPIO_ON){
			ledPin[key].high();
		}
		else{
			ledPin[key].low();
		} 
	}
	
	/**
	 * Function to set the temperature Range.
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
}