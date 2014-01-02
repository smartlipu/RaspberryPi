package com.weatherstation.sip;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.weatherstation.utils.Utils;

/**
 * Servlet implementation class SipUriFromUser
 */
public class SipUriFromUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String sipUri = "sip:lipu@10.201.11.20:5030";

	private Logger log = Logger.getLogger(SipUriFromUser.class.getSimpleName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SipUriFromUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String value = request.getParameter("value").trim();
		response.setContentType("application/html");
		PrintWriter pw = response.getWriter();
		if (value != null) {
			try {
				//if sip sip uri from the same network then only set the new uri
				if (Utils.sameNetwork(getIpFromSipUri(value),
						Utils.getEthernetIpAdd().trim(), "255.255.255.0")) {
					setSipUri(request.getParameter("value").trim());
					pw.print("alart('You have just set a new SIP Uri')");
					pw.flush();
				}
			} catch (Exception e) {
				log.info("Problem in IP addresses");
			}
		}else{
			pw.print("alert('Sip Uri is not from same network')");
			pw.flush();
		}
	}

	private String getIpFromSipUri(String uri) {
		String ip = uri.split("@")[1];
		if (ip.contains(":")) {
			ip = ip.split(":")[0];
		}
		return ip;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	public String getSipUri() {
		return sipUri;
	}

	public void setSipUri(String sipUri) {
		this.sipUri = sipUri;
	}
}
