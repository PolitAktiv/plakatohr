<%@page import="com.liferay.portal.theme.PortletDisplay"%>
<%@page import="com.liferay.portal.security.permission.PermissionChecker"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="publish" var="publish" />
<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="returnToUserDataFormular" var="returnToUserDataFormular" />

<%
OhrMediaHelper media = new OhrMediaHelper();
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

Long targetDirectoryID = GetterUtil.getLong(
		portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

//String jpgIDString = renderRequest.getParameter("picture");
String email = renderRequest.getParameter("email");
String lastname = renderRequest.getParameter("lastname");
String firstname = renderRequest.getParameter("firstname");
String opinion = renderRequest.getParameter("opinion");
String backgroundID = renderRequest.getParameter("backgroundID");
//long jpgID = Long.parseLong(jpgIDString);
//DLFileEntry jpg = DLFileEntryLocalServiceUtil.getDLFileEntry(jpgID);
String backURL = ParamUtil.getString(request, "backURL");

//ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);
//PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();
//PortletDisplay portletDisplay= themeDisplay.getPortletDisplay();
//String portletId= portletDisplay.getId();
String publishButton = "";
if (themeDisplay.isSignedIn()) {
	publishButton = "<aui:button type=\"submit\" value=\"Veröffentlichen\" />";
}
/*
if (permissionChecker.hasPermission(serviceContext.getScopeGroupId(), portletId, primaryKey, "ADD_DOCUMENT")) {
	publishButton = "<aui:button type='submit' value='Veröffentlichen' />";
}
*/
%>

<style>

div.PlakatOhR_FinalPreview img {
    border-top: 2px solid #dedede;
  border-left: 2px solid #dedede;
  border-right: 2px solid white;
  border-bottom: 2px solid white;
  border-radius:2px;
  margin-bottom:4px;
}

div.PlakatOhR_FinalPreview {
   background-color: #f4f4f4;
  border: 1px solid #cccccc;
  
  padding:10px !important;;
  margin:10px;
}

div.PlakatOhR_DownloadLinks {
	text-align: center;
	padding-top:10px;

}


</style>

<h2>Fertig! Sie können nun Ihr Plakat veröffentlichen oder herunterladen</h2>

<div class="PlakatOhR_FinalPreview">

<div class="PlaktOhR_Img">
<img src="<%= jpgDownloadUrl %>"/>
</div>

<div class="PlakatOhR_DownloadLinks">	
	Plakat herunterladen: 
	<a href="<%= jpgDownloadUrl %>"  target="_blank"><i class="icon-download-alt">&nbsp;</i>JPG-Datei</a>
	<a href="<%= pdfDownloadUrl %>"  target="_blank"><i class="icon-download-alt">&nbsp;</i>PDF-Datei</a>
</div>

</div>

<aui:form action="<%= publish %>" method="post" name="fm">
	<aui:input name="email" value="<%= email %>" type="hidden" />
	<aui:input name="lastname" value="<%= lastname %>" type="hidden" />
	<aui:input name="firstname" value="<%= firstname %>" type="hidden" />
	<aui:input name="opinion" value="<%= opinion %>" type="hidden" />
	<aui:input name="backgroundID" value="<%= backgroundID %>" type="hidden" />
    
    <aui:button-row>
    <aui:button type="cancel" value="Zurück: Daten ändern" onClick="history.go(-1)" />
   	<aui:button type="cancel" value="Verwerfen und neu anfangen" onClick="<%= initializePlakatohr %>" />
   	<%=publishButton%>
    </aui:button-row>
</aui:form>
