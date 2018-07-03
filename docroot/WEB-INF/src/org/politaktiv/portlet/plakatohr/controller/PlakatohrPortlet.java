package org.politaktiv.portlet.plakatohr.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.io.FileUtils;
import org.politaktiv.portlet.plakatohr.configurator.OhrConfigConstants;
import org.politaktiv.svgmanipulator.SvgConverter;
import org.politaktiv.svgmanipulator.SvgManipulator;
import org.politaktiv.svgmanipulator.util.MimeTypeException;
import org.politaktiv.svgmanipulator.util.base64Encoder;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
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

/**
 * This class is the main controller for the portlet and used for action phase calls
 */

public class PlakatohrPortlet extends MVCPortlet {

	private static final String JSP_PATH = "/jsp/portlet/";
	private static final String BACKGROUND_SELECTION_JSP = JSP_PATH + "formular/backgroundSelection.jsp";
	private static final String USER_DATA_FORMULAR_JSP = JSP_PATH + "formular/userDataFormular.jsp";
	//private static final String IMAGE_CROPPER_JSP = JSP_PATH + "alloyUI/imageCropper.jsp";
	private static final String PREVIEW_JSP = JSP_PATH + "formular/preview.jsp";
	private static final String SUCCESS_JSP = JSP_PATH + "formular/success.jsp";
	private static final String TERMS_CONDITIONS_JSP = JSP_PATH + "termscond.jsp";
	
	//private static final String STANDARD_USER_PICTURE = "/images/standardUserPicture.png";
	
	private static final String GUEST_USER_DIR = "/home/ohr/";
	
	private static final String SESSION_ATTR_NAME_JPEG ="OhrDataJpeg";
	private static final String SESSION_ATTR_NAME_PDF ="OhrDataPdf";
	
	private static Log _log;

	/**
	 * Constructor for the portlet. Do not use it yourself, it will be called by Liferay automatically!
	 */
	public PlakatohrPortlet() {
		_log = LogFactoryUtil.getLog(PlakatohrPortlet.class);
	}

