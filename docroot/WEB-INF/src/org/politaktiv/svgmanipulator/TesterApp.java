package org.politaktiv.svgmanipulator;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.politaktiv.svgmanipulator.util.IllegalJpegOrientation;
import org.politaktiv.svgmanipulator.util.ImageRotator;
import org.politaktiv.svgmanipulator.util.MimeTypeException;
import org.politaktiv.svgmanipulator.util.base64Encoder;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;


public class TesterApp {

	public static void main(String[] args) throws IOException, MimeTypeException, IllegalJpegOrientation, ImageProcessingException, MetadataException  {

		File fXmlFile = new File(args[0]);
		File jpgFile = new File(args[1]);
		
		
		// Testing image rotation -- also reads in the JPEG file
		BufferedImage img = ImageRotator.correctOrientation(jpgFile);
		
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", bOut);
		ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
		
		// for testing: store image to file
		FileOutputStream imgOut = new FileOutputStream(new File("/tmp/test-rotated.jpg"));
		imgOut.write(bOut.toByteArray());
		imgOut.close();
		
		// store into base64
		String imgBase64 = base64Encoder.getBase64svg(bIn);
		
		// SVG Manipulator Testing
		
	
		SvgManipulator manipulator = new SvgManipulator(fXmlFile);
		
		Set<String> fieldNames = manipulator.getAllFieldNames();
		for ( String fN : fieldNames) {
			System.err.println(fN + " - " + manipulator.getSizeOfFlowPara(fN) ); 
		}
		
		System.err.println("Title field: " +manipulator.getSvgTitle());
		
		manipulator.replaceTextAll("MEINUNG", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
		manipulator.replaceTextAll("NAME", "Citrone");
		manipulator.replaceFlowParaByImage("FOTO", imgBase64);
		
		manipulator.setSvgVersion("1.2");
		manipulator.convertFlowInkscapeToBatik();
		manipulator.convertCssInkscapeToBatik();
		
		
		//System.out.println(manipulator.getSvgAsXml());
		String newSvgData = manipulator.getSvgAsXml();
		
		SvgConverter converter = new SvgConverter(newSvgData);
		converter.generateOutput(new File("/tmp/test.jpg"), SvgConverter.JPG);
		converter.generateOutput(new File("/tmp/test.pdf"), SvgConverter.PDF);
		//converter.generateOutput(new File("/tmp/test.png"), SvgConverter.PNG);
		//converter.generateOutput(new File("/tmp/test.svg"), SvgConverter.SVG_PRETTY_XML);
		converter.generateOutput(new File("/tmp/test.svg"), SvgConverter.SVG);


	}	
		
}
