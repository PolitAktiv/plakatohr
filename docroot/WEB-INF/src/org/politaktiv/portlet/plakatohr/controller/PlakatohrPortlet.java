package org.politaktiv.portlet.plakatohr.controller;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.politaktiv.svgmanipulator.SvgConverter;
import org.politaktiv.svgmanipulator.SvgManipulator;
import org.politaktiv.svgmanipulator.util.base64Encoder;

import com.liferay.portal.kernel.io.ByteArrayFileInputStream;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.util.PortalUtil;
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
		String firstname = ParamUtil.getString(request, "firstname");
		String lastname = ParamUtil.getString(request, "lastname");
		String email = ParamUtil.getString(request, "email");
		String oppinion = ParamUtil.getString(request, "oppinion");
		
		//UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(request);
		//ByteArrayFileInputStream inputStream = null;

		/*
		 * try { File file = uploadPortletRequest.getFile("sampleFile");
		 * 
		 * if (!file.exists()) { System.out.println("Empty File"); }
		 * 
		 * if ((file != null) && file.exists()) { inputStream = new
		 * ByteArrayFileInputStream(file, 1024); byte [] data;
		 * 
		 * try { data = FileUtil.getBytes(inputStream); } catch (IOException e)
		 * { e.printStackTrace(); } } } finally {
		 * StreamUtil.cleanUp(inputStream); }
		 */

		System.out.println("====> " + firstname + " " + lastname + ", " + email + ", " + oppinion);

		System.out.println("Starting manipulation");
		//Has to be changed to the file upload request
		String imagePath = "/home/but/Bilder/hauck.jpg";
		//Has to be changed to the user selection
		String svgPath = "/home/but/Bilder/GrueneWiese.svg";
		startManipulation(firstname, lastname, oppinion, imagePath, svgPath);
	}

	/**
	 * Start the manipulation of a SVG file with a SvgManipulator and the given data from the
	 * @param firstname
	 * @param lastname
	 * @param oppinion
	 * @param imagePath
	 * @param svgPath
	 */
	private void startManipulation(String firstname, String lastname, String oppinion, String imagePath,
			String svgPath) {

		try {

			File svgFile = new File(svgPath);
			File jpgFile = new File(imagePath);

			String imgBase64 = base64Encoder.getBase64svg(jpgFile);

			SvgManipulator manipulator = new SvgManipulator(svgFile);

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

			SvgConverter converter = new SvgConverter(newSvgData);
			converter.generateOutput(new File("/tmp/test.jpg"), SvgConverter.JPG);
			converter.generateOutput(new File("/tmp/test2.pdf"), SvgConverter.PDF);
			// converter.generateOutput(new File("/tmp/test.png"),
			// SvgConverter.PNG);
			converter.generateOutput(new File("/tmp/test.svg"), SvgConverter.SVG);

			System.out.println("Files created in /tmp");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}