package org.politaktiv.portlet.formular;

/*
 * This class is used to define the formular from the view-side and to offer functions to create a poster for this user
 */
public class Formular {
	
	private User user;
	
	public Formular() {}
	
	/*
	 * Escape HTML to get rid of potentially dangerous input
	 */
	private String escapeHTML(String input) {
		return input;
	}
	
	/*
	 * Extract the data from the formular to create a user
	 */
	private void extractData() {
		
	}
	
}