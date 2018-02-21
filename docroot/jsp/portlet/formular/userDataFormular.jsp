<%@page import="com.liferay.portal.kernel.util.LocaleUtil"%>
<%@page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil"%>
<%@page import="com.liferay.portlet.journal.model.JournalArticle"%>
<%@page
	import="org.politaktiv.portlet.plakatohr.controller.PlakatohrPortlet"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/jsp/portlet/import.jsp"%>

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
value="<%= PlakatohrPortlet.getTermsCondJsp() %>"/>
<portlet:param name="message" value="PlaktOhR Terms and Conditions"/> <!-- TODO param lˆschen, ‹berreste von Code-Kopie aus Internet -->
</portlet:renderURL>



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
}

.aui .control-group.error .checkbox {
	color: #b50303;
}
</style>

<div class="OhrPreviewOutmost">

	<h2>Schritt 2: Bitte geben Sie die notwendigen Daten f√ºr den
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
        String termsCondTitle = art.getTitle(themeDisplay.getLocale(),true);


		
		
	%>

	<script type="text/javascript">

	function ohrDisplayTermsCond() {
		var actionURL = '<%=termsCondRenderURL%>';
			Liferay.Util.openWindow({
				id : '$<portlet:namespace />showTermsCond',
				title : '<%= termsCondTitle %>',
				uri : actionURL
			});
		}
	</script>


	<portlet:renderURL var="varURL">
		<portlet:param name="mvcPath" value="<second-JSP-URL>"></portlet:param>
		<portlet:param name="backURL"
			value="<%=themeDisplay.getURLCurrent()%>"></portlet:param>
	</portlet:renderURL>

	<aui:form action="<%=userDataSubmit%>" method="post" id="form"
		name="form" enctype="multipart/form-data">

		<div class="formContainer">

			<div class="PlakatOhR_BackgroundPreview_outer">
				<div class="PlakatOhR_BackgroundPreview">
					<img src="<%=media.getDlFileUrl(themeDisplay, background)%>" />
				</div>
			</div>

			<div class="RightColumn">
				<aui:input id="fn" name="firstname" label="Vorname"
					placeholder="Vorname" style="max-width:100%;" required="<%=true%>"
					type="text">
					<aui:validator name="maxLength">35</aui:validator>
				</aui:input>
				<aui:input name="lastname" label="Nachname" placeholder="Nachname"
					style="max-width:100%;" required="<%=true%>" type="text">
					<aui:validator name="maxLength">35</aui:validator>
				</aui:input>
				<aui:input name="email" label="E-Mail" placeholder="E-Mail"
					style="max-width:100%;" required="<%=true%>" type="text">
					<aui:validator name="email" />
				</aui:input>
			</div>
		</div>

		<div>
			<div>
				<%
					String finalMessage = portletPreferences.getValue(OhrConfigConstants.INTRODUCTION_TEXT_HTML, "").trim();
						if (finalMessage != null && finalMessage != "") {
							response.getWriter().println(finalMessage);
						}
				%>
			</div>
			<aui:input id="pic" name="picture" label="Bild" required="<%=true%>"
				type="file">
				<aui:validator name="acceptFiles">'jpg,png'</aui:validator>
			</aui:input>
			<%
				if (textOptions != null && !textOptions.isEmpty()) {
			%>
			<aui:select label="Text Anfang" name="textBeginning"
				style="max-width:100%;" required="<%=true%>">
				<%
					for (String option : textOptions) {
				%><aui:option value="<%=option%>"><%=option%></aui:option>
				<%
					}
				%>
				<aui:option value="Freitext">Freitext</aui:option>
			</aui:select>
			<%
				}
			%>
			<aui:input name="opinion" label="Meinung" placeholder="Meinung"
				required="<%=true%>" type="textarea"
				style="width:100%; max-width:100%; height:200px;">
				<aui:validator name="maxLength"><%=opinionMaxLen%></aui:validator>
			</aui:input>
			<aui:input name="backgroundID" value="<%=backgroundIDString%>"
				type="hidden">
			</aui:input>
		</div>

		<div>
			<aui:input name="" cssClass="acceptTerms" id="acceptTermsChkbox"
				type="checkbox" required="<%=true%>">
				<aui:validator name="required" errorMessage="<p style='color:red;'>Bitte akzeptiere Sie die Nutzungsbedingungen</br></p>" />
			</aui:input>
			<div>
				Ich habe die <a href="JavaScript:ohrDisplayTermsCond();">Nutzungsbedingungen</a>
				gelesen und bin damit einverstanden.
			</div>
		</div>


		<div id="<portlet:namespace />spinnerContainer"></div>

		<div>
			<aui:button-row>
				<aui:button type="cancel" value="Zur√ºck: Hintergrundmotiv ausw√§hlen"
					onClick="history.go(-1)" />
				<aui:button cssClass="acceptTermsButton" disabled="false"
					id="buttonSubmit" type="submit"></aui:button>
			</aui:button-row>
		</div>

	</aui:form>




	<script type="text/javascript">
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
												//this.submit();
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
	</script>
</div>

