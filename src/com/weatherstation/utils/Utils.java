package com.weatherstation.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static boolean sameNetwork(String ip1, String ip2, String mask)
			throws Exception {

		byte[] a1 = InetAddress.getByName(ip1).getAddress();
		byte[] a2 = InetAddress.getByName(ip2).getAddress();
		byte[] m = InetAddress.getByName(mask).getAddress();

		for (int i = 0; i < a1.length; i++)
			if ((a1[i] & m[i]) != (a2[i] & m[i]))
				return false;

		return true;

	}

	public static String getEthernetIpAdd() {
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

	private static boolean isIpv4Address(final String ip) {

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}
}
