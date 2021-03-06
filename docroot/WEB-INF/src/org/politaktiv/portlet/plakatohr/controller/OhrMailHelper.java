package org.politaktiv.portlet.plakatohr.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.politaktiv.portlet.plakatohr.configurator.OhrConfigConstants;
import org.politaktiv.strutil.stringUtil;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * Helper class for sending notification mails. This provides both generic functions for sending mail
 * as well as functions specific to the PlakatOhR portlet. E-mail messages are scheduled for sending
 * asynchronously. All errors and failures are reported to log. 
 */
public class OhrMailHelper {
	
	private static Log _log;
	
	public OhrMailHelper() {
		_log = LogFactoryUtil.getLog(OhrMailHelper.class);
	}
	
	private String formatFieldsToMailBody(Map<String,String> fields) {
		String result = "";
		for ( String f : fields.keySet()) {
			result += f + ": " + fields.get(f) + "\n\n";
		}
		return result; 
	}

	/**
	 * Sends an e-mail message with all values specified as strings.
	 * While this is close to service methods provided by LifeRay, it does does send e-mail asynchronously.
	 * @param body the body of the message.
	 * @param subject the subject of the message.
	 * @param from from address.
	 * @param to recipient address.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	public boolean sendMail(String body, String subject, String from, String to, List<File> files) {
		return sendMail(body, subject, from, to, null, files);
	}
	
	/**
	 * Sends an e-mail message with all values specified as strings.
	 * While this is close to service methods provided by LifeRay, it allows to specify
	 * a reply-to address and does send e-mail asynchronously.
	 * @param body the body of the message.
	 * @param subject the subject of the message.
	 * @param from from address.
	 * @param to recipient address.
	 * @param replyTo a reply-to address.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	public boolean sendMail(String body, String subject, String from, String to, String replyTo, List<File> files) {
		MailMessage mail = new MailMessage();
		
		// check/set recipient, give up on error
		try {
			InternetAddress toAddr = new InternetAddress(to.trim());
			mail.setTo(toAddr);
		} catch ( AddressException e )  {
			_log.error("Cannot create recipient InternetAddress from " + to + ". Giving up sending this e-mail!");
			_log.error(e);
			return false;
		}		
		
		// check/set reply-to, be failsafe
		if (replyTo != null && ! replyTo.trim().equals("") ) {
			InternetAddress[] replyToArr = new InternetAddress[1];
			try {
				replyToArr[0] = new InternetAddress(replyTo);
				mail.setReplyTo(replyToArr);
			} catch ( AddressException e )  {
				_log.error("Cannot create replyTo InternetAddress from " + replyTo + ", sending mail without replyTo header.");
			}
		}
		
		// check/set subject, be failsafe
		if ( subject == null || subject.trim().equals("") ) {
			 mail.setSubject("(no subject)");
		} else {
			 mail.setSubject(subject);
		}

		// LifeRay fails without a sender address
		try {
			mail.setFrom(new InternetAddress(from.trim()));
		} catch (AddressException e) {
			_log.error("Cannot create sender InternetAddress from " + from + ". Giving up sending this e-mail!");
			_log.error(e);
			return false;
		}

		
		// now finally the contents/body of the mail
		mail.setBody(body);
			
		//add all file attachments if there are any
		if (files != null && !files.isEmpty()) {
			for (File file : files) {
				mail.addFileAttachment(file, file.getName());
			}
		}
		
		// all set, now go and schedule the mail for sending!
		// ... some debugging stuff
		HashMap<String, String[]> debugData = new HashMap<String, String[]>();
		debugData.put("to", stringUtil.addressesToStrings(mail.getTo()));
		debugData.put("from", stringUtil.addressesToStrings(mail.getFrom()));
		debugData.put("reply-to", stringUtil.addressesToStrings(mail.getReplyTo()));
		debugData.put("subject", stringUtil.addressesToStrings(mail.getSubject()));
		debugData.put("html", stringUtil.addressesToStrings(  ((mail.getHTMLFormat() == true) ? "true": "false") ) );		
		_log.debug("Scheduling e-mail for sending: " +
				stringUtil.formatMapForLog(debugData));
		
		// ... go for it
		MailServiceUtil.sendEmail(mail);
		
		return true;
	}
	
	
	/**
	 * Sends an e-mail with a message created from form fields. 
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param fields the fields to create the message body from.
	 * @param prefs portlet preferences for recipient and subject.
	 * @param themeDisplay used to obtain system preferences for sender address.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	public boolean sendMail(Map<String, String> fields, PortletPreferences prefs, ThemeDisplay themeDisplay, List<File> files) {
		return sendMail(fields, prefs, themeDisplay, null, files);
	}
	
	/**
	 * Sends an e-mail with a message created from form fields. 
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param fields the fields to create the message body from.
	 * @param request used to obtain system preferences for sender address.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	
	public boolean sendMail(Map<String, String> fields, PortletRequest request, List<File> files) {
		return sendMail(fields, request, null, files);
	}

	
	/**
	 * Sends an e-mail with a message with a body from a string.
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param body the body of the e-mail message.
	 * @param prefs portlet preferences for recipient and subject.
	 * @param themeDisplay used to obtain system preferences for sender address.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	public boolean sendMail(String body, PortletPreferences prefs, ThemeDisplay themeDisplay, List<File> files) {
		return sendMail(body, prefs, themeDisplay, null, files);
	}

	/**
	 * Sends an e-mail with a message with a body from a string.
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param body the body of the e-mail message.
	 * @param request used to obtain system preferences for sender address.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	
	public boolean sendMail(String body, PortletRequest request, List<File> files) {
		return sendMail(body, request, null, files);
	}
	
	
	/**
	 * Sends an e-mail with a message created from form fields. 
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param fields the fields to create the message body from.
	 * @param prefs portlet preferences for recipient and subject.
	 * @param themeDisplay used to obtain system preferences for sender address.
	 * @param replyTo a reply to address to set for this message.
	 * @param files a list of file attachments. null or empty list for no attachments
 	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	public boolean sendMail(Map<String, String> fields, PortletPreferences prefs, ThemeDisplay themeDisplay, String replyTo, List<File> files) {
		return sendMail(formatFieldsToMailBody(fields), prefs, themeDisplay, replyTo, files);
	}

	/**
	 * Sends an e-mail with a message created from form fields. 
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param fields the fields to create the message body from.
	 * @param request used to obtain system preferences for sender address.
	 * @param replyTo a reply to address to set for this message.
	 * @param files a list of file attachments. null or empty list for no attachments
 	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	boolean sendMail(Map<String, String> fields, PortletRequest request, String replyTo, List<File> files) {
		return sendMail(formatFieldsToMailBody(fields), request, replyTo, files);
	}

	/**
	 * Sends an e-mail with a message with a body from a string.
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param body the body of the e-mail message.
	 * @param prefs portlet preferences for recipient and subject.
	 * @param themeDisplay used to obtain system preferences for sender address.
	 * @param replyTo a reply to address to set for this message.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	public boolean sendMail(String body, PortletPreferences prefs, ThemeDisplay themeDisplay, String replyTo, List<File> files) {
		
		// collect preferences
		String subjectPref = GetterUtil.getString(
				prefs.getValue(OhrConfigConstants.E_MAIL_SUBJECT, ""),
				"");
		String toPref = GetterUtil.getString(
				prefs.getValue(OhrConfigConstants.E_MAIL_RECIPIENT, ""),
				"");
				
		// obtain system-wide sender address
		String fromAddress;
		try {
			fromAddress = PrefsPropsUtil.getString(themeDisplay.getCompanyId(),PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);
		} catch (SystemException e) {
			_log.error("Cannot get from address from system configuration, giving up on this e-mail.");
			_log.error(e);
			return false;
		}

		return sendMail(body, subjectPref, fromAddress, toPref, replyTo, files);

		
		
	}	
	
	/**
	 * Sends an e-mail with a message with a body from a string.
	 * All other relevant data (sender, recipient, subject etc.) are obtained from
	 * preferences and system configuration. The mail-message is sent asynchronously in the background.
	 * @param body the body of the e-mail message.
	 * @param request used to obtain system preferences for sender address.
	 * @param replyTo a reply to address to set for this message.
	 * @param files a list of file attachments. null or empty list for no attachments
	 * @return false on any kind of error. Returning true can still mean what the asynchronous sending may fail in the backend.
	 */
	public boolean sendMail(String body, PortletRequest request, String replyTo, List<File> files) {
		return sendMail(body, request.getPreferences(), getThemeDisplay(request), replyTo, files);
		
	}
	
	private ThemeDisplay getThemeDisplay(PortletRequest request) {
		return (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
	}
	

}
