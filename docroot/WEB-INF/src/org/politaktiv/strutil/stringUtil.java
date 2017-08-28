package org.politaktiv.strutil;

import java.util.Arrays;
import java.util.List;

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
	
}
