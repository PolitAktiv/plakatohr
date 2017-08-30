package org.politaktiv.portlet.plakatohr.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

/**
 * Helper class for all kinds of interaction with LifeRay's file/media library. Needs to be instantiated
 * so that the logging works.
 */
public class OhrMediaHelper {
	
	private static Log _log;
	
	public OhrMediaHelper() {
		_log = LogFactoryUtil.getLog(OhrMediaHelper.class);
	}
	
	/**
	 * Retrieves a list of file entries that serve as previews for the background images from the source folder.
	 * This will only return file entries for which both a JPG and SVG version exist. JPGs and SVGs are determined
	 * by their MIME type. SVGs are looked up from the JPGs by replacing the file extension (if any) by ".svg". The
	 * comparison is case-insensitive. Any errors are logged. 
	 * @param folderId the source folder ID
	 * @param themeDisplay used by backend methods.
	 * @return a list of file entries or empty list on failure.
	 */
	public List<DLFileEntry> getBackgroundPreviews(long folderId, ThemeDisplay themeDisplay)  {
		
		List<DLFileEntry> result = new LinkedList<DLFileEntry>();
		
		
		// obtain the folder from the ID
		DLFolder dir; 
		try {
			dir = DLFolderLocalServiceUtil.getDLFolder(folderId);
		} catch (Exception e) {
			_log.error("Illegal source folder ID: " + folderId);
			_log.error(e);
			return result;
		}
		
		// get folder entries
		List<DLFileEntry> dlFileEntries;
		try {
			dlFileEntries = DLFileEntryLocalServiceUtil.getFileEntries(themeDisplay.getScopeGroupId(), dir.getFolderId());
		} catch (SystemException e) {
			_log.error("Cannot retrieve entries from folder: " + folderId);
			_log.error(e);
			return result;
		}
		
		try {
			_log.debug("Scanning source folder: " + dir.getPath());
		} catch (Exception e) {
			_log.error("Cannot retrieve source folder path, perhaps not a problem?");
			_log.error(e);
		} 

		// filter entries: collect all JPGs and SVGs
		LinkedList<DLFileEntry> jpgEntries = new LinkedList<DLFileEntry>();
		LinkedList<DLFileEntry> svgEntries = new LinkedList<DLFileEntry>();
		for (DLFileEntry entry : dlFileEntries) {
			if (entry.getMimeType().toLowerCase().equals("image/jpeg")) {
				jpgEntries.add(entry);
			}
			if (entry.getMimeType().toLowerCase().equals("image/svg+xml")) {
				svgEntries.add(entry);
			}
			_log.debug("Folder entry: " + entry.getTitle() + " " + entry.getMimeType());
		}
		
		// now check for JPG files that have a corresponding SVG file
		for ( DLFileEntry jpgFile : jpgEntries) {
			String jpgTitle = jpgFile.getTitle();
			String jpgExt = jpgFile.getExtension();
			if (jpgExt.length() > 0) {
				jpgExt = "." + jpgExt;
			}
			String jpgBaseName = jpgTitle.substring(0, jpgTitle.length()-jpgExt.length());
			String svgName = jpgBaseName + ".svg";
			boolean found = false;
			for ( DLFileEntry svgFile : svgEntries ) {
				if (svgFile.getTitle().toLowerCase().equals(svgName.toLowerCase())) {
					result.add(jpgFile);
					found = true;
				}
			}
			if (!found) {
				_log.error("Background previews: No SVG file found for file " + jpgTitle);
			}
			
		}
		
		Collections.sort(result);
		
		return result;
		
		
	}
	
	/**
	 * Translates a folder ID into a folder name.
	 * @param folderId the folder ID to translate.
	 * @return the folder name or null in any case of error (errors are logged in detail).
	 */
	public String getFolderName(long folderId) {
		String result = "";
		
		try {
			DLFolder dir = DLFolderLocalServiceUtil.getDLFolder(folderId);
			result= dir.getName();
			//result = dir.getPath();
		} catch (Exception e) {
			_log.error("Illegal folder ID: " + folderId);
			_log.error(e);
			return(null);
		}
		
		return(result);
	
		
		
	}
	
	/**
	 * Get the public URL for a file entry in the media library.
	 * @param themeDisplay needed by service methods.
	 * @param fileEntry the file entry to find the URL for
	 * @return the public URL for that file entry or null in any case of error (errors are logged in detail).
	 */
	public URL getDlFileUrl(ThemeDisplay themeDisplay, DLFileEntry fileEntry) {
		
		String url = themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/" + themeDisplay.getScopeGroupId() + StringPool.SLASH + fileEntry.getUuid();
		
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			_log.error(e);
			return null;
		}
		
	}
	
	

}
