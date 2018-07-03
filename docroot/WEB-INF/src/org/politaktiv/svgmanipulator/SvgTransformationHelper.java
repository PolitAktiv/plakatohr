package org.politaktiv.svgmanipulator;

/**
 * Helper class for computing transformations on SVG elements
 * @author but
 *
 */
public class SvgTransformationHelper {
	
	private static double roundToSvg(double value) {
		
		return Math.round(value*10000)/10000.0;
		
	}
	
	
	private static double rotationCoord(double coordinate, double size) {
		
		return (size / 2.0) + coordinate;
		
	}
	
	public static String rotateAroundCenter(double angle, svgRectSize coordinates) {
		
		double Xr = rotationCoord(coordinates.getXNum(), coordinates.getWidthNum());
		double Yr = rotationCoord(coordinates.getYNum(), coordinates.getHeightNum());
		
		return "rotate(" + angle + ", " + roundToSvg(Xr) + ", " + roundToSvg(Yr) + ")";
		
	}
	
	public static String transformFlipVertical(svgRectSize coordinates) {
		
		return flip(-1,1, coordinates);
		
	}

	
	public static String transformFlipHorizonal(svgRectSize coordinates) {

		return flip(1,-1, coordinates);
		
	}
	
	private static String flip(int Fx, int Fy, svgRectSize coordinates) {
		
		if ( ! ( Fx == -1 || Fx == 1  ) ) {
			throw new RuntimeException("Illegal argument Fx = " + Fx);
		}

		if ( ! ( Fy == -1 || Fy == 1  ) ) {
			throw new RuntimeException("Illegal argument Fy = " + Fy);
		}
		
		
		double Xr = rotationCoord(coordinates.getXNum(), coordinates.getWidthNum());
		double Yr = rotationCoord(coordinates.getYNum(), coordinates.getHeightNum());
		
		double Xt = -1*(Fx -1) * Xr;
		double Yt = -1*(Fy -1) * Yr;
		
		if (Xt == 0.0 && Yt == 0.0) {
			return "";
		}
		
		return "translate("+ roundToSvg(Xt) + "," + roundToSvg(Yt)  +") scale(" + Fx + "," + Fy + ")";
		
		
	}
	
	public static String transformToJpegOrientation(int orientation, svgRectSize coordinates) throws IllegalJpegOrientation {
		
		switch(orientation) {
		case 1: 
			return "";
		case 2:
			return flip(-1,1, coordinates);
		case 3:
			return flip(-1,-1, coordinates);
		case 4:
			return flip(1,-1, coordinates);
		case 5: 
			return flip(1,-1, coordinates) + " " + rotateAroundCenter(90, coordinates);
		case 6:
			return rotateAroundCenter(90, coordinates);
		case 7: 	
			return flip(1,-1, coordinates) + " " + rotateAroundCenter(270, coordinates);
		case 8:
			return rotateAroundCenter(270, coordinates);
		}
		
		throw new IllegalAccessError("Illegal JPEG orientation: " + orientation);
		
		
	}

	

}