	/**
	 * Initializes the Plakatohr and goes back to the first page
	 * @param request
	 * @param response
	 */
	public void initializePlakatohr(ActionRequest request, ActionResponse response) {
		response.setRenderParameter("jspPage", BACKGROUND_SELECTION_JSP);
	}
	
	
	public static String getTermsCondJsp() {
		return TERMS_CONDITIONS_JSP;
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
	public void userDataSubmit(ActionRequest request, ActionResponse response) throws IOException {

		UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(request);
		String firstname = ParamUtil.getString(uploadPortletRequest, "firstname");
		String lastname = ParamUtil.getString(uploadPortletRequest, "lastname");
		String email = ParamUtil.getString(uploadPortletRequest, "email");
		String textBeginning = "";
		if (!ParamUtil.getString(uploadPortletRequest, "textBeginning").equals("Freitext")) {
			textBeginning = ParamUtil.getString(uploadPortletRequest, "textBeginning") + " ";	
		}						
		String opinion = textBeginning + ParamUtil.getString(uploadPortletRequest, "opinion");
		String backgroundIDString = ParamUtil.getString(uploadPortletRequest, "backgroundID");
		long backgroundID = -1;
		try {
			backgroundID = Long.parseLong(backgroundIDString);
		}	
		catch ( NumberFormatException e) {
			_log.error("Malformated background ID parameter");
			//_log.error(e);
			//return;
			throw(e);
		}
		PortletPreferences portletPreferences = request.getPreferences();
		
		File file = uploadPortletRequest.getFile("picture");
		//File file = new File(STANDARD_USER_PICTURE);
		/*File tempFile = uploadPortletRequest.getFile("picture");
		if((tempFile != null) && ((getFileExtension(file) == "jpg")
							   || (getFileExtension(file) == "png"))) {
			file = tempFile;
		} */

		try {
			InputStream inStream = new FileInputStream(file);

			_log.debug("User data: " +  firstname + " " + lastname + ", " + email + ", " + opinion);
			_log.debug("Background ID from UI: " + backgroundID);
			
			
			OhrMediaHelper media = new OhrMediaHelper();
			
			Long sourceDirectoryID = GetterUtil.getLong(
					portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			Map<DLFileEntry, DLFileEntry> previewTemplateMap = media.getBackgroundPreviewsAndTemplates(
					sourceDirectoryID, (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));

			DLFileEntry result = null;
			for (DLFileEntry jpg : previewTemplateMap.keySet()) {
				if (jpg.getFileEntryId() == backgroundID) {
					result = previewTemplateMap.get(jpg);
				}
			}
			if (result != null) {
				startManipulation(firstname, lastname, opinion, inStream, result, request);
				
				//response.setRenderParameter("picture", id);
				response.setRenderParameter("firstname", firstname);
				response.setRenderParameter("lastname", lastname);
				response.setRenderParameter("email", email);
				response.setRenderParameter("opinion", opinion);
				response.setRenderParameter("background", backgroundIDString);
				response.setRenderParameter("userPicture", file.getPath());
				response.setRenderParameter("jspPage", PREVIEW_JSP);
			} else {
				inStream.close();
				throw new RuntimeException("Illegal background ID: " + backgroundID);
			}

		} catch (IOException e) {
			_log.error("Cannot load background image.");
			throw(e);
		} catch (MimeTypeException e) {
			SessionErrors.add(request, "Benutzer-Bild kann nicht geladen werden: Inkompatibles Dateiformat.");
			_log.warn(e);
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
	 * @throws IOException 
	 * @throws MimeTypeException 
	 */
	private String startManipulation(String firstname, String lastname, String opinion, InputStream inStream,
			DLFileEntry svgFile, ActionRequest request) throws IOException, MimeTypeException {
		
		String id = null;

		try {

			InputStream svgInputStream = svgFile.getContentStream();

			_log.debug("Source File:" + svgFile.getTitle());

			String imgBase64 = base64Encoder.getBase64svg(inStream);
			SvgManipulator manipulator = new SvgManipulator(svgInputStream);

			Set<String> fieldNames = manipulator.getAllFieldNames();
			for (String fN : fieldNames) {
				_log.debug(fN + " - " + manipulator.getSizeOfFlowPara(fN));
			}

			manipulator.replaceTextAll("MEINUNG", opinion);
			manipulator.replaceTextAll("NAME", firstname + " " + lastname);
			manipulator.replaceFlowParaByImage("FOTO", imgBase64);

			manipulator.setSvgVersion("1.2");
			manipulator.convertFlowInkscapeToBatik();
			manipulator.convertCssInkscapeToBatik();

			String newSvgData = manipulator.getSvgAsXml();
			SvgConverter converter = new SvgConverter(newSvgData);
			
			
			PortletSession s = request.getPortletSession();
			
			ByteArrayOutputStream osJpg = new ByteArrayOutputStream();
			converter.generateOutput(osJpg, SvgConverter.JPG);
			osJpg.close();
			byte[] daten = osJpg.toByteArray();
			s.setAttribute(SESSION_ATTR_NAME_JPEG, daten);

			ByteArrayOutputStream osPdf = new ByteArrayOutputStream();
			converter.generateOutput(osPdf, SvgConverter.PDF);
			daten = osPdf.toByteArray();
			osPdf.close();
			s.setAttribute(SESSION_ATTR_NAME_PDF, daten);
			
			

		} catch (IOException e) {
			throw(e);
		} catch (PortalException e) {
			throw new IOException(e);
		} catch (SystemException e) {
			throw new IOException(e);
		} catch (MimeTypeException e) {
			_log.warn("Cannot determine MIME type for user-uploaded image.");
			throw(e);
		}
		
		return id;

	}

	/**
	 * Creates a unique ID from a given string through MD5
	 * @param name
	 * @return
	 */
	private String getUniqueID(String name) {
		
		String nameEscaped = name.replaceAll("ä", "ae");
		nameEscaped = nameEscaped.replaceAll("Ä", "Ae");
		nameEscaped = nameEscaped.replaceAll("ü", "ue");
		nameEscaped = nameEscaped.replaceAll("Ü", "Ue");
		nameEscaped = nameEscaped.replaceAll("Ö", "Oe");
		nameEscaped = nameEscaped.replaceAll("ö", "oe");
		nameEscaped = nameEscaped.replaceAll("ß", "ss");
		nameEscaped = nameEscaped.replaceAll("[^A-Za-z]", "_");

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

		return nameEscaped + "-" + md5;

	}

	/**
	 * Saves the user's choice from the background selection JSP and moves forward to the next page
	 * @param request
	 * @param response
	 */
	public void backgroundSelection(ActionRequest request, ActionResponse response) {
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		String backgroundID = uploadRequest.getParameter("background");

		_log.debug("Background selected, ID: " + backgroundID);
		
		//PortletSession s = request.getPortletSession();
		//s.setAttribute("testTextText", "Dies ist voll der Test-Text",PortletSession.APPLICATION_SCOPE);
		//s.setAttribute("testTextText", "Dies ist voll der Test-Text");
				
		response.setRenderParameter("backgroundID", backgroundID);
		response.setRenderParameter("termsConditionsJsp", TERMS_CONDITIONS_JSP);
		response.setRenderParameter("jspPage", USER_DATA_FORMULAR_JSP);
	}
	
	public void returnToUserDataFormular(ActionRequest request, ActionResponse response) {
		String backgroundID = ParamUtil.getString(request, "backgroundID");
		String firstname = ParamUtil.getString(request, "firstname");
		String lastname = ParamUtil.getString(request, "lastname");
		String email = ParamUtil.getString(request, "email");
		String opinion = ParamUtil.getString(request, "opinion");

		_log.debug("Background set, ID: " + backgroundID);
		
		//PortletSession s = request.getPortletSession();
		//s.setAttribute("testTextText", "Dies ist voll der Test-Text",PortletSession.APPLICATION_SCOPE);
		//s.setAttribute("testTextText", "Dies ist voll der Test-Text");
		
		response.setRenderParameter("backgroundID", backgroundID);
		response.setRenderParameter("termsConditionsJsp", TERMS_CONDITIONS_JSP);
		response.setRenderParameter("firstname", firstname);
		response.setRenderParameter("lastname", lastname);
		response.setRenderParameter("email", email);
		response.setRenderParameter("opinion", opinion);
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
	 * @throws IOException 
	 */
	public void publish(ActionRequest request, ActionResponse response) throws PortalException, SystemException, IOException  {
		OhrMailHelper mail = new OhrMailHelper();
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		String email = uploadRequest.getParameter("email");
		String lastname = uploadRequest.getParameter("lastname");
		String firstname = uploadRequest.getParameter("firstname");
		String opinion = uploadRequest.getParameter("opinion");
		
		OhrMediaHelper media = new OhrMediaHelper();
		
		
		PortletPreferences portletPreferences = request.getPreferences();
		
		Long targetDirectoryID = GetterUtil.getLong(
				portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_log.debug("Target: " + targetDirectoryID);
		_log.debug("Target Path: " + DLFolderLocalServiceUtil.getFolder(targetDirectoryID).getPath());
		
		
		String baseName = getUniqueID(lastname + "-" + firstname);
		String filenameJpg = baseName + ".jpg";
		String filenamePdf = baseName + ".pdf";

		byte[] jpegData;
		try {
			jpegData = getDataFromSession(request, SESSION_ATTR_NAME_JPEG);
		} catch (IOException e) {
			throw(new IOException("Cannot load JPEG data from portlet session.", e));
		}
		byte[] pdfData;
		try {
			pdfData= getDataFromSession(request, SESSION_ATTR_NAME_PDF);
		} catch (IOException e) {
			throw(new IOException("Cannot load PDF data from portlet session.", e));
		}

		
		_log.debug("Storing " +filenameJpg + " ...");
		media.storeFile(targetDirectoryID, "image/jpeg", filenameJpg,
				new ByteArrayInputStream(jpegData),
				request);
		_log.debug("Storing " +filenamePdf + " ...");
		media.storeFile(targetDirectoryID, "application/pdf", filenamePdf,
				new ByteArrayInputStream(pdfData),
				request);
		
		_log.debug("Files created in the output directory.");		
		
		HashMap<String,String> mailFields = new HashMap<String, String>();
		mailFields.put("Vorname", firstname);
		mailFields.put("Nachname", lastname);
		mailFields.put("E-Mail", email);
		mailFields.put("Meinung", opinion.replaceAll("\\n", " ").replaceAll("\\r", " "));
		mailFields.put("Plakat-Datei", baseName);
		mail.sendMail(mailFields, request, email, null);
		response.setRenderParameter("jspPage", SUCCESS_JSP);
	}
	
	/**
	 * Called when the user accepts to publish the created Plakat.
	 * Only will get triggered for users who are not logged in
	 * It will send a mail to a moderator/admin and move the user to the success jsp.
	 * @param request
	 * @param response
	 * @throws NumberFormatException
	 * @throws PortalException
	 * @throws SystemException
	 * @throws IOException 
	 */
	public void publishAsGuest(ActionRequest request, ActionResponse response) throws PortalException, SystemException, IOException  {
		OhrMailHelper mail = new OhrMailHelper();
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		String email = uploadRequest.getParameter("email");
		String lastname = uploadRequest.getParameter("lastname");
		String firstname = uploadRequest.getParameter("firstname");
		String opinion = uploadRequest.getParameter("opinion");
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String siteName = themeDisplay.getScopeGroup().getName();

		_log.debug("Target Path: " + GUEST_USER_DIR + "/" + siteName);	
		
		String baseName = getUniqueID(lastname + "-" + firstname);
		String filenameJpg = baseName + ".jpg";
		String filenamePdf = baseName + ".pdf";
		
		List<File> fileAttachments = new LinkedList<File>();

		File currentGuestDir = new File(GUEST_USER_DIR + "/" + siteName);
		if(!currentGuestDir.exists()) {
			currentGuestDir.mkdir();
		}
		byte[] jpegData;
		try {
			jpegData = getDataFromSession(request, SESSION_ATTR_NAME_JPEG);
			_log.debug("Storing " + filenameJpg + " ...");
			File file = new File(GUEST_USER_DIR + "/" + siteName + "/" + filenameJpg);
			FileUtils.writeByteArrayToFile(file, jpegData);
			fileAttachments.add(file);
		} catch (IOException e) {
			throw(new IOException("Cannot load JPEG data from portlet session.", e));
		}
		byte[] pdfData;
		try {
			pdfData= getDataFromSession(request, SESSION_ATTR_NAME_PDF);
			_log.debug("Storing " + filenamePdf + " ...");
			File file = new File(GUEST_USER_DIR + "/" + siteName + "/" + filenamePdf);
			FileUtils.writeByteArrayToFile(file, pdfData);
			fileAttachments.add(file);
		} catch (IOException e) {
			throw(new IOException("Cannot load PDF data from portlet session.", e));
		}
		
		_log.debug("Files created in the output directory.");		
		
		HashMap<String,String> mailFields = new HashMap<String, String>();
		mailFields.put("Vorname", firstname);
		mailFields.put("Nachname", lastname);
		mailFields.put("E-Mail", email);
		mailFields.put("Meinung", opinion.replaceAll("\\n", " ").replaceAll("\\r", " "));
		mailFields.put("Plakat-Datei", baseName);
		mailFields.put("Gast", "true");
		mailFields.put("Ordner", GUEST_USER_DIR + "/" + siteName);
		mail.sendMail(mailFields, request, email, fileAttachments);
		response.setRenderParameter("jspPage", SUCCESS_JSP);
	}
	
	private byte[] getDataFromSession(PortletRequest request, String attKey) throws IOException {
		PortletSession pS = request.getPortletSession();
		Object dataObj = pS.getAttribute(attKey);
		if (dataObj == null ) {
			throw new IOException("Session does not contain data for key: " + attKey);
		}
		byte[] daten = (byte[]) dataObj;
		
		return daten;
	}
	
	private void sendDataFromSession(ResourceRequest request, ResourceResponse response, String attKey, String mimeType) throws IOException {
		byte[] daten= getDataFromSession(request, attKey);
		
		response.setContentType(mimeType);
		OutputStream out = response.getPortletOutputStream();
		out.write(daten, 0, daten.length);
		out.flush();		
	}
	
	
	
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws IOException, PortletException {
		
		String fileName = ParamUtil.getString(request, "fileName");
		
		if (fileName.equals("plakat.pdf")) {
			sendDataFromSession(request, response, SESSION_ATTR_NAME_PDF, "application/pdf");	
		} else if (fileName.equals("plakat.jpg")) {
			sendDataFromSession(request, response, SESSION_ATTR_NAME_JPEG, "image/jpeg");
		} else {
			throw new IOException("I do not know how to handle the requested file name: " + fileName);
		}
		
	}
	/*
	private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
    */
	

}