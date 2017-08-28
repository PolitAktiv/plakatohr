package org.politaktiv.strutil;

import java.util.LinkedList;
import java.util.Map;

import com.liferay.portal.kernel.log.Log;

public class logUtil {
	
	public static void logMapDebug(Log logger, String prefix, Map<String,String[]> m) {
		
		
		LinkedList<String> result = new LinkedList<String>();
		
		for ( String k : m.keySet() ) {
			
			result.add(k + "=[" + stringUtil.strJoin(m.get(k), ",") + "]");
			
		}
		
		logger.debug(prefix + ": {" + stringUtil.strJoin(result, ",") + "}");
	}

}
