package org.politaktiv.portlet.plakatohr.controller;

import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletPreferences;

import org.politaktiv.portlet.plakatohr.configurator.OhrConfigConstants;

import com.liferay.mail.service.MailService;
import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.GetterUtil;

public class OhrMailHelper {
	
	private static Log _log;
	
	public OhrMailHelper() {
		_log = LogFactoryUtil.getLog(OhrMailHelper.class);
	}
	
	private static String formatFieldsToMailBody(Map<String,String> fields) {
		String result = "";
		
		for ( String f : fields.keySet()) {
			result += f + ": " + fields.get(f) + "\n\n";
			
		}
		
		return result; 
		
		
	}
	
	public void sendMail(Map<String, String> fields, PortletPreferences prefs) {
		sendMail(fields, prefs, null);
	}
	
	public void sendMail(Map<String, String> fields, PortletPreferences prefs, String replyTo) {
		
		MailMessage mail = new MailMessage();
		
		// check/set recipient, give up on error
		String toPref = GetterUtil.getString(
				prefs.getValue(OhrConfigConstants.E_MAIL_RECIPIENT, ""),
				"");
		try {
			InternetAddress to = new InternetAddress(toPref);
			mail.setTo(to);
		} catch ( AddressException e )  {
			_log.error("Cannot create recipient InternetAddress from " + toPref + ". Giving up sending this e-mail!");
			_log.error(e);
			return;
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
		String subjectPref = GetterUtil.getString(
				prefs.getValue(OhrConfigConstants.E_MAIL_SUBJECT, ""),
				"");
		if ( subjectPref == null || subjectPref.trim().equals("") ) {
			 mail.setSubject("(no subject)");
		} else {
			 mail.setSubject("subjectPref");
		}
		
		// now finally the contents/body of the mail
		mail.setBody(formatFieldsToMailBody(fields));
			
		
		// all set, now go and schedule the mail for sending!
		MailService service = MailServiceUtil.getService();
		service.sendEmail(mail);
		
		
	}
	
	

}
