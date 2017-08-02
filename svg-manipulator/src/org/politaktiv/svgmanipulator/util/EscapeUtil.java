package org.politaktiv.svgmanipulator.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscapeUtil {
	
	
	/**
	 * Escape a string so that it can be used in an XPath expression.
	 * @param s the input string.
	 * @return the escaped output string.
	 */
	public static String escapeXpath(String s) {
		
	    Matcher matcher = Pattern.compile("['\"]")
	            .matcher(s);
	        StringBuilder buffer = new StringBuilder("concat(");
	        int start = 0;
	        while (matcher.find()) {
	          buffer.append("'")
	              .append(s.substring(start, matcher.start()))
	              .append("',");
	          buffer.append("'".equals(matcher.group()) ? "\"'\"," : "'\"',");
	          start = matcher.end();
	        }
	        if (start == 0) {
	          return "'" + s + "'";
	        }
	        return buffer.append("'")
	            .append(s.substring(start))
	            .append("'")
	            .append(")")
	            .toString();
		
	}
	
	/**
	 * Simple and inefficient escaping to XML, replaces &, <, >, " with corresponding entities. 
	 * @param s the string to be escaped.
	 * @return the escaped string result.
	 */
	public static String escapeXml(String s) {
	    return s.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
	}

}
