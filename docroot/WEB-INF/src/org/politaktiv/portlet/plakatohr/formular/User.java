package org.politaktiv.portlet.plakatohr.formular;

/*
 * This class is used to define a user with the data defined in the forumlar
 */
public class User {
	
	private String surname;
	private String lastname;
	private String message;
	private String background;
	private String picture;
	
	/*
	 * A minimal user is defined by the following attributes
	 * 		surname
	 * 		lastname
	 * 		message
	 * 		background
	 * 		picture
	 */
	public User(String surname, String lastname, String message, String background, String picture) {
		this.surname 	= surname;
		this.lastname 	= lastname;
		this.message 	= message;
		this.background = background;
		this.picture 	= picture;
	}
	
	public String getSurname() {
		return this.surname;
	}
	
	public String getLastname() {
		return this.lastname;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String getBackground() {
		return this.background;
	}
	
	public String getPicture() {
		return this.picture;
	}
	
}