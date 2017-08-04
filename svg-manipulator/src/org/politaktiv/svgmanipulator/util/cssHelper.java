package org.politaktiv.svgmanipulator.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Tools for handling CSS in SVG files
 *
 */
public class cssHelper {
	
	public static HashMap<String,String> splitCss(String css) {
		
		HashMap<String, String> result = new HashMap<String, String>();
		
		List<String> elements = Arrays.asList(css.split("\\s*;\\s*"));
		for ( String e : elements) {
			String[] keyValue = e.split("\\s*:\\s*");
			//System.err.println(e);
			result.put(keyValue[0].trim(), keyValue[1].trim());
		}
		
		return result;
		
	}
	
	public static String CssToString(HashMap<String,String> map) {
		
		Set<String> elements = new HashSet<String>();
		
		for ( String key : map.keySet() ) {
			String e = key + ":" + map.get(key);
			elements.add(e);
		}
		
		// no string join in Java 6
		String result ="";
		for ( String e : elements) {
			result = result + ";" +e;
 		}
		result = result.replaceFirst(";", "");
		
		//System.err.println(result);
		
		
		return (result);
		
	}
	
	/**
	 * Takes the value from b and overrides or inserts those in a, returning the result 
	 * @param a
	 * @param b
	 * @return
	 */
	public static HashMap<String,String> addAndOverride(HashMap<String,String> a, HashMap<String,String> b) {
		HashMap<String, String> result = new HashMap<String,String>();
		result.putAll(a);
		
		for ( String key : b.keySet() ) {
			result.put(key, b.get(key));
		}
		
		return(result);
		
		
	}
	
	

}
