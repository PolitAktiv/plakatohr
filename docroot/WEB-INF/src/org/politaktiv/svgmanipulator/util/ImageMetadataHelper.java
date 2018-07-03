package org.politaktiv.svgmanipulator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * Helper class for reading metadata information from image files/streams
 * @author but
 *
 */
public class ImageMetadataHelper {
	
	public static int getOrientation(InputStream inStream) throws IOException {
		
		int orientation =1;
		
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(inStream);
			
			 Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			 if (directory != null) {
				 orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			 }	 
		} catch (ImageProcessingException e) {
			// do nothing, assume default
		} catch (MetadataException e) {
			// do nothing, assume default
		}
		
		return orientation;
		
	}
	
	
	public static int getOrientation(File inputFile) throws IOException {
		return getOrientation(new FileInputStream(inputFile));
		
	}
	

}
