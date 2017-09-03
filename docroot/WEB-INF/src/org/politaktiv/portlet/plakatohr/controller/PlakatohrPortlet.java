package org.politaktiv.portlet.plakatohr.controller;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/*
 * This class is the main controller for the portlet managing incoming events from the frontend.
 * It also uses the configuration file to use project specific data.
 */
public class PlakatohrPortlet extends MVCPortlet {
	
	public PlakatohrPortlet() {
		
	}
	
	public void userDataSubmit(ActionRequest request, ActionResponse reponse) {
		String firstname = ParamUtil.getString(request, "firstname");
		String lastname = ParamUtil.getString(request, "lastname");
		String email = ParamUtil.getString(request, "email");
		String oppinion = ParamUtil.getString(request, "oppinion");
		
		System.out.println("====> " + firstname + " " + lastname + ", " + email + ", " + oppinion);
	}
	
}