package org.politaktiv.svgmanipulator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Helper class that slurps an entire {@link InputStream} into a byte array.
 * @author OtN
  */
public class StreamSlurper {
	
	/**
	 * Reads all Data from the {@link InputStream} into the resulting byte array. This will not close
	 * the stream. 
	 * @param istream the input stream
	 * @return an array full of bytes read from the stream
	 * @throws IOException in any case of evil wrong-going
	 */
	public static byte[] read(InputStream istream) throws IOException {
		
		// initial buffer size: estimate of the data to read
		byte[] result = new byte[0];
		byte[] buffer = new byte[istream.available()];
		
    	while ( istream.read(buffer) != -1 ) {
    		result = concat(result, buffer);
    	}
		
		
		return buffer;
		
	}
	
	public static byte[] concat(byte[] first, byte[] second) {
		  byte[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
		}
		

}
