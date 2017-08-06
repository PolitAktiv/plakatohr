package org.politaktiv.portlet.svgManipulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.PDFTranscoder;

/**
 * Converts SVG to various output formats using Apache FOP and Apache Batik. Attention: SVG data might need
 * mending in order to be compatible.
 * @author but
 *
 */
public class SvgConverter {
	
	private static class OutputFormat { };
	public static OutputFormat JPG = new OutputFormat();
	public static OutputFormat PNG = new OutputFormat();
	public static OutputFormat PDF = new OutputFormat();
	public static OutputFormat SVG = new OutputFormat();
	
	
	private String xmlData;
	
	/**
	 * Constructs a new converter containing XML/SVG data
	 * @param SvgXmlData the data as a string. Must be valid SVG that can be understood by Apache Batik and FOP.
	 */
	public SvgConverter(String SvgXmlData) {
		xmlData = SvgXmlData;
	}
	
	/**
	 * See {@link #generateOutput(OutputStream, OutputFormat)}. Same thing but using an output file.
	 */
	public void generateOutput(File outputFile, OutputFormat format) throws IOException {
    	FileOutputStream ostream = new FileOutputStream(outputFile);
    	generateOutput(ostream, format);
    	ostream.close();
	}
	
	/**
	 * Generate output from the SVG data stored in this converter. Depending on the format, uses a rasterizer
	 * or PDF converter (or passthru in case of {@link #SVG})
	 * @param ostream this is where the output goes to.
	 * @param format the file format.
	 * @throws IOException in any case of evil wrong-going of things.
	 */
	public void generateOutput(OutputStream ostream, OutputFormat format) throws IOException {
		
		// special case: just dump the SVG data
		if (format == SVG) {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(ostream));
			w.write(xmlData);
			w.flush();
			return;
		}
		
		
    	SVGAbstractTranscoder t;
    	// initialize different output transcoders depending on the format
    	if (format == JPG) {
    		 t = new JPEGTranscoder();
    		  t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
    	                new Float(.9));			    		 
    	} else if ( format == PNG) {
    		t = new PNGTranscoder();
    	} else if (format== PDF) {
    		t = new PDFTranscoder();
    	} else {
    		throw new RuntimeException("Impossible state reached: undefined file format requested.");
    	}
    	
    	// use the transcoders
    	genericTranscoding(t, ostream);
    	
    	// clean the toilet
    	ostream.flush();
    	
    	
	}
	

	/**
	 * Generic wrapper for transcoding. Does not  {@link OutputStreamWriter#flush()}
	 * @param t the transcoder to use.
	 * @param ostream the output stream to store to.
	 * @throws IOException in any case of error.
	 */
    private void genericTranscoding(SVGAbstractTranscoder t, OutputStream ostream) throws IOException {
		TranscoderInput transcoderInput = new TranscoderInput( 
        		new StringReader(xmlData));
        TranscoderOutput transcoderOutput = new TranscoderOutput(ostream);
        try {
			t.transcode(transcoderInput, transcoderOutput);
		} catch (TranscoderException e) {
			throw new IOException(e);
		}
    }
	

	
	

}
