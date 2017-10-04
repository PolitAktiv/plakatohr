<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="userDataSubmit" var="userDataSubmit" />
<portlet:actionURL name="providePreviewImage"
	windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"
	var="providePreviewImage" />

<script type="text/javascript">
	document.getElementById("pic").onchange = function() {
        document.getElementById("fn").focus();
	}

    function autofocus() {
        document.getElementById("fn").focus();
    }
</script>


<h2>Schritt 2: Bitte geben Sie die notwendigen Daten für den Inhalt
	Ihres Plakats an</h2>

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
	long backgroundID = Long.parseLong(backgroundIDString);
	DLFileEntry background = DLFileEntryLocalServiceUtil.getDLFileEntry(backgroundID);
%>

<portlet:renderURL var="varURL"> 
<portlet:param name="mvcPath" value="<second-JSP-URL>"></portlet:param>
<portlet:param name="backURL" value="<%= themeDisplay.getURLCurrent() %>"></portlet:param>
</portlet:renderURL>

<div>
	<img width="400"
		src="<%=media.getDlFileUrl(themeDisplay, background)%>" />
</div>

<aui:form action="<%=userDataSubmit%>" method="post" id="fm" name="fm"
	enctype="multipart/form-data">
	<aui:input id="pic" name="picture" label="Bild" required="<%=true%>"
		type="file" onChange="autofocus()">
		<aui:validator name="acceptFiles">'jpg,png'</aui:validator>
	</aui:input>
	<aui:input id="fn" name="firstname" label="Vorname" placeholder="Vorname"
		required="<%=true%>" type="text">
		<aui:validator name="maxLength">35</aui:validator>
	</aui:input>
	<aui:input name="lastname" label="Nachname" placeholder="Nachname"
		required="<%=true%>" type="text">
		<aui:validator name="maxLength">35</aui:validator>
	</aui:input>
	<aui:input name="email" label="E-Mail" placeholder="E-Mail"
		required="<%=true%>" type="text">
		<aui:validator name="email" />
	</aui:input>
	<aui:input name="opinion" label="Meinung" placeholder="Meinung"
		required="<%=true%>" type="textarea">
		<aui:validator name="maxLength"><%=opinionMaxLen%></aui:validator>
	</aui:input>
	<aui:input name="backgroundID" value="<%=backgroundIDString%>"
		type="hidden">
	</aui:input>

	<aui:button-row>
		<aui:button type="cancel" value="Zurück: Hintergrundmotiv auswählen"
			onClick="history.go(-1)" />
		<aui:button type="submit" value="Nächster Schritt: Vorschau" />
	</aui:button-row>
</aui:form>
