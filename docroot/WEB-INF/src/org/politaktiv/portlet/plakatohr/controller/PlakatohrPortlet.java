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
import javax.portlet.PortletSession;

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
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * This class is the main controller for the portlet and used for action phase calls
 */

public class PlakatohrPortlet extends MVCPortlet {

	private static final String JSP_PATH = "/jsp/portlet/";
	private static final String BACKGROUND_SELECTION_JSP = JSP_PATH + "formular/backgroundSelection.jsp";
	private static final String USER_DATA_FORMULAR_JSP = JSP_PATH + "formular/userDataFormular.jsp";
	private static final String PREVIEW_JSP = JSP_PATH + "formular/preview.jsp";
	private static final String DATA_PROVIDER_JSP = JSP_PATH + "/dataProvider.jsp";
	private static final String SUCCESS_JSP = JSP_PATH + "formular/success.jsp";

	/**
	 * Constructor for the portlet. Do not use it yourself, it will be called by Liferay automatically!
	 */
	public PlakatohrPortlet() {

	}

	/**
	 * Initializes the Plakatohr and goes back to the first page
	 * @param request
	 * @param response
	 */
	public void initializePlakatohr(ActionRequest request, ActionResponse response) {
		response.setRenderParameter("jspPage", BACKGROUND_SELECTION_JSP);
	}

