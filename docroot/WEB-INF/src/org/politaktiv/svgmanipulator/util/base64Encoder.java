package org.politaktiv.svgmanipulator.util;

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
	public static String getBase64(File f) throws IOException {
		FileInputStream inS = new FileInputStream(f);
		String result = getBase64(inS);
		inS.close();
		return result;
	}
	
	/**
	 * Reads an entire stream and converts it into a base64-encoded string
	 * @param istream the input stream
	 * @return the output as base64 encoded string
	 * @throws IOException on any error in IO
	 */
	public static String getBase64(InputStream istream) throws IOException {
		byte[] buf = StreamSlurper.read(istream);
		return bytesToBase64(buf);
	}
	
	/**
	 * Essentially the same as {@link #getBase64(InputStream)} but produces
	 * output suitable for the xlink:href attribute used in SVG's images.
	 * @param f the input file
	 * @return the output as xlink:href compatible base64
	 * @throws IOException in case of any IO error or when the file type cannot be determined.
	 */
	public static String getBase64svg(InputStream istream) throws MimeTypeException, IOException {
		byte[] buf = StreamSlurper.read(istream);
		String mimeType = simpleFileMagic(buf);
		if (mimeType == null) {
			throw new MimeTypeException("Cannot determine MIME type for image data");
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
	 * @throws MimeTypeException 
	 */
	public static String getBase64svg(File f) throws IOException, MimeTypeException {
		return(getBase64svg(new FileInputStream(f)));
	}
	
	
	
	private static String simpleFileMagic(byte[] buf) {
		if ( buf.length > 10) {
			if ( buf[0] == (byte)0xFF   &&  buf[1] == (byte)0xD8 && buf[2] == (byte)0xFF ) {
				return "image/jpeg";
			}	
			if ( buf[1] == 'P' && buf[2] == 'N' && buf[3] == 'G' ) {
				return "image/png";
			}
		}
		
		
		return null;
		
	}
	

	
}
