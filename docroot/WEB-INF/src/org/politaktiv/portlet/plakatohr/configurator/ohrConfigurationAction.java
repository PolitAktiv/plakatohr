package org.politaktiv.portlet.plakatohr.configurator;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

public class ohrConfigurationAction extends DefaultConfigurationAction {

	/*
	@Override
	public void processAction(
	    PortletConfig portletConfig, ActionRequest actionRequest,
	    ActionResponse actionResponse) throws Exception {  

	    super.processAction(portletConfig, actionRequest, actionResponse);

	    PortletPreferences prefs = actionRequest.getPreferences();

	    String somePreferenceKey = prefs.getValue(
	        "somePreferenceKey", "true");

	    // Add any preference processing here.
	}
	*/

	@Override
	public String render(PortletConfig arg0, RenderRequest arg1, RenderResponse arg2) throws Exception {

			return "/jsp/portlet/configurator/ohrConfigurator.jsp";
	}

}
