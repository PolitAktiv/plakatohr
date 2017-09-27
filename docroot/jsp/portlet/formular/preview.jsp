<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="publish" var="publish" />
<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="backgroundSelection" var="backgroundSelection" />

<%
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
	
	<p>Plakat herunterladen: <a href=""><i class="icon-download-alt">&nbsp;</i>JPEG-Datei</a>
	<a href=""><i class="icon-download-alt">&nbsp;</i>PDF-Datei</a></p>
    
    <aui:button-row>
    <aui:button type="cancel" value="Zurück: Daten ändern" onClick="<%= backgroundSelection %>" />
   	 <aui:button type="cancel" value="Plakat verwerfen und neu anfangen" onClick="<%= initializePlakatohr %>" />
    	<aui:button type="submit" value="Veröffentlichen" />
    </aui:button-row>
</aui:form>
