package org.politaktiv.portlet.plakatohr.controller;

import java.net.MalformedURLException;
import java.net.URL;
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

public class MediaHelper {
	
	private static Log _log;
	
	public MediaHelper() {
		_log = LogFactoryUtil.getLog(MediaHelper.class);
	}
	
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

		// filter entries
		for (DLFileEntry entry : dlFileEntries) {
			// TODO: check for SVGs!
			if (entry.getMimeType().toLowerCase().equals("image/jpeg")) {
				result.add(entry);
			}
		}
		
		
		return result;
		
		
	}
	
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
