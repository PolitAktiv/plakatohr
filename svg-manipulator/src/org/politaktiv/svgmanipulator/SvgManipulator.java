package org.politaktiv.svgmanipulator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.politaktiv.svgmanipulator.util.EscapeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList; 

public class SvgManipulator {
	
	private Document doc;
	
	/**
	 * Create a new SvgManipulator from a given SVG File. This will parse the file right away.
	 * @param svgFile the file to parse and manipulate
	 * @throws IOException if anything goes wrong during parsing or IO.
	 */
	public SvgManipulator(File svgFile) throws IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(svgFile);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Creates a new SvgManipulator from a given DOM. Assuming the DOM contains SVG. 
	 * @param document to use for this SvgManipulator
	 */
	public SvgManipulator(Document document) {
		this.doc = document.cloneNode(true).getOwnerDocument();
	}
	

	/**
	 * Create a new SvgManipulator from a given SVG File. This will parse the file right away.
	 * @param inStream the stream to read in XML from to parse and manipulate
	 * @throws IOException if anything goes wrong during parsing or IO.
	 */
	public SvgManipulator(InputStream inStream) throws IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inStream);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	
	/**
	 * 
	 * @return the SVG contained in this SvgManipulator as XML in a string
	 * @throws IOException if anything goes wrong while transforming the underlying DOM to XML
	 */
	public String getSvgAsXml() throws IOException {
	     DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		} catch (Exception e) {
			throw new IOException(e);
		}
	       
	   return(writer.toString());
		
	}
	
	/**
	 * 
	 * @return the SVG contained in this SvgManipulator as a (deep) clone of its internal DOM structure. 
	 */
	public Node getSvgAsDom() {
		return doc.cloneNode(true);
	}
	
	public boolean setSvgVersion(String version) {
		NodeList svgRoots = doc.getElementsByTagName("svg");
		if ( svgRoots.getLength() != 1 || ! (svgRoots.item(0) instanceof Element )) {
			return false;
		}
		
		((Element)svgRoots.item(0)).setAttribute("version", version);
		
		return true;
		
	}
	
	/**
	 * Find all field names from FlowParas, that is all text contents wrapped in $$ contained in FlowParas 
	 * @return the field names
	 */
	public Set<String> getFlowParaFieldNames() {
		HashSet<String> result = new HashSet<String>();
		
		NodeList nodes;
		try {
			nodes = getAllFlowParas();
		} catch (XPathExpressionException e) {
			return result;
		}
		
		Pattern r = Pattern.compile("\\$\\$(\\w+)\\$\\$");
		
		// loop over flowParas and check for text content with $$something$$
		for (int i = 0; i < nodes.getLength(); ++i) {
			String text = nodes.item(i).getTextContent();
			Matcher m = r.matcher(text);
			if ( m.matches()) {
				result.add(m.group(1));
			}	
		}		
		
		return result;
		
	}
	
	private NodeList getAllFlowParas() throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		
		nodes = (NodeList)xPath.evaluate("//flowPara",
		        doc.getDocumentElement(), XPathConstants.NODESET);
		return nodes;
	}
	
	
	private NodeList getFlowParasByFieldName(String name) throws XPathExpressionException {

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		
		// find flowPara node using xPath
		nodes = (NodeList)xPath.evaluate("//flowPara[contains(text(), " +
				EscapeUtil.escapeXpath("$$" + name + "$$") +
				 ")]",
		        doc.getDocumentElement(), XPathConstants.NODESET);

		return nodes;
		
	}
	
	
	
	
	/**
	 * Replace the text of any flowPara SVG node that contains a certain string
	 * @param text the string to search for/replace, $$ will be added before and after.
	 * @param replacement the replacement for that string.
	 * @return The number of flowPara texts replaced
	 */
	public int replaceTextInParagraph(String text, String replacement) {
		int replaced = 0;
		String replacementEscaped = EscapeUtil.escapeXml(replacement);
		
		NodeList nodes;
		
		// find flowPara node using xPath
		try {
			nodes = getFlowParasByFieldName(text);
		} catch (XPathExpressionException e) {
			// If in any case the expression should fail, return false
			return 0;
		}
		
		// replace the text in the nodes found
		for (int i = 0; i < nodes.getLength(); ++i) {
			nodes.item(i).setTextContent(replacementEscaped);
			replaced++;
		}		
		
		return replaced;
		
	}
	
	/**
	 * Checks all style attributes in the SVG/XML and does some manipulations to them so that
	 * Batik does not stumble over CSS it cannot understand.
	 * @return the number of style attribute occurences that have been mended.
	 */
	public int convertCssInkscapeToBatik() {

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		
		// find flowPara node using xPath
		try {
			nodes = (NodeList)xPath.evaluate("//*[@style]",
			        doc.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			return 0;
		}
		
		
		int counter = 0;
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node n = nodes.item(i);
			if (n instanceof Element) {
				Element e = (Element)n;
				String style = e.getAttribute("style");
				
				// replace evil stuff
				style = style.replace("text-align:center", "text-align:middle");
				
				// store back
				e.setAttribute("style", style);
				counter++;
				
			}
		}	
		
		return counter;
		
	}
	
	/**
	 * Convert Inkscape's flowRoot version into something that Batik can actually understand. Also does some mending
	 * to CSS styles.
	 * Works only for a single shape of rect for now, not for other forms of floating elements.
	 * @return number of times this has been applied
	 */
	public int convertFlowInkscapeToBatik() {
		
		NodeList nodes;
		int counter = 0;
		
		// find flowPara node using xPath
		try {
			nodes = getAllFlowParas();
		} catch (XPathExpressionException e) {
			// If in any case the expression should fail, return false
			return 0;
		}
		
		
		for (int i = 0; i < nodes.getLength(); ++i) {
			// check if the parent is a flowRoot, because this is the problematic case caused by Inkscape
			Node flowPara = nodes.item(i);
			Element flowRoot= (Element)flowPara.getParentNode();
			if (! flowPara.getParentNode().getNodeName().equals("flowRoot") || 
					! ( flowPara.getParentNode() instanceof Element) ) {
				continue;
			}
			// dive down to first flow region
			NodeList flowRegions = flowRoot.getElementsByTagName("flowRegion");
			if ( flowRegions.getLength() != 1 || ! (flowRegions.item(0) instanceof Element )) {
				continue;
			}
			
			// dive down to rect
			Element flowRegion = (Element)flowRegions.item(0);
			NodeList rects = flowRegion.getElementsByTagName("rect");
			if ( rects.getLength() != 1 || ! ( rects.item(0) instanceof Element ) ) {
				continue;
			}
			Element rect = (Element)rects.item(0);
			// set rect to invisible
			rect.setAttribute("visibility", "hidden");
			
			// insert the flowPara into a flowDiv, it will be removed from flowRoot by doing so.
			Element flowDiv = doc.createElement("flowDiv");
		    flowDiv.appendChild(flowPara);
			flowRoot.appendChild(flowDiv);
			
			// delete style from FlowRoot since Inkscape will store it in FlowPara redundantly, creating a mess for Batik
			flowRoot.removeAttribute("style");
			
			counter++;
			
		}
		
		return counter;
		
	}
	
	/**
	 * Finds the size of a flowPara as itendified by its contained field name such als $$PHOTO$$, while $$ are added
	 * automatically.
	 * @param fieldName the name of the field in the contents
	 * @return the size of the first flowPara or null if no unique FlowPara with unique rectangle inside found by that field name.
	 */
	public svgRectSize getSizeOfFlowPara(String fieldName) {

		NodeList nodes;
		// find flowPara node using xPath
		try {
			nodes = getFlowParasByFieldName(fieldName);
		} catch (XPathExpressionException e) {
			// If in any case the expression should fail, return false
			return null;
		}
		if (nodes.getLength() < 1) {
			return null;
		}
		
		Node flowPara = nodes.item(0);

		// climb up until you hit the flow root
		// (will be direct parent for Inkscape SVG, something else for batik-compatible SVG, most likely flowDiv will 
		// be in between there
		Element flowRoot = null;
		do  {
			flowRoot= (Element)flowPara.getParentNode();
		} while ( (! "flowRoot".equals(flowRoot.getNodeName())) && flowRoot  != null );
		if (flowRoot == null) {
			// actually nothing found or root node reached, try next one
			return null;
		}
			
		// dive down to first flow region
		NodeList flowRegions = flowRoot.getElementsByTagName("flowRegion");
		if ( flowRegions.getLength() != 1 || ! (flowRegions.item(0) instanceof Element )) {
			return null;
		}

		// dive down to rect
		Element flowRegion = (Element)flowRegions.item(0);
		NodeList rects = flowRegion.getElementsByTagName("rect");
		if ( rects.getLength() != 1 || ! ( rects.item(0) instanceof Element ) ) {
			return null;
		}
		Element rect = (Element)rects.item(0);
		
		return new svgRectSize(rect);
	}
	
	
	/**
	 * Replace any flowPara SVG node that contains a certain string by an image.
	 * Attention: Supports only first flowRegion, so a text flowing into multiple elements will not be supported
	 * @param text the text contents of the flowPara that is to be replaced.
	 * @param imageHref Link to the Image or base64 encoded data
	 * @return The number of flowParas replaced by images
	 */
	public int replaceTextInParagraphByImage(String text, String imageHref) {
		
		int replaced = 0;
		
		NodeList nodes;
		
		// find flowPara node using xPath
		try {
			nodes = getFlowParasByFieldName(text);
		} catch (XPathExpressionException e) {
			// If in any case the expression should fail, return false
			return 0;
		}
		
		
		
		// replace the text in the nodes found
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node flowPara = nodes.item(i);

			// climb up until you hit the flow root
			// (will be direct parent for Inkscape SVG, something else for batik-compatible SVG, most likely flowDiv will 
			// be in between there
			Element flowRoot = null;
			do  {
				flowRoot= (Element)flowPara.getParentNode();
			} while ( (! "flowRoot".equals(flowRoot.getNodeName())) && flowRoot  != null );
			if (flowRoot == null) {
				// actually nothing found or root node reached, try next one
				continue;
			}
			
			// store transform attribute since it might change the appearance
			String transformAtt = flowRoot.getAttribute("transform");
			
			// dive down to first flow region
			NodeList flowRegions = flowRoot.getElementsByTagName("flowRegion");
			if ( flowRegions.getLength() != 1 || ! (flowRegions.item(0) instanceof Element )) {
				continue;
			}
			
			// dive down to rect
			Element flowRegion = (Element)flowRegions.item(0);
			NodeList rects = flowRegion.getElementsByTagName("rect");
			if ( rects.getLength() != 1 || ! ( rects.item(0) instanceof Element ) ) {
				continue;
			}
			Element rect = (Element)rects.item(0);
			
			// copy information from <rect> into an <image>
			Element newImage = doc.createElement("image");
			newImage.setAttribute("x", rect.getAttribute("x"));
			newImage.setAttribute("y", rect.getAttribute("y"));
			newImage.setAttribute("height", rect.getAttribute("height"));
			newImage.setAttribute("width", rect.getAttribute("width"));
			
			// insert transform attribute if it was present
			if (! "".equals(transformAtt)) {
				newImage.setAttribute("transform", transformAtt);
				//System.err.println(transformAtt);
			}
			
			// TODO: Escape here?
			newImage.setAttribute("xlink:href", imageHref);
			
			// replace the flowRoot by the image
			flowRoot.getParentNode().replaceChild(newImage, flowRoot);
			
			
			replaced++;
		}		
		
		return replaced;		
		
		
	}
	
	

}
