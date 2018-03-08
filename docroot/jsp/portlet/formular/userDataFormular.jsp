<%@page import="com.liferay.portal.kernel.util.LocaleUtil"%>
<%@page
	import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil"%>
<%@page import="com.liferay.portlet.journal.model.JournalArticle"%>
<%@page
	import="org.politaktiv.portlet.plakatohr.controller.PlakatohrPortlet"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/jsp/portlet/import.jsp"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>

<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="userDataSubmit" var="userDataSubmit" />

<portlet:actionURL name="providePreviewImage"
	windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"
	var="providePreviewImage" />

<portlet:actionURL name="termsCondictionsDisplay"
	var="termsCondictionsDisplay" />

<portlet:renderURL var="termsCondRenderURL"
	windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
	<portlet:param name="mvcPath"
		value="<%=PlakatohrPortlet.getTermsCondJsp()%>" />
	<portlet:param name="message" value="PlaktOhR Terms and Conditions" />
	<!-- TODO param löschen, Überreste von Code-Kopie aus Internet -->
</portlet:renderURL>

<style>
.termscond-popup {
	display: none;
	background: white;
	border-radius: 7px;
	bottom: 0;
	left: 0;
	margin: auto;
	overflow: scroll;
	position: fixed;
	right: 0;
	top: 0;
	max-width: 75%;
	max-height: 80%;
	z-index: 11;
	padding: 0 14px;
}

.overlay1 {
	width: 100%;
	background: #000;
	opacity: 0.4;
	height: 100%;
	position: fixed;
	top: 0;
	left: 0;
	display: none;
	z-index: 10;
}
</style>

<style>
div.PlakatOhR_BackgroundPreview img {
	border-top: 2px solid #dedede;
	border-left: 2px solid #dedede;
	border-right: 2px solid white;
	border-bottom: 2px solid white;
	border-radius: 1px;
	margin-bottom: 0px;
	max-height: 280px;
	max-width: 98%;
}

div.PlakatOhR_BackgroundPreview {
	background-color: #f4f4f4;
	border: 1px solid #cccccc;
	max-width: auto;
	margin: 4px;
	overflow: hidden;
	float: left;
}

div.formContainer {
	overflow: hidden;
}

div.RightColumn {
	float: left;
	max-width: 49%;
	padding-right: 10px;
}

div.PlakatOhR_BackgroundPreview_outer {
	float: left;
	width: 49%;
}

#<
portlet:namespace />spinnerOuter {
	display: block;
	top: 50%;
	position: absolute;
	width: 100%;;
	z-index: 100;
	background-color: #f4f4f4;
	height: 100%;
	opacity: .8;
	top: 0px !important;
	margin: 0px !important;
}

#<
portlet:namespace />spinner {
	margin-top: 80%;
}

.OhrPreviewOutmost {
	position: relative;
	max-width: 100%;
	overflow: hidden;
}

.aui .control-group.error .checkbox {
	color: #b50303;
}

.mimeError {
	color: #b50303;
	display: none;
}
</style>

