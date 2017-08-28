
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="org.politaktiv.portlet.plakatohr.controller.MediaHelper"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFileEntry"%>
<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %> 

<%@page import="com.liferay.portlet.documentlibrary.model.DLFolderConstants"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>

<liferay-theme:defineObjects />
<portlet:defineObjects />



<%

MediaHelper media = new MediaHelper();

ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

// get preferences (or defaults)
long sourceFolderId = GetterUtil.getLong(
		portletPreferences.getValue("sourceFolderId", StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

long targetFolderId = GetterUtil.getLong(
		portletPreferences.getValue("targetFolderId", StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
    
    String portletId = PortletKeys.DOCUMENT_LIBRARY;
%>

<p>Hallo, ich bin ein Plakatohr!</p>

sourceFolderId=<%= sourceFolderId %><br />
targetFolderId=<%= targetFolderId %>

<p>Im Source folder leben diese Bilder:</p>

<%
for ( DLFileEntry entry : media.getBackgroundPreviews(sourceFolderId, themeDisplay)) {
	%><li><%= entry.getName()%></li><%
}

%>