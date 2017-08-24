package org.politaktiv.portlet.plakatohr.configurator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import javax.portlet.PortletPreferences;

public class ohrConfigurationAction implements ConfigurationAction {

	@Override
	public void processAction(PortletConfig config, ActionRequest actionRequest, ActionResponse actionResponse) throws Exception { 
		
			  String portletResource = ParamUtil.getString(actionRequest, "portletResource"); 

			 PortletPreferences prefs = PortletPreferencesFactoryUtil.getPortletSetup(actionRequest, portletResource); 

			  //Read, validate, and then set form parameters as portlet preferences
			  // TODO: implement

			  prefs.store();

			  SessionMessages.add(actionRequest, config.getPortletName() + ".doConfigure");
			
		
	}

	@Override
	public String render(PortletConfig arg0, RenderRequest arg1, RenderResponse arg2) throws Exception {

			return "/jsp/portlet/configurator/ohrConfigurator.jsp";
	}

}