<div class="OhrPreviewOutmost">

	<h2>Schritt 2: Bitte geben Sie die notwendigen Daten für den
		Inhalt Ihres Plakats an</h2>

	<%
		OhrMediaHelper media = new OhrMediaHelper();
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		int opinionMaxLen = GetterUtil
				.getInteger(portletPreferences.getValue(OhrConfigConstants.OPINION_MAX_LENGTH, ""), 100);

		String backgroundIDString = renderRequest.getParameter("backgroundID");
		String firstname = renderRequest.getParameter("firstname");
		String lastname = renderRequest.getParameter("lastname");
		String email = renderRequest.getParameter("email");
		String opinion = renderRequest.getParameter("opinion");
		String termsConditionsJsp = renderRequest.getParameter("termsConditionsJsp");
		long backgroundID = Long.parseLong(backgroundIDString);
		DLFileEntry background = DLFileEntryLocalServiceUtil.getDLFileEntry(backgroundID);

		String textBeginnings = GetterUtil
				.getString(portletPreferences.getValue(OhrConfigConstants.TEXT_BEGINNINGS, ""), "");
		List<String> textOptions = new LinkedList<String>();
		for (String line : textBeginnings.split("\\n")) {
			if (!line.isEmpty()) {
				textOptions.add(line.trim());
			}
		}

		// Terms & Conditions Display vorbereiten: Titel des WebContents holen
		String termsCondArticleId = GetterUtil
				.getString(portletPreferences.getValue(OhrConfigConstants.TERMS_COND_ARTICLE_ID, ""), "");

		long groupId = themeDisplay.getScopeGroupId();
		JournalArticle art = JournalArticleLocalServiceUtil.getArticle(groupId, termsCondArticleId);
		String termsCondTitle = art.getTitle(themeDisplay.getLocale(), true);
	%>

	<script type="text/javascript">

	function ohrDisplayTermsCond() {
		var actionURL = '<%=termsCondRenderURL%>';
			Liferay.Util.openWindow({
				id : '$<portlet:namespace />showTermsCond',
				title : '<%=termsCondTitle%>
		',
				uri : actionURL
			});
		}
	</script>


	<portlet:renderURL var="varURL">
		<portlet:param name="mvcPath" value="<second-JSP-URL>"></portlet:param>
		<portlet:param name="backURL"
			value="<%=themeDisplay.getURLCurrent()%>"></portlet:param>
	</portlet:renderURL>

	<form action="<%=userDataSubmit%>" method="post" id="form" name="form"
		enctype="multipart/form-data">

		<div class="formContainer">

			<div class="PlakatOhR_BackgroundPreview_outer">
				<div class="PlakatOhR_BackgroundPreview">
					<img src="<%=media.getDlFileUrl(themeDisplay, background)%>" />
				</div>
			</div>

			<div class="RightColumn">
				<label for="plakatohrFirstname">Vorname</label> <input
					id="plakatohrFirstname" name="firstname" placeholder="Vorname"
					style="max-width: 100%;" type="text" required>
				<%-- <aui:validator name="maxLength">35</aui:validator> --%>

				<label for="plakatohrLastname">Nachname</label> <input
					id="plakatohrLastname" name="lastname" placeholder="Nachname"
					style="max-width: 100%;" type="text" required>
				<%-- <aui:validator name="maxLength">35</aui:validator> --%>

				<label for="plakatohrEmail">E-Mail</label> <input
					id="plakatohrEmail " name="email" placeholder="E-Mail"
					style="max-width: 100%;" type="email" required>
				<%-- <aui:validator name="email" /> --%>

			</div>
		</div>

		<div style="max-width: 100%; padding-right: 20px;">

			<%
				String finalMessage = portletPreferences.getValue(OhrConfigConstants.INTRODUCTION_TEXT_HTML, "").trim();
				if (finalMessage != null && finalMessage != "") {
			%><div>
				<%
					response.getWriter().println(finalMessage);
				%>
			</div>
			<%
				}
			%>
			<label for="picture">Bild (bitte nur Bilder des Typs .jpg und
				.png angeben)</label> <input id="picture" name="picture" type="file"
				required>
			<%-- Falls der Validator rein soll <aui:validator name="acceptFiles">'jpg,png'</aui:validator> --%>
			<p class="mimeError">Es werden nur Bilder des Typs .jpg und .png
				unterstützt</p>
			<%
				if (textOptions != null && !textOptions.isEmpty()) {
			%>
			<label for="plakatohrTextbeginnings">Textanfang</label> <select
				id="plakatohrTextbeginnings" name="textBeginning"
				style="width: auto !important; max-width: 100%;" required>
				<%
					for (String option : textOptions) {
				%><option value="<%=option%>"><%=option%></option>
				<%
					}
				%>
				<option value="Freitext">(Freitext)</option>
			</select>
			<%
				}
			%>
			<label for="plakatohrOpinion">Meinung</label>
			<textarea id="plakatohrOpinion" name="opinion" placeholder="Meinung"
				style="width: 100%; max-width: 100%; height: 200px;" required></textarea>
			<%-- <input name="opinion" label="Meinung" placeholder="Meinung"
				required="<%=true%>" type="textarea"
				style="width:100%; max-width:100%; height:200px;"> --%>
			<%-- <aui:validator name="maxLength"><%=opinionMaxLen%></aui:validator> --%>
			<input name="backgroundID" value="<%=backgroundIDString%>"
				type="hidden">
		</div>

		<div>
			<input name="acceptTermsChkbox" class="acceptTerms"
				id="acceptTermsChkbox" type="checkbox" required>
			<%-- <aui:validator name="required"
					errorMessage="<p style='color:red;'>Bitte akzeptiere Sie die Nutzungsbedingungen</br></p>" />
					--%>

			Ich habe die <a onclick='showPopup();' href='javascript:void(0)'>Nutzungsbedingungen</a>
			gelesen und bin damit einverstanden.

		</div>


		<div id="<portlet:namespace />spinnerContainer"></div>

		<div>
			<%-- <button-row> --%>
			<button class="btn btn-cancel"
				value="Zurück: Hintergrundmotiv auswählen" onClick="history.go(-1)">Zurück:
				Hintergrundmotiv auswählen</button>
			<button class="btn btn-primary acceptTermsButton" id="buttonSubmit"
				name="buttonSubmit" type="submit" value="Weiter zur Vorschau">Weiter</button>
			<%-- </button-row> --%>
		</div>

	</form>

	<%-- <script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script> --%>

	<script>
	//We have to check for the mime type of the file selected mby the user to make sure it's one of the valid ones
	//Magic Numbers try to watch the header of the file so it is way harder to trick the validation
	//This is necessary because of files with a wrong ending or none at all
	// Return the first few bytes of the file as a hex string
	function getBLOBFileHeader(url, blob, callback) {
  		var fileReader = new FileReader();
  		fileReader.onloadend = function(e) {
    		var arr = (new Uint8Array(e.target.result)).subarray(0, 4);
    		var header = "";
    		for (var i = 0; i < arr.length; i++) {
      			header += arr[i].toString(16);
    		}
    		callback(url, header);
  		};
 		fileReader.readAsArrayBuffer(blob);
	}

	function headerCallback(url, headerString) {
  		if((printHeaderInfo(url, headerString) == "image/png") || 
			  (printHeaderInfo(url, headerString) == "image/jpeg")) {
		  	$(".mimeError").hide();
	  		$("button[id='buttonSubmit']").prop("type", "submit");
  		} else {
	  		$(".mimeError").show();
	  		$("button[id='buttonSubmit']").prop("type", "button");
  		}
	}

	function remoteCallback(url, blob) {
  		getBLOBFileHeader(url, blob, headerCallback);
	}

	// Add more from http://en.wikipedia.org/wiki/List_of_file_signatures
	function mimeType(headerString) {
  		switch (headerString) {
    		case "89504e47":
      			type = "image/png";
      			break;
    		case "ffd8ffe0":
    		case "ffd8ffe1":
    		case "ffd8ffe2":
    		case "ffd8ffdb":
      			type = "image/jpeg";
      			break;
    		default:
      			type = "unknown";
      			break;
  		}
  		return type;
	}

	function printHeaderInfo(url, headerString) {
  		return mimeType(headerString);
	}

	// Check for FileReader support
	if (window.FileReader && window.Blob) {
  		/* Handle local files */
  		$("#picture").on('change', function(event) {
    		var file = event.target.files[0];
    		remoteCallback(escape(file.name), file);
  		});
	}

	//functions to show or hide the terms and conditions
	function showPopup() {
		$(".termscond-popup").show();
		$(".overlay1").show();
	}
	function closePopup() {
		$(".termscond-popup").hide();
		$(".overlay1").hide();
	}
	</script>

	<%
		long groupID = themeDisplay.getScopeGroupId();
	%>

	<div class="termscond-popup">
		<liferay-ui:journal-article showTitle="true" groupId="<%=groupID%>"
			articleId="<%=termsCondArticleId%>" />
		<div align="center">
			<button class="btn btn-primary" onClick="closePopup();">Schließen</button>
		</div>
	</div>
	<div class="overlay1"></div>

	<script
		src="https://cdn.jsdelivr.net/jquery.validation/1.16.0/jquery.validate.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/jquery.validation/1.16.0/additional-methods.min.js"></script>
	<script>
		var form = $("#form");
		jQuery.validator.setDefaults({
			debug : true,
			success : "valid"
		});

		form
				.validate({
					rules : {
						firstname : {
							required : true
						},

						lastname : {
							required : true
						},

						email : {
							required : true,
							email : true
						},

						picture : {
							required : true,
						//accept: "image/jgp,image/jpeg,image/png"
						},

						opinion : {
							required : true
						},

						acceptTermsChkbox : {
							required : true
						}
					},

					messages : {
						firstname : {
							required : "<div style='color: #b50303;'>Dieses Feld ist erforderlich</div>"
						},

						lastname : {
							required : "<div style='color: #b50303;'>Dieses Feld ist erforderlich</div>"
						},

						email : {
							required : "<div style='color: #b50303;'>Dieses Feld ist erforderlich</div>",
							email : "<div style='color: #b50303;'>Bitte geben Sie eine gültige E-Mail Adresse an</div>"
						},

						picture : {
							required : "<div style='color: #b50303;'>Dieses Feld ist erforderlich</div>",
						//accept: "<div style='color: #b50303;'>Es werden nur Bilder des Typs .jpg und .png unterstützt</div>"
						},

						opinion : {
							required : "<div style='color: #b50303;'>Dieses Feld ist erforderlich</div>"
						},

						acceptTermsChkbox : {
							required : "<div style='color: #b50303;'>Bitte stimmen Sie den Nutzungsbedingungen zu</div>"
						}
					}
				});

		form
				.submit(function(e) {
					if (form.valid()) {
						e.preventDefault();
						$("#<portlet:namespace />spinnerContainer")
								.append(
										'<div id="<portlet:namespace />spinnerOuter"><div class="loading-animation" id="<portlet:namespace />spinner"></div></div>');
						this.submit();
					}
				});
	<%--
		AUI()
				.use(
						'aui-form-validator',
						function(A) {
							//var defaultFormValidator = A.config.FormValidator;

							//$('form')
							A
									.on(
											'submit',
											function(e) {
												e.preventDefault();
												$(
														"#<portlet:namespace />spinnerContainer")
														.append(
																'<div id="<portlet:namespace />spinnerOuter"><div class="loading-animation" id="<portlet:namespace />spinner"></div></div>');
											});
						});
		/*
		AUI().ready(function(A) {
			var confirmbt = A.one(".confirm");
			var visible = false;

			A.one('.acceptTerms').on('change', function() {
				visible = !visible;
				if (visible) {
					A.one("#buttonSubmit").set('disabled', false);
					A.one("#buttonSubmit").removeClass('disabled');

				} else {
					A.one("#buttonSubmit").set('disabled', true);
					A.one("#buttonSubmit").addClass('disabled');
				}

			});
		});
		 */
		 --%>
		
	</script>
</div>

