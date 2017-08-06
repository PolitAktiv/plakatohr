package org.politaktiv.portlet.svgManipulator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
	public static String fileToBase64(File f) throws IOException {
		byte[] buf = read(f);
		return bytesToBase64(buf);
	}
	
	/**
	 * Essentially the same as {@link #bytesToBase64(byte[])} but produces
	 * output suitable for the xlink:href attribute used in SVG's images.
	 * @param f the input file
	 * @return the output as xlink:href compatible base64
	 * @throws IOException 
	 */
	public static String fileToBase64Svg(File f) throws IOException {
		byte[] buf = read(f);
		String mimeType = simpleFileMagic(buf);
		if (mimeType == null) {
			throw new IOException("Cannot determine MIME type for image data");
		}
		String svgPrefix = "data:" + mimeType + ";base64,";
		return (svgPrefix + bytesToBase64(buf));
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
	

}
