package org.politaktiv.portlet.plakatohr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;

import org.apache.commons.io.IOUtils;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
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
	 * @return a set of file entries or empty list on failure.
	 */
	public Set<DLFileEntry> getBackgroundPreviews(long folderId, ThemeDisplay themeDisplay)  {
		Map<DLFileEntry,DLFileEntry> map = getBackgroundPreviewsAndTemplates(folderId, themeDisplay);
		if (map == null) {
			return null;
		}
		return map.keySet();
		
	}
	
	/**
	 * Retrieves a map of file entries that serve as previews for the background images from the source folder 
	 * – each preview entry is mapped to an SVG template entry.
	 * This will only return file entries for which both a JPG and SVG version exist. JPGs and SVGs are determined
	 * by their MIME type. SVGs are looked up from the JPGs by replacing the file extension (if any) by ".svg". The
	 * comparison is case-insensitive. Any errors are logged. 
	 * @param folderId the source folder ID
	 * @param themeDisplay used by backend methods.
	 * @return a mapping from preview entries (JPG) to template entries (SVG) or an empty map on failure.
	 */
	public Map<DLFileEntry,DLFileEntry> getBackgroundPreviewsAndTemplates(long folderId, ThemeDisplay themeDisplay)  {
		
		Map<DLFileEntry,DLFileEntry> result = new HashMap<DLFileEntry, DLFileEntry>();
		
		// obtain the folder from the ID
		DLFolder dir; 
		dir = getFolder(folderId);
		if (dir == null) {
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
					result.put(jpgFile, svgFile);
					found = true;
				}
			}
			if (!found) {
				_log.error("Background previews: No SVG file found for file " + jpgTitle);
			}
			
		}
		
		return result;
		
		
	}
	
	/**
	 * Translates a folder ID into a folder name.
	 * @param folderId the folder ID to translate.
	 * @return the folder name or null in any case of error (errors are logged in detail).
	 */
	public String getFolderName(long folderId) {
		String result = null;
		
		DLFolder dir = getFolder(folderId);
		if ( dir == null) {
			return null;
		}
		
		try {
			result = dir.getName();
		} catch (Exception e) {
			_log.error("Cannot get name for folder with ID: " + folderId);
			_log.error(e);
			return(null);
		}
			
		
		return result;
		
		
	}
	
	/**
	 * Retrieves a folder object by its ID. Errors will yield null, details being logged.
	 * @param folderId the ID of the folder to revtrieve
	 * @return the folder object or null on any kind of error.
	 */
	public DLFolder getFolder(long folderId) {
		
		DLFolder result = null;
		
		try {
			result = DLFolderLocalServiceUtil.getDLFolder(folderId);
		} catch (Exception e) {
			_log.error("Illegal folder ID: " + folderId);
			_log.error(e);
			return(null);
		}
		
		return(result);
		
		
	}
	
	// FIXME: this is broken sh**
	public boolean userHasWritePermission(ThemeDisplay themeDisplay, DLFolder folder) {
		
		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();
		
		// probably wrong:
		String portletId = themeDisplay.getPortletDisplay().getId();
		
		/*
		// see https://github.com/liferay/liferay-portal/blob/master/portal-impl/src/com/liferay/portlet/documentlibrary/service/permission/DLFolderPermission.java
		String portletId = PortletProviderUtil.getPortletId(
				Folder.class.getName(), PortletProvider.Action.EDIT);
		 */
		
		
		Boolean check =  StagingPermissionUtil.hasPermission(
					permissionChecker, folder.getGroupId(), folder.getClass().getName(),
					folder.getFolderId(), portletId,  ActionKeys.ADD_DOCUMENT);
		
		if (check == null) {
			try {
				_log.debug("StagingPermissionUtil returned null for " + folder.getPath());
			} catch (Exception e) {
				_log.debug("StagingPermissionUtil returned null for folder ID" + folder.getFolderId());
				
			}
			return false;
		}
		
		return check;
		
		
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
	
	/**
	 * Uploads a file into a folder. File contents is taken from a stream.
	 * File titles must be unique in that folder, otherwise an exception will be thrown.
	 * @param targetFolderId the folder to upload data to.
	 * @param mimeType the mime type of the new file.
	 * @param title the title (displayed as name) of the new file.
	 * @param is a stream to read data from.
	 * @param request actionRequest or renderRequest
	 * @param themeDisplay the themeDisplay
	 * @throws IOException on any kind of error. 
	 */
	@Deprecated
	public void storeFile(long targetFolderId, String mimeType, String title, InputStream is, PortletRequest request, ThemeDisplay themeDisplay) throws IOException {

			storeFile(targetFolderId, mimeType, title, is, request);
		
	}

	
	
	/**
	 * Uploads a file into a folder. File contents is taken from a stream.
	 * File titles must be unique in that folder, otherwise an exception will be thrown.
	 * @param targetFolderId the folder to upload data to.
	 * @param mimeType the mime type of the new file.
	 * @param title the title (displayed as name) of the new file.
	 * @param is a stream to read data from.
	 * @param request actionRequest or renderRequest
	 * @throws IOException on any kind of error. 
	 */
	public long storeFile(long targetFolderId, String mimeType, String title, InputStream is, PortletRequest request) throws IOException {
		
		long id;
		
		byte[] bytes = IOUtils.toByteArray(is);
		long size = bytes.length;
		
		InputStream bufferIs = new ByteArrayInputStream(bytes);
		
		ServiceContext serviceContext;
		try {
			serviceContext = ServiceContextFactory.getInstance(DLFileEntry.class.getName(), request);
			// TODO: file name null geht irgendwie nicht gut? Preview generator macht Quatsch!
			FileEntry file = DLAppServiceUtil.addFileEntry(getThemeDisplay(request).getScopeGroupId(), targetFolderId, null, mimeType, 
					title, "", "", bufferIs,  size, serviceContext);
			id = file.getFileEntryId();
		} catch (Exception e) {
			_log.error(e);
			throw new IOException(e);
		}
		
		return id;
		
	}

	public DLFileEntry getDLFileByTitle(long folderID, String title) throws SystemException {
		List<DLFileEntry> listOfFiles = DLFileEntryLocalServiceUtil.getFileEntries(folderID, title);
		DLFileEntry result = null;
		for (DLFileEntry file : listOfFiles) {
			if(file.getName() == title) {
				result = file;
			}
		}
		return result;
	}
	
	
	private ThemeDisplay getThemeDisplay(PortletRequest request) {
		return (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
	}
	

}
