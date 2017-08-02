package org.politaktiv.svgmanipulator;

import org.w3c.dom.Element;

/**
 * Contains the size of a rectangle from SVG. The definition of the dimension is in Strings
 * since it could contain units other than pixels.
 *
 */
public class svgRectSize {
	
	private String x;
	private String y;
	private String width;
	private String height;
	
	
	
	public svgRectSize(String x, String y, String width, String height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public svgRectSize(Element rect) {
		this(rect.getAttribute("x"),
				rect.getAttribute("y"),
				rect.getAttribute("width"),
				rect.getAttribute("height"));
	}
	
	
	public String getX() {
		return x;
	}
	public String getY() {
		return y;
	}
	public String getWidth() {
		return width;
	}
	public String getHeight() {
		return height;
	}
	

	
	public String toString() {
		return("x: " + x + ", y: "+ y + ", width: " + width + ", height: " + height); 
	}
	

}
