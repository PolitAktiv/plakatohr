<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="backgroundSelection" var="backgroundSelection" />

<p>Schritt 1: Bitte wählen Sie ein Hintergrundmotiv für Ihr Plakat aus</p>

<%
	OhrMediaHelper media = new OhrMediaHelper();

	ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

	long sourceFolderId = GetterUtil.getLong(
			portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

	String portletId = PortletKeys.DOCUMENT_LIBRARY;

	DLFileEntry last = null;
	Map<DLFileEntry, DLFileEntry> jpegsUndSVGs = media.getBackgroundPreviewsAndTemplates(sourceFolderId,
			themeDisplay);

	for (DLFileEntry entry : jpegsUndSVGs.keySet()) {%>
	<li><img width="300" src="<%=media.getDlFileUrl(themeDisplay, entry)%>" /><%=entry.getTitle().substring(0,entry.getTitle().lastIndexOf('.'))%></li>
<%
	last = entry;
	}
%>

<aui:form action="<%=backgroundSelection%>" method="post" name="fm">
	<aui:select name="background" label="Hintergrund auswählen" enctype="multipart/form-data">
	<%	for (DLFileEntry entry : jpegsUndSVGs.keySet()) {%>
	<aui:option value="<%=entry.getFileEntryId()%>"><%=entry.getTitle().substring(0,entry.getTitle().lastIndexOf('.'))%></aui:option>
<%}%>
	</aui:select>
	
	<aui:button type="submit" />
</aui:form>