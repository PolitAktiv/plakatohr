package org.politaktiv.portlet.plakatohr.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import org.politaktiv.portlet.plakatohr.configurator.OhrConfigConstants;
import org.politaktiv.svgmanipulator.SvgConverter;
import org.politaktiv.svgmanipulator.SvgManipulator;
import org.politaktiv.svgmanipulator.util.base64Encoder;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/*
 * This class is the main controller for the portlet managing incoming events from the frontend.
 * It also uses the configuration file to use project specific data.
 */
public class PlakatohrPortlet extends MVCPortlet {

	public PlakatohrPortlet() {

	}

	/**
	 * 
	 * @param request
	 * @param reponse
	 */
	public void userDataSubmit(ActionRequest request, ActionResponse reponse) {
		
		UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(request);
		String firstname = ParamUtil.getString(uploadPortletRequest, "firstname");
		String lastname = ParamUtil.getString(uploadPortletRequest, "lastname");
		String email = ParamUtil.getString(uploadPortletRequest, "email");
		String oppinion = ParamUtil.getString(uploadPortletRequest, "oppinion");

		

		//String fileName = uploadPortletRequest.getFileName("picture");
		File file = uploadPortletRequest.getFile("picture");
		//String mimeType = uploadPortletRequest.getContentType("picture");
		//String title = fileName;
		//String description = "This file is added via programatically";
		//long repositoryId = themeDisplay.getScopeGroupId();
		try {
			//long targetFolderID = Long.parseLong(OhrConfigConstants.TARGET_FOLDER_ID);
			//DLFolder folder = media.getFolder(targetFolderID);
			//ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFileEntry.class.getName(), request);
			InputStream inStream = new FileInputStream(file);
			//DLAppServiceUtil.addFileEntry(repositoryId, folder.getFolderId(), fileName, mimeType, title, description, "", is, file.getTotalSpace(), serviceContext);

		System.out.println("====> " + firstname + " " + lastname + ", " + email + ", " + oppinion);

		System.out.println("Starting manipulation");
		// Has to be changed to the user selection
		//String svgFile = "Grüne Wiese.svg";
		
		OhrMediaHelper media = new OhrMediaHelper();
		PortletPreferences portletPreferences = request.getPreferences();
		Long sourceDirectoryID = GetterUtil.getLong(
				portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);		
		
		Map<DLFileEntry, DLFileEntry> previewTemplateMap = media.getBackgroundPreviewsAndTemplates(sourceDirectoryID, (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));
		DLFileEntry einTemplate = previewTemplateMap.values().iterator().next(); // TODO das ist per se kaputt ;-)
		
		
		startManipulation(firstname, lastname, oppinion, inStream, einTemplate, request);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Start the manipulation of a SVG file with a SvgManipulator and the given
	 * data from the
	 * 
	 * @param firstname
	 * @param lastname
	 * @param oppinion
	 * @param imagePath
	 * @param svgPath
	 */
	private void startManipulation(String firstname, String lastname, String oppinion, InputStream inStream,
			DLFileEntry svgFile, ActionRequest request) {

		try {
			/*
			long sourceFolderId = GetterUtil.getLong(
					portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			long targetFolderId = GetterUtil.getLong(
					portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
			    
			
			System.out.println(OhrConfigConstants.TARGET_FOLDER_ID);
			Long outputDirectoryID = Long.parseLong(OhrConfigConstants.TARGET_FOLDER_ID.trim(), 10);
			System.out.println(outputDirectoryID);
			String outputDirectoryPath = DLFolderLocalServiceUtil.getFolder(outputDirectoryID).getPath();
			System.out.println(outputDirectoryPath);
			*/
			
			OhrMediaHelper media = new OhrMediaHelper();
			
			PortletPreferences portletPreferences = request.getPreferences();
			
			/*
			Long sourceDirectoryID = GetterUtil.getLong(
					portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
			String sourceDirectoryPath = DLFolderLocalServiceUtil.getFolder(sourceDirectoryID).getPath();*/
			
			Long targetDirectoryID = GetterUtil.getLong(
					portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
			
			/*
			Long sourceDirectoryID = Long.parseLong("357443", 10);
			System.out.println("Source:" + sourceDirectoryID);
			String sourceDirectoryPath = DLFolderLocalServiceUtil.getFolder(sourceDirectoryID).getPath();
			*/
			
			//Long targetDirectoryID = Long.parseLong("357446", 10);
			System.out.println("Target: " + targetDirectoryID);
			System.out.println("Target Path: " + DLFolderLocalServiceUtil.getFolder(targetDirectoryID).getPath());
			//System.out.println("Source: " + sourceDirectoryID);

			//File svgFile = new File(sourceDirectoryPath + "/" + svgFilename);
			
			
			
			//InputStream svgInputStream = new FileInputStream(sourceDirectoryPath + "/" + svgFilename);
			InputStream svgInputStream = svgFile.getContentStream();
			
			System.out.println("Source File:" + svgFile.getTitle());

			String imgBase64 = base64Encoder.getBase64svg(inStream);

			SvgManipulator manipulator = new SvgManipulator(svgInputStream);

			Set<String> fieldNames = manipulator.getAllFieldNames();
			for (String fN : fieldNames) {
				System.err.println(fN + " - " + manipulator.getSizeOfFlowPara(fN));
			}

			manipulator.replaceTextAll("MEINUNG", oppinion);
			manipulator.replaceTextAll("NAME", firstname + " " + lastname);
			manipulator.replaceFlowParaByImage("FOTO", imgBase64);

			manipulator.setSvgVersion("1.2");
			manipulator.convertFlowInkscapeToBatik();
			manipulator.convertCssInkscapeToBatik();

			// System.out.println(manipulator.getSvgAsXml());
			String newSvgData = manipulator.getSvgAsXml();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
					
			SvgConverter converter = new SvgConverter(newSvgData);
			converter.generateOutput(os, SvgConverter.JPG);
			//converter.generateOutput(new File(outputDirectoryPath + "/test.jpg"), SvgConverter.JPG);
			//converter.generateOutput(new File(outputDirectoryPath + "/test2.pdf"), SvgConverter.PDF);
			// converter.generateOutput(new File("/tmp/test.png"),
			// SvgConverter.PNG);
			//converter.generateOutput(new File(outputDirectoryPath + "/test.svg"), SvgConverter.SVG);
			ByteArrayInputStream is = new ByteArrayInputStream( os.toByteArray() );
			
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

			//String filename = firstname + lastname + request.getPortletSession().getId() + (int) (Math.random() * 1000000);
			String filename = getUniqueID(lastname + "-" + firstname);
			media.storeFile(targetDirectoryID, "image/jpeg", filename + ".jpg", is, request, themeDisplay);
			System.out.println("Files created in the output directory");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private String getUniqueID(String name) {
		
		Calendar cal =  Calendar.getInstance();
			
		long millis = cal.getTimeInMillis();
		
		int rand = (int) (Math.random() * 1000000);
		
		String md4input = name + millis + rand;
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		String md5 =String.format("%032x", new BigInteger(1, md.digest(md4input.getBytes())));
		
		return name + "-" + md5; 
		
		
		
	}

}