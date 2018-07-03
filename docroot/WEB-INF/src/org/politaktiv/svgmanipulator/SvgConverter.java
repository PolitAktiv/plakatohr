package org.politaktiv.svgmanipulator;

import java.io.BufferedWriter;

import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;


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
 * {@link SvgManipulator} can do this mending.
 */
public class SvgConverter {
	
	private static class OutputFormat { };
	public static OutputFormat JPG = new OutputFormat();
	public static OutputFormat PNG = new OutputFormat();
	public static OutputFormat PDF = new OutputFormat();
	public static OutputFormat SVG = new OutputFormat();
	public static OutputFormat SVG_PRETTY_XML = new OutputFormat();
	
	
	private String xmlData;
	
	
	private String prettyXml(String xml) throws IOException {
	     try {
	            final InputSource src = new InputSource(new StringReader(xml));
	            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
	            final Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));

	        //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");


	            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
	            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
	            final LSSerializer writer = impl.createLSSerializer();

	            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
	            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

	            return writer.writeToString(document);
	        } catch (Exception e) {
	            throw new IOException(e);
	        }
	}
	
	
	/**
	 * Constructs a new converter containing XML/SVG data
	 * @param SvgXmlData the data as a string. Must be valid SVG that can be understood by Apache Batik and FOP.
	 */
	public SvgConverter(String SvgXmlData) {
		xmlData = SvgXmlData;
	}

	/**
	 * Generate output to a file from the SVG data stored in this converter. Depending on the format, uses a rasterizer
	 * or PDF converter (or passthru in case of {@link #SVG}). 
	 * @param outputFile this is where the output goes to.
	 * @param format the file format.
	 * @throws IOException in any case of evil wrong-going of things.
	 */
	public void generateOutput(File outputFile, OutputFormat format) throws IOException {
    	FileOutputStream ostream = new FileOutputStream(outputFile);
    	generateOutput(ostream, format);
    	ostream.close();
	}
	
	/**
	 * Generate output to a stream from the SVG data stored in this converter. Depending on the format, uses a rasterizer
	 * or PDF converter (or passthru in case of {@link #SVG}). Attention: This does not close the output stream!
	 * @param ostream this is where the output goes to.
	 * @param format the file format.
	 * @throws IOException in any case of evil wrong-going of things.
	 */
	public void generateOutput(OutputStream ostream, OutputFormat format) throws IOException {
		
		// special case 1: just dump the SVG data
		// special case 2: create pretty XML from SVG data
		if (format == SVG || format == SVG_PRETTY_XML) {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(ostream));
			if (format == SVG_PRETTY_XML ) {
				w.write(prettyXml(xmlData));
			} else { 
				w.write(xmlData);
			}
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
    		PDFTranscoder pt = new PDFTranscoder();
    		//pt.
  
    		//pt.
    		
    		t = (SVGAbstractTranscoder) pt;
    		
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
