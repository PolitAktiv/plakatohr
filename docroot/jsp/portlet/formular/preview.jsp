<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="publish" var="publish" />
<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />

<%
//PortletSession s = renderRequest.getPortletSession();
//DLFileEntry jpgFileEntry = (DLFileEntry) s.getAttribute("picture");
OhrMediaHelper media = new OhrMediaHelper();
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

Long targetDirectoryID = GetterUtil.getLong(
		portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

String jpgIDString = renderRequest.getParameter("picture");
String email = renderRequest.getParameter("email");
long jpgID = Long.parseLong(jpgIDString);
DLFileEntry jpg = DLFileEntryLocalServiceUtil.getDLFileEntry(jpgID);
%>

<p>Fertig! Sie können nun Ihr Plakat herunterladen und veröffentlichen.</p>

<img src="<%=media.getDlFileUrl(themeDisplay, jpg)%>"/>

<aui:form action="<%= publish %>" method="post" name="fm" enctype="multipart/form-data">
	<aui:input name="plakatID" value="<%= jpgIDString %>" type="hidden">
	</aui:input>
	<aui:input name="email" value="<%= email %>" type="hidden">
	</aui:input>
    
    <aui:button-row>
   	 <aui:button type="cancel" value="Neues Plakat erstellen" onClick="<%= initializePlakatohr %>" />
    	<aui:button type="submit" value="Veröffentlichen" />
    </aui:button-row>
</aui:form>
