package org.politaktiv.svgmanipulator;


import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.politaktiv.svgmanipulator.util.base64Encoder;


public class TesterApp {

	public static void main(String[] args) throws IOException  {

		File fXmlFile = new File(args[0]);
		File jpgFile = new File(args[1]);
		
		String imgBase64 = base64Encoder.getBase64svg(jpgFile);
		
	
		SvgManipulator manipulator = new SvgManipulator(fXmlFile);
		
		Set<String> fieldNames = manipulator.getAllFieldNames();
		for ( String fN : fieldNames) {
			System.err.println(fN + " - " + manipulator.getSizeOfFlowPara(fN));
		}
		
		
		manipulator.replaceTextAll("MEINUNG", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
		manipulator.replaceTextAll("NAME", "Christoph Citrone");
		manipulator.replaceFlowParaByImage("FOTO", imgBase64);
		
		manipulator.setSvgVersion("1.2");
		manipulator.convertFlowInkscapeToBatik();
		manipulator.convertCssInkscapeToBatik();
		
		
		//System.out.println(manipulator.getSvgAsXml());
		String newSvgData = manipulator.getSvgAsXml();
		
		SvgConverter converter = new SvgConverter(newSvgData);
		converter.generateOutput(new File("/tmp/test.jpg"), SvgConverter.JPG);
		converter.generateOutput(new File("/tmp/test2.pdf"), SvgConverter.PDF);
		//converter.generateOutput(new File("/tmp/test.png"), SvgConverter.PNG);
		converter.generateOutput(new File("/tmp/test.svg"), SvgConverter.SVG);


	}	
		
}
