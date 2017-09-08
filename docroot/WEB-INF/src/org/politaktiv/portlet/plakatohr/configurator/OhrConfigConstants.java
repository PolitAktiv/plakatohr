package org.politaktiv.portlet.plakatohr.configurator;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Class capturing string field names used for the preferences of the PlakatOhR.
 *
 */
public class OhrConfigConstants {
	
	public static String SOURCE_FOLDER_ID="sourceFolderId";
	public static String TARGET_FOLDER_ID="targetFolderId";
	public static String E_MAIL_RECIPIENT="eMailRecipient";
	public static String E_MAIL_SUBJECT="eMailSubject";
	
	
	/**
	 * @return a list of all the field values managed by this configuration constant class. 
	 * @throws IllegalArgumentException in case of some weird Java reflection error that should not occur.
	 * @throws IllegalAccessException in case of some weird Java reflection error that should not occur.
	 */
	public static List<String> getFieldValues() throws IllegalArgumentException, IllegalAccessException {
		
		List<String> result = new LinkedList<String>();
		
	    // fancy hack: use reflection to walk through all config constants and get them from the request, store
	    // them into the preferences
	    for (Field f : OhrConfigConstants.class.getDeclaredFields()) {
	        result.add( (String) f.get(OhrConfigConstants.class) );
	    }
	    
	    return result;
		
	}
	

}
