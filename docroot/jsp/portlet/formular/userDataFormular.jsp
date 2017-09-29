<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/jsp/portlet/import.jsp" %>

<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="userDataSubmit" var="userDataSubmit" />
<portlet:actionURL name="providePreviewImage" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>" var="providePreviewImage" />


<h2>Schritt 2: Bitte geben Sie die notwendigen Daten für den Inhalt Ihres Plakats an</h2>

<%
OhrMediaHelper media = new OhrMediaHelper();
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

String backgroundIDString = renderRequest.getParameter("backgroundID");
String firstname = renderRequest.getParameter("firstname");
String lastname = renderRequest.getParameter("lastname");
String email = renderRequest.getParameter("email");
String opinion = renderRequest.getParameter("opinion");
long backgroundID = Long.parseLong(backgroundIDString);
DLFileEntry background = DLFileEntryLocalServiceUtil.getDLFileEntry(backgroundID);
%>

<div>
	<img width="400" src="<%=media.getDlFileUrl(themeDisplay, background)%>"/>
</div>

<aui:form action="<%= userDataSubmit %>" method="post" name="fm" enctype="multipart/form-data">
	<aui:input name="picture" label="Bild" required="<%= true %>" type="file" >
		<aui:validator name="acceptFiles">'jpg,png'</aui:validator>
	</aui:input>
	<aui:input name="firstname" label="Vorname" placeholder="Vorname"  required="<%= true %>" type="text" >
    	 <aui:validator name="maxLength">35</aui:validator>
    </aui:input>
    <aui:input name="lastname" label="Nachname" placeholder="Nachname"  required="<%= true %>" type="text" >
    	 <aui:validator name="maxLength">35</aui:validator>
    </aui:input>
	<aui:input name="email" label="E-Mail" placeholder="E-Mail"  required="<%= true %>" type="text" >
		<aui:validator name="email" />
	</aui:input>
	<aui:input name="opinion" label="Meinung" placeholder="Meinung"  required="<%= true %>" type="textarea" >
		<aui:validator name="maxLength">140</aui:validator>
	</aui:input>
	<aui:input name="backgroundID" value="<%=backgroundIDString%>" type="hidden">
	</aui:input>
    
    <aui:button-row>
   	 <aui:button type="cancel" value="Zurück: Hintergrundmotiv auswählen" onClick="<%=  initializePlakatohr %>" />
    	<aui:button type="submit" value="Nächster Schritt: Vorschau" />
    </aui:button-row>
</aui:form>