	/**
	 * Collects the data from the form filled in by the user, creates a Plakat and moves to the next page. The form contains:
	 * First name
	 * Last name
	 * E-Mail
	 * Opinion
	 * @param request
	 * @param reponse
	 */
	public void userDataSubmit(ActionRequest request, ActionResponse response) throws RuntimeException {

		UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(request);
		String firstname = ParamUtil.getString(uploadPortletRequest, "firstname");
		String lastname = ParamUtil.getString(uploadPortletRequest, "lastname");
		String email = ParamUtil.getString(uploadPortletRequest, "email");
		String opinion = ParamUtil.getString(uploadPortletRequest, "opinion");
		long backgroundID = Long.parseLong(ParamUtil.getString(uploadPortletRequest, "backgroundID"));
		File file = uploadPortletRequest.getFile("picture");

		try {
			InputStream inStream = new FileInputStream(file);

			System.out.println("====> " + firstname + " " + lastname + ", " + email + ", " + opinion);
			System.out.println("Starting manipulation");

			OhrMediaHelper media = new OhrMediaHelper();
			PortletPreferences portletPreferences = request.getPreferences();
			Long sourceDirectoryID = GetterUtil.getLong(
					portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			Map<DLFileEntry, DLFileEntry> previewTemplateMap = media.getBackgroundPreviewsAndTemplates(
					sourceDirectoryID, (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));

			System.out.println("Final background ID: " + backgroundID);
			DLFileEntry result = null;
			for (DLFileEntry jpg : previewTemplateMap.keySet()) {
				if (jpg.getFileEntryId() == backgroundID) {
					result = previewTemplateMap.get(jpg);
				}
			}
			if (result != null) {
				String id = startManipulation(firstname, lastname, opinion, inStream, result, request);
				
				response.setRenderParameter("picture", id);
				response.setRenderParameter("email", email);
				response.setRenderParameter("jspPage", PREVIEW_JSP);
			} else {
				throw new RuntimeException("Illegal background ID: " + backgroundID);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Start the manipulation of a SVG file with a SvgManipulator and the given
	 * data from the form and background selection
	 * @param firstname
	 * @param lastname
	 * @param oppinion
	 * @param imagePath
	 * @param svgPath
	 */
	private String startManipulation(String firstname, String lastname, String opinion, InputStream inStream,
			DLFileEntry svgFile, ActionRequest request) {
		
		String id = null;

		try {
			OhrMediaHelper media = new OhrMediaHelper();
			PortletPreferences portletPreferences = request.getPreferences();

			Long targetDirectoryID = GetterUtil.getLong(
					portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			System.out.println("Target: " + targetDirectoryID);
			System.out.println("Target Path: " + DLFolderLocalServiceUtil.getFolder(targetDirectoryID).getPath());

			InputStream svgInputStream = svgFile.getContentStream();

			System.out.println("Source File:" + svgFile.getTitle());

			String imgBase64 = base64Encoder.getBase64svg(inStream);
			SvgManipulator manipulator = new SvgManipulator(svgInputStream);

			Set<String> fieldNames = manipulator.getAllFieldNames();
			for (String fN : fieldNames) {
				System.err.println(fN + " - " + manipulator.getSizeOfFlowPara(fN));
			}

			manipulator.replaceTextAll("MEINUNG", opinion);
			manipulator.replaceTextAll("NAME", firstname + " " + lastname);
			manipulator.replaceFlowParaByImage("FOTO", imgBase64);

			manipulator.setSvgVersion("1.2");
			manipulator.convertFlowInkscapeToBatik();
			manipulator.convertCssInkscapeToBatik();

			String newSvgData = manipulator.getSvgAsXml();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			SvgConverter converter = new SvgConverter(newSvgData);
			converter.generateOutput(os, SvgConverter.JPG);
			// converter.generateOutput(new File(outputDirectoryPath +
			// "/test.jpg"), SvgConverter.JPG);
			// converter.generateOutput(new File(outputDirectoryPath +
			// "/test2.pdf"), SvgConverter.PDF);
			// converter.generateOutput(new File("/tmp/test.png"),
			// SvgConverter.PNG);
			// converter.generateOutput(new File(outputDirectoryPath +
			// "/test.svg"), SvgConverter.SVG);
			
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			String filename = getUniqueID(lastname + "-" + firstname);
			String jpgFilename = filename + ".jpg";
			id = String.valueOf(media.storeFile(targetDirectoryID, "image/jpeg", jpgFilename, is, request));
			System.out.println("Files created in the output directory");	
			
			// session Testerei
			PortletSession s = request.getPortletSession();
			//s.setAttribute("testTextText", "Dies ist voll der Test-Text",PortletSession.APPLICATION_SCOPE);
			//s.setAttribute("testTextText", "Dies ist voll der Test-Text");
			converter.generateOutput(os, SvgConverter.JPG);
			byte[] daten = os.toByteArray();
			s.setAttribute("daten", daten);
			
			

		} catch (IOException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return id;

	}

	/**
	 * Creates a unique ID from a given string through MD5
	 * @param name
	 * @return
	 */
	private String getUniqueID(String name) {

		// Use time in millis and a random number to massively reduce the chance of collision
		Calendar cal = Calendar.getInstance();
		long millis = cal.getTimeInMillis();
		int rand = (int) (Math.random() * 1000000);

		// MD5 Hash
		String md4input = name + millis + rand;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		String md5 = String.format("%032x", new BigInteger(1, md.digest(md4input.getBytes())));

		return name + "-" + md5;

	}

	/**
	 * Saves the user's choice from the background selection JSP and moves forward to the next page
	 * @param request
	 * @param response
	 */
	public void backgroundSelection(ActionRequest request, ActionResponse response) {
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		String backgroundID = uploadRequest.getParameter("background");

		System.out.println("===> Background ID: " + backgroundID);
		
		//PortletSession s = request.getPortletSession();
		//s.setAttribute("testTextText", "Dies ist voll der Test-Text",PortletSession.APPLICATION_SCOPE);
		//s.setAttribute("testTextText", "Dies ist voll der Test-Text");
		
				
		response.setRenderParameter("backgroundID", backgroundID);
		response.setRenderParameter("jspPage", USER_DATA_FORMULAR_JSP);
	}
	
	/**
	 * Called when the user accepts to publish the created Plakat.
	 * It will send a mail to a moderator/admin and move the user to the success jsp.
	 * @param request
	 * @param response
	 * @throws NumberFormatException
	 * @throws PortalException
	 * @throws SystemException
	 */
	public void publish(ActionRequest request, ActionResponse response) throws NumberFormatException, PortalException, SystemException {
		OhrMailHelper mail = new OhrMailHelper();
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		String email = uploadRequest.getParameter("email");
		String plakatID = uploadRequest.getParameter("plakatID");
		DLFileEntry plakat = DLFileEntryLocalServiceUtil.getDLFileEntry(Long.parseLong(plakatID));
		String plakatTitle = plakat.getTitle();
		
		String content = "Anfrage zur Veröffentlichung eines Plakats mit der ID " + plakatID + ", Titel " + plakatTitle + " und Nutzer E-Mail Adresse: " + email;
		mail.sendMail(content, request, email);
		response.setRenderParameter("jspPage", SUCCESS_JSP);
	}
	
	public void providePreviewImage(ActionRequest request, ActionResponse response) {
		response.setRenderParameter("type", "jpg");
		response.setRenderParameter("jspPage", DATA_PROVIDER_JSP);
	}
	
	public void providePdfImage(ActionRequest request, ActionResponse response) {
		response.setRenderParameter("type", "pdf");
		response.setRenderParameter("jspPage", DATA_PROVIDER_JSP);
	}
	

}