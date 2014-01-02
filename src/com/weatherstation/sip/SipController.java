package com.weatherstation.sip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

public class SipController implements SipListener {

	private static SipController instance = null;

	private static SipProvider sipProvider;

	private static AddressFactory addressFactory;

	private SipFactory sipFactory;

	private static MessageFactory messageFactory;

	private static HeaderFactory headerFactory;

	private static SipStack sipStack;

	private ListeningPoint udpListeningPoint;

	private String ipAddress;
	
	private double temperatureDifference = 2.0;

	private Logger log = Logger.getLogger(SipController.class.getSimpleName());

	private SipController() throws PeerUnavailableException,
			TransportNotSupportedException, InvalidArgumentException,
			ObjectInUseException, TooManyListenersException {
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "WatherStation");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
				"WatherStation.txt");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
				"WatherStationdebug.log");
		properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
				"false");
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "TRACE");

		sipStack = sipFactory.createSipStack(properties);
		addressFactory = sipFactory.createAddressFactory();
		headerFactory = sipFactory.createHeaderFactory();
		messageFactory = sipFactory.createMessageFactory();

		// ipaddress of computer eth0
		this.ipAddress = getEthernetIpAdd();
		log.info("IP address is(logger): " + getEthernetIpAdd());

		udpListeningPoint = sipStack.createListeningPoint(ipAddress, 5060,
				ListeningPoint.UDP);
		sipProvider = sipStack.createSipProvider(udpListeningPoint);
		sipProvider.addSipListener(this);
	}

	public static SipController getInstance() {
		if (instance == null) {
			try {
				instance = new SipController();
			} catch (PeerUnavailableException e) {
				e.printStackTrace();
			} catch (TransportNotSupportedException e) {
				e.printStackTrace();
			} catch (ObjectInUseException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			} catch (TooManyListenersException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public void sendMessage(String to, String message) throws ParseException,
			SipException, InvalidArgumentException {
		SipURI from = addressFactory.createSipURI(getUsername(), getHost()
				+ ":" + getPort());
		Address fromNameAddress = addressFactory.createAddress(from);
		fromNameAddress.setDisplayName(getUsername());
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
				"watherstationv1.0");

		String username = to.substring(to.indexOf(":") + 1, to.indexOf("@"));
		String address = to.substring(to.indexOf("@") + 1);

		// to header
		SipURI toAddress = addressFactory.createSipURI(username, address);
		Address toNameAddress = addressFactory.createAddress(toAddress);
		toNameAddress.setDisplayName(username);
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

		SipURI requestURI = addressFactory.createSipURI(username, address);
		requestURI.setTransportParam("udp");

		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(),
				getPort(), "udp", "branch1");
		viaHeaders.add(viaHeader);

		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L,
				Request.MESSAGE);

		MaxForwardsHeader maxForwards = headerFactory
				.createMaxForwardsHeader(70);

		Request request = messageFactory.createRequest(requestURI,
				Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, maxForwards);

		SipURI contactURI = addressFactory.createSipURI(getUsername(),
				getHost());
		contactURI.setPort(getPort());
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());
		ContactHeader contactHeader = headerFactory
				.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		ContentTypeHeader contentTypeHeader = headerFactory
				.createContentTypeHeader("text", "plain");
		request.setContent(message, contentTypeHeader);

		System.out.println(request);
		System.out.println(sipProvider.getListeningPoints());
		sipProvider.sendRequest(request);
		// stopListesing();
	}

//	private void stopListesing() throws ObjectInUseException {
//		sipProvider.removeListeningPoint(udpListeningPoint);
//		for (ListeningPoint lp : sipProvider.getListeningPoints()) {
//			sipStack.deleteListeningPoint(lp);
//		}
//		sipStack.deleteSipProvider(sipProvider);
//		sipStack.stop();
//	}

	private int getPort() {
		return 5060;
	}

	private String getHost() {
		return ipAddress;
	}

	private String getUsername() {
		return "weatherstation";
	}

	public static void main(String[] a) {
		try {
			SipController sip = new SipController();
			sip.sendMessage("sip:lipu@" + sip.getHost() + ":5030",
					"test message");

		} catch (PeerUnavailableException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
	}

	public void processIOException(IOExceptionEvent arg0) {
	}

	public void processRequest(RequestEvent event) {
		Request req = event.getRequest();
		CSeqHeader cseq = (CSeqHeader) req.getHeader(CSeqHeader.NAME);
		String method = cseq.getMethod();
		if(!Request.MESSAGE.equals(method)){
			log.info("Bad Request: " + method + " Only MESSAGE is accepted my this server");
			return;
		}
		String msg = new String(req.getRawContent());
		setTemperatureDifference(Double.valueOf(msg));
	}

	public void processResponse(ResponseEvent event) {
		Response res = event.getResponse();

		if (res.getStatusCode() == 200) {
			log.info("200 OK message is received");
//			try {
//				stopListesing();
//			} catch (ObjectInUseException e) {
//				e.printStackTrace();
//			}
		}
	}

	public void processTimeout(TimeoutEvent arg0) {
	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
	}

	public String getEthernetIpAdd() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();

			while (en.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) en.nextElement();
				if ("eth0".equalsIgnoreCase(ni.getDisplayName())) {
					Enumeration<InetAddress> ee = ni.getInetAddresses();
					while (ee.hasMoreElements()) {
						InetAddress ia = (InetAddress) ee.nextElement();
						if (isIpv4Address(ia.getHostAddress())) {
							return ia.getHostAddress();
						}
					}
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private static final String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static boolean isIpv4Address(final String ip) {

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	public double getTemperatureDifference() {
		return temperatureDifference;
	}

	public void setTemperatureDifference(double temperatureDifference) {
		this.temperatureDifference = temperatureDifference;
	}
}
