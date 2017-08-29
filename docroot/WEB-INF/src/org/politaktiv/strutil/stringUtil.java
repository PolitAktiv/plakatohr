package org.politaktiv.strutil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class stringUtil {

	public static String strJoin(List<String> list) {
		return strJoin(list, ";");
	}
	
	public static String strJoin(String[] strArr) {
		return strJoin(Arrays.asList(strArr));
	}
	
	public static String strJoin(String[] strArr, String delim) {
		return strJoin(Arrays.asList(strArr), delim);
	}
	
	public static String strJoin(List<String> list, String delim) {
		String result ="";
		for ( String e : list) {
			result = result + delim +e;
 		}
		result = result.replaceFirst(delim, "");
		
		return(result);
	}
	
	
	public static String formatMapForLog(String prefix, Map<String,String[]> m) {
		
		
		LinkedList<String> result = new LinkedList<String>();
		
		for ( String k : m.keySet() ) {
			
			result.add(k + "=[" + strJoin(m.get(k), ",") + "]");
			
		}
		
		return(prefix + "{" + strJoin(result, ",") + "}");
	}
	
}
