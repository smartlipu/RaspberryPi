package com.weatherstation.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sip.ObjectInUseException;

import org.apache.log4j.Logger;

import com.weatherstation.sip.SipController;

public class ServletDestroyListener implements ServletContextListener {

	private SipController sip;

	private Logger log = Logger.getLogger(ServletDestroyListener.class
			.getSimpleName());

	public ServletDestroyListener() {
		this.sip = SipController.getInstance();
	}

	public void contextDestroyed(ServletContextEvent event) {
		try {
			sip.stopListesing();
		} catch (ObjectInUseException e) {
			log.error("Stoping Litsening Point failed" + e);
		}
	}

	public void contextInitialized(ServletContextEvent arg0) {
	}

}
