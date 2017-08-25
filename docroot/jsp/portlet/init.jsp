
<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %> 

<%@page import="com.liferay.portlet.documentlibrary.model.DLFolderConstants"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>

<liferay-theme:defineObjects />
<portlet:defineObjects />



<%
// get preferences (or defaults)
long sourceFolderId = GetterUtil.getLong(
		portletPreferences.getValue("sourceFolderID", StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
String sourceFolderName = StringPool.BLANK;

long targetFolderId = GetterUtil.getLong(
		portletPreferences.getValue("targetFolderID", StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
String targetFolderName = StringPool.BLANK;
    
    String portletId = PortletKeys.DOCUMENT_LIBRARY;
%>

<p>Hallo, ich bin ein Plakatohr!</p>

sourceFolderId=<%= sourceFolderId %><br />
targetFolderId=<%= targetFolderId %>

