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
	
	public void swapWidhtHeight() {
		String h= this.height;
		String w= this.width;
		this.width = h;
		this.height = w;
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
	
	public Double getHeightNum() {
		try {
			return Double.parseDouble(getHeight().replaceAll("\\D+$", ""));
		} catch (NumberFormatException e) {
			return null;
		}			
	}
	
	public Double getWidthNum() {
		try {
			return Double.parseDouble(getWidth().replaceAll("\\D+$", ""));
		} catch (NumberFormatException e) {
			return null;
		}			
	}	

	public Double getXNum() {
		try {
			return Double.parseDouble(getX().replaceAll("\\D+$", ""));
		} catch (NumberFormatException e) {
			return null;
		}			
	}	

	public Double getYNum() {
		try {
			return Double.parseDouble(getY().replaceAll("\\D+$", ""));
		} catch (NumberFormatException e) {
			return null;
		}			
	}	

	
	/**
	 * Determines the aspect-ratio of this {@link svgRectSize}. May return null if this fails. 
	 * Mixed units (e.g., cm and px) in the dimensions of the rect will cause weird effects.
	 * @return the aspect ratio or null.
	 */
	public Double getAspetcRatio() {
		
		
		String wNum = getWidth().replaceAll("\\D+$", "");
		String hNum = getHeight().replaceAll("\\D+$", "");	
		
		Double result = null;
		try {
			result = Double.parseDouble(wNum) / Double.parseDouble(hNum);
		} catch (NumberFormatException e) {
			// empty catch block, resulting returning null
		}
		
		return result;
		
		
		
	}
	

	
	public String toString() {
		return("x: " + x + ", y: "+ y + ", width: " + width + ", height: " + height + ", aspect ratio: " + getAspetcRatio()); 
	}
	

}
