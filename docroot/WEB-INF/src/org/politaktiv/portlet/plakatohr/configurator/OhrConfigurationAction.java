package org.politaktiv.portlet.plakatohr.configurator;

import java.lang.reflect.Field;

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

/**
 * This class handles the correct saving of PlakatOhR configuration as set by the user
 * in the configuration tab.
 * @author nott
 */
public class OhrConfigurationAction extends DefaultConfigurationAction {
	
	private static Log _log;
	
	public OhrConfigurationAction() {
		_log = LogFactoryUtil.getLog(OhrConfigurationAction.class);
	}
	
	

	/**
	 * This takes the configuration from the HTTP request and stores it into the preference
	 * system for the PlakatOhR portlet. Does not rely on --preference keys as used by LifeRay by default.
	 */
	@Override
	public void processAction(
	    PortletConfig portletConfig, ActionRequest actionRequest,
	    ActionResponse actionResponse) throws Exception {  

	    super.processAction(portletConfig, actionRequest, actionResponse);

	    PortletPreferences prefs = actionRequest.getPreferences();
	    
	    // fancy hack: use reflection to walk through all config constants and get them from the request, store
	    // them into the preferences
	    for (Field f : OhrConfigConstants.class.getDeclaredFields()) {
	        String fieldValue = (String) f.get(OhrConfigConstants.class);
	        prefs.setValue(fieldValue, "" + ParamUtil.getLong(actionRequest,fieldValue));
	    }	    
	    
	    
	    _log.debug(stringUtil.formatMapForLog("New portlet preferences: ", prefs.getMap()));

	    prefs.store();
	}

	/**
	 * Used by LifeRay to find the JSP to load in the configuration tab.
	 * @return /jsp/portlet/configurator/ohrConfigurator.jsp
	 */
	@Override
	public String render(PortletConfig arg0, RenderRequest arg1, RenderResponse arg2) throws Exception {

			return "/jsp/portlet/configurator/ohrConfigurator.jsp";
	}

}
