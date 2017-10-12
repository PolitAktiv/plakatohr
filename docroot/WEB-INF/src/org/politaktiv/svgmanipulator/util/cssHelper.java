package org.politaktiv.svgmanipulator.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Tools for handling CSS as found in in SVG files (not as found as in HTML)
 *
 */
public class cssHelper {
	
	/**
	 * Splits CSS data from a style attribute into a map of key-value pairs
	 * @param css the CSS data
	 * @return a map of key value pairs.
	 */
	public static HashMap<String,String> splitCss(String css) {
		
		HashMap<String, String> result = new HashMap<String, String>();
		
		List<String> elements = Arrays.asList(css.split("\\s*;\\s*"));
		for ( String e : elements) {
			String[] keyValue = e.split("\\s*:\\s*");
			// skip split errors
			if (keyValue.length >= 2) {
				result.put(keyValue[0].trim(), keyValue[1].trim());
			}	
		}
		
		return result;
		
	}
	
	/**
	 * Takes a map of key-value pairs and re-combines them to a string usable for a style attribute
	 * @param map map to use
	 * @return the single string style attribute.
	 */
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
		
		return (result);
		
	}
	
	/**
	 * Takes values from key-value map <strong>a</strong> and overrides those in key-value map <strong>a</strong>.
	 * Values not present in <strong>a</strong> will be inserted. 
	 * @param a the original key-value-map
	 * @param b the map from which stuff is overwritten in a, or added to a.
	 * @return the result of all of this magic.
	 */
	public static HashMap<String,String> addAndOverride(HashMap<String,String> a, HashMap<String,String> b) {
		return( addAndOverride(a, b, null));
		
	}
	
	/**
	 * Takes values from key-value map <strong>a</strong> and overrides those in key-value map <strong>a</strong>.
	 * Values not present in <strong>a</strong> will be inserted. <strong>prefix</strong> specifies a prefix for key names
	 * in <strong>a</strong> that is used as a selector.
	 * @param a the original key-value-map
	 * @param b the map from which stuff is overwritten in a, or added to a.
	 * @param prefix only keys with this prefix will be used from b.
	 * @return the result of all of this magic.
	 */
	public static HashMap<String,String> addAndOverride(HashMap<String,String> a, HashMap<String,String> b, String prefix) {
		HashMap<String, String> result = new HashMap<String,String>();
		result.putAll(a);

		if (prefix == null) {
			for ( String key : b.keySet() ) {
				result.put(key, b.get(key));
			}
		} else {
			for ( String key : b.keySet() ) {
				if (key.startsWith(prefix)) {
					result.put(key, b.get(key));
				}	
			}
			
		}
		
		return(result);
		
	}
	

}
