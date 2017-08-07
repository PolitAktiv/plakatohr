package org.politaktiv.svgmanipulator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public class base64Encoder {
	
	/**
	 * Converts a buffer of bytes into a base64-encoded string 
	 * @param buf the input bytes
	 * @return the output as base64 encoded String
	 */
	public static String bytesToBase64(byte[] buf) {
		return DatatypeConverter.printBase64Binary(buf);
	}
	
	/**
	 * Converts an entire file into a base64-encoded string
	 * @param f the input file
	 * @return the output as base64 encoded string
	 * @throws IOException if anything goes wrong with the file IO
	 */
	public static String getBase64(File f) throws IOException {
		return(getBase64(new FileInputStream(f)));
	}
	
	/**
	 * Reads an entire stream and converts it into a base64-encoded string
	 * @param istream the input stream
	 * @return the output as base64 encoded string
	 * @throws IOException on any error in IO
	 */
	public static String getBase64(InputStream istream) throws IOException {
		byte[] buf = read(istream);
		return bytesToBase64(buf);
	}
	
	/**
	 * Essentially the same as {@link #getBase64(InputStream)} but produces
	 * output suitable for the xlink:href attribute used in SVG's images.
	 * @param f the input file
	 * @return the output as xlink:href compatible base64
	 * @throws IOException in case of any IO error or when the file type cannot be determined.
	 */
	public static String getBase64svg(InputStream istream) throws IOException {
		byte[] buf = read(istream);
		String mimeType = simpleFileMagic(buf);
		if (mimeType == null) {
			throw new IOException("Cannot determine MIME type for image data");
		}
		String svgPrefix = "data:" + mimeType + ";base64,";
		return (svgPrefix + bytesToBase64(buf));
		
	}
	
	/**
	 * Essentially the same as {@link #getBase64(File)} but produces
	 * output suitable for the xlink:href attribute used in SVG's images.
	 * @param f the input file
	 * @return the output as xlink:href compatible base64
	 * @throws IOException in case of any IO error or when the file type cannot be determined.
	 */
	public static String getBase64svg(File f) throws IOException {
		return(getBase64svg(new FileInputStream(f)));
	}
	
	
	
	private static String simpleFileMagic(byte[] buf) {
		if ( buf.length > 10) {
			if ( buf[6] == 'J' && buf[7] == 'F' && buf[8] == 'I' && buf[9] == 'F' ) {
				return "image/jpeg";
			}
			if ( buf[1] == 'P' && buf[2] == 'N' && buf[3] == 'G' ) {
				return "image/png";
			}
		}
		
		
		return null;
		
	}
	
	/*
	
	private static byte[] read(File file) throws IOException {

	    byte[] buffer = new byte[(int) file.length()];
	    InputStream ios = null;
	    try {
	        ios = new FileInputStream(file);
	        if (ios.read(buffer) == -1) {
	            throw new IOException(
	                    "EOF reached while trying to read the whole file");
	        }
	    } finally {
	        try {
	            if (ios != null)
	                ios.close();
	        } catch (IOException e) {
	        }
	    }
	    return buffer;
	}
	*/
	
	private static byte[] read(InputStream istream) throws IOException {
		
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
