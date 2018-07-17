package org.politaktiv.svgmanipulator.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * Helper class that rotates bitmap images according to JPEG/EXIF orientation values. This
 * code is taken and adapted from https://code.i-harness.com/de/q/5a1dcc
 * @author otn
 *
 */
public class ImageRotator {
	
	public static BufferedImage correctOrientation(File inFile) throws ImageProcessingException, IOException, MetadataException, IllegalJpegOrientation {
		FileInputStream inS =new FileInputStream(inFile);
		BufferedImage result= correctOrientation(inS);
		inS.close();
		return result;
	}
	
	
	/**
	 * Checks the orientation of the image and corrects it if necessary.
	 * <p>If the orientation of the image does not need to be corrected, no operation will be performed.</p>
	 * @param inputStream
	 * @return
	 * @throws ImageProcessingException
	 * @throws IOException
	 * @throws MetadataException
	 * @throws IllegalJpegOrientation 
	 */
	public static BufferedImage correctOrientation(InputStream inputStream) throws ImageProcessingException, IOException, MetadataException, IllegalJpegOrientation {
		
		// buffer the input stream into a byte array
		byte[] dataBuffer = StreamSlurper.read(inputStream);
		
        // Create a buffered image from the input stream
        BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(dataBuffer));
        if (bimg == null) {
        	throw new IOException("ImageIO.read() returned null.");
        }		
		
        // are there metadate we can use?
	    Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(dataBuffer));
	    if(metadata != null && metadata.containsDirectoryOfType(ExifIFD0Directory.class)) {
	            // Get the current orientation of the image
	            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
	            int orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);




	            // Get the current width and height of the image
	            int[] imageSize = {bimg.getWidth(), bimg.getHeight()};
	            int width = imageSize[0];
	            int height = imageSize[1];

	            // Determine which correction is needed
	            AffineTransform t = new AffineTransform();
	            switch(orientation) {
	            case 1:
	                // no correction necessary skip and return the image
	                return bimg;
	            case 2: // Flip X
	                t.scale(-1.0, 1.0);
	                t.translate(-width, 0);
	                return transform(bimg, t, false);
	            case 3: // PI rotation 
	                t.translate(width, height);
	                t.rotate(Math.PI);
	                return transform(bimg, t, false);
	            case 4: // Flip Y
	                t.scale(1.0, -1.0);
	                t.translate(0, -height);
	                return transform(bimg, t, false);
	            case 5: // - PI/2 and Flip X
	                t.rotate(-Math.PI / 2);
	                t.scale(-1.0, 1.0);
	                return transform(bimg, t,true);
	            case 6: // -PI/2 and -width
	                t.translate(height, 0);
	                t.rotate(Math.PI / 2);
	                return transform(bimg, t,true);
	            case 7: // PI/2 and Flip
	                t.scale(-1.0, 1.0);
	                t.translate(height, 0);
	                t.translate(0, width);
	                t.rotate(  3 * Math.PI / 2);
	                return transform(bimg, t,true);
	            case 8: // PI / 2
	                t.translate(0, width);
	                t.rotate(  3 * Math.PI / 2);
	                return transform(bimg, t,true);
	            }
	            // if this point is reached, the metadata orientation value was something else
	            throw new IllegalJpegOrientation("Illegal JPEG/EXIF orientation value: " + orientation);
	            
	    } else {
	    	// no usable metadata, return image directly
	    	return bimg;
	    	
	    }

	}

	/**
	 * Performs the tranformation
	 * @param bimage
	 * @param transform
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage transform(BufferedImage bimage, AffineTransform transform, boolean swapXY) throws IOException {
	    // Create an transformation operation
	    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);

	    // Create an instance of the resulting image, with the same width, height and image type than the referenced one
	    BufferedImage destinationImage;
	    if (swapXY) {
	    	destinationImage = new BufferedImage( bimage.getHeight(),bimage.getWidth() , bimage.getType() );
	    } else {
	    	destinationImage = new BufferedImage( bimage.getWidth(), bimage.getHeight(), bimage.getType() );
	    }
	    op.filter(bimage, destinationImage);

	   return destinationImage;
	}
	
	
}
