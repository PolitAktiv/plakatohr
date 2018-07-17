package org.politaktiv.svgmanipulator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class StreamSlurper {
	
	public static byte[] read(InputStream istream) throws IOException {
		
		// initial buffer size: estimate of the data to read
		byte[] result = new byte[0];
		byte[] buffer = new byte[istream.available()];
		
	    try {		
	    	while ( istream.read(buffer) != -1 ) {
	    		result = concat(result, buffer);
	    	}
	    } finally {
	        try {
	            if (istream != null)
	                istream.close();
	        } catch (IOException e) {
	        }
	    }
		
		
		return buffer;
		
	}
	
	public static byte[] concat(byte[] first, byte[] second) {
		  byte[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
		}
		

}
