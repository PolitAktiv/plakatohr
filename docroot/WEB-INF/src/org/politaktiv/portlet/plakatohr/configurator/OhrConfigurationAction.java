package org.politaktiv.portlet.plakatohr.configurator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.politaktiv.strutil.stringUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

public class OhrConfigurationAction extends DefaultConfigurationAction {
	
	private static Log _log;
	
	public OhrConfigurationAction() {
		_log = LogFactoryUtil.getLog(OhrConfigurationAction.class);
	}
	

	

	
/*	private void logAttributes(ActionRequest actionRequest) {
		
		LinkedList<String> result = new LinkedList<String>();
		
		  for (Enumeration<String> atts = actionRequest.getAttributeNames(); atts.hasMoreElements();) {
			  String attName = atts.nextElement();
			  result.add(attName + ":" + actionRequest.getAttribute(attName));
		  }
			_log.debug("Request Attributes: {" + strJoin(result) + "}");
		      
		
	}
*/
	
	@Override
	public void processAction(
	    PortletConfig portletConfig, ActionRequest actionRequest,
	    ActionResponse actionResponse) throws Exception {  

	    super.processAction(portletConfig, actionRequest, actionResponse);

	    PortletPreferences prefs = actionRequest.getPreferences();
	    
	    long sourceFolderId = ParamUtil.getLong(actionRequest, "sourceFolderId");
	    long targetFolderId = ParamUtil.getLong(actionRequest, "targetFolderId");
	    
	    prefs.setValue("sourceFolderId", "" + sourceFolderId);
	    prefs.setValue("targetFolderId", "" + targetFolderId);

	    _log.debug(stringUtil.formatMapForLog("New portlet preferences: ", prefs.getMap()));

	    prefs.store();
	}

	@Override
	public String render(PortletConfig arg0, RenderRequest arg1, RenderResponse arg2) throws Exception {

			return "/jsp/portlet/configurator/ohrConfigurator.jsp";
	}

}
