package org.politaktiv.strutil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

/**
 * Static class with useful stuff to do with strings
 */
public class stringUtil {

	/**
	 * Join a list of strings into a single string, putting ; in between. (There is no string join in Java6)
	 * @param list the list of strings to join.
	 * @return the joint list as a string.
	 */
	public static String strJoin(List<String> list) {
		return strJoin(list, ";");
	}
	
	/**
	 * Join an array of strings into a single string, putting ; in between. (There is no string join in Java6)
	 * @param strArr the array of strings to join.
	 * @return the joint list as a string.
	 */
	
	public static String strJoin(String[] strArr) {
		return strJoin(Arrays.asList(strArr));
	}
	
	/**
	 * Join an array of strings into a single string, putting a given delimiter in between. (There is no string join in Java6)
	 * @param strArr the array of strings to join.
	 * @param delim the delimiter to insert.
	 * @return the joint list as a string.
	 */
	
	public static String strJoin(String[] strArr, String delim) {
		return strJoin(Arrays.asList(strArr), delim);
	}
	
	/**
	 * Join a list of strings into a single string, putting a given delimiter in between. (There is no string join in Java6)
	 * @param list the list of strings to join.
	 * @param delim the delimiter to insert.
	 * @return the joint list as a string.
	 */
	public static String strJoin(List<String> list, String delim) {
		String result ="";
		for ( String e : list) {
			result = result + delim +e;
 		}
		result = result.replaceFirst(delim, "");
		
		return(result);
	}
	
	/**
	 * Formats a Map of String to an array of Strings for logging. This can be used e.g. to log preferences or request parameters
	 * as provided by LifeRay.
	 * @param prefix a prefix to insert before the result.
	 * @param m the map to be formatted.
	 * @return a string containing a human readable map representation for loggin.
	 */
	public static String formatMapForLog(String prefix, Map<String,String[]> m) {
		
		
		LinkedList<String> result = new LinkedList<String>();
		
		for ( String k : m.keySet() ) {
			
			result.add(k + "=[" + strJoin(m.get(k), ",") + "]");
			
		}
		
		return(prefix + "{" + strJoin(result, ",") + "}");
	}
	
	/**
	 * Formats a Map of String to an array of Strings for logging. This can be used e.g. to log preferences or request parameters
	 * as provided by LifeRay.
	 * @param m the map to be formatted.
	 * @return a string containing a human readable map representation for loggin.
	 */
	public static String formatMapForLog(Map<String,String[]> m) {
		return formatMapForLog("", m);
	}	
	
	
	
	public static String[] addressesToStrings(InternetAddress[] a) {
		if ( a == null) {
			return addressesToStrings("");
		}

		String[] result = new String[a.length];

		for (int i = 0 ; i < a.length; i++) {
			result[i] = a[i].getAddress();
		}
		return result;
	}
	public static String[] addressesToStrings(InternetAddress a) {
		String[] result = new String[1];
		result[0] = (a == null) ? "" : a.getAddress();
		return result;
	}
	public static String[] addressesToStrings(String a) {
		String[] result = new String[1];
		result[0] = (a == null) ? "" : a;
		return result;
	}
	
	
}
