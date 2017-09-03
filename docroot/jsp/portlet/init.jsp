
<%@page import="com.liferay.portal.kernel.util.PropsKeys"%>
<%@page import="com.liferay.portal.kernel.util.PrefsPropsUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.politaktiv.portlet.plakatohr.configurator.OhrConfigConstants"%>
<%@page import="org.politaktiv.portlet.plakatohr.controller.OhrMediaHelper"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFileEntry"%>
<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %> 
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>

<%@page import="com.liferay.portlet.documentlibrary.model.DLFolderConstants"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>

<portlet:actionURL name="userDataSubmit" var="userDataSubmit" />

<liferay-theme:defineObjects />
<portlet:defineObjects />



<%

OhrMediaHelper media = new OhrMediaHelper();

ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

// get preferences (or defaults)
long sourceFolderId = GetterUtil.getLong(
		portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

long targetFolderId = GetterUtil.getLong(
		portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
    
    String portletId = PortletKeys.DOCUMENT_LIBRARY;
%>

<p>Hallo, ich bin ein Plakatohr!</p>

sourceFolderId=<%= sourceFolderId %><br />
targetFolderId=<%= targetFolderId %>

<p>Im Source folder leben diese Bilder:</p>

<%
for ( DLFileEntry entry : media.getBackgroundPreviews(sourceFolderId, themeDisplay)) {
	%><li><%= entry.getName() %>
	<%= entry.getFileEntryId() %>
	<%= entry.getExtension()%>
	<%= entry.getTitle()%>
	<img src="<%= media.getDlFileUrl(themeDisplay, entry)%>" />
	
	</li><%
}




/*
OhrMailHelper mailer = new OhrMailHelper();
HashMap<String,String> m = new HashMap<String,String>();
m.put("Name", "Hugo");
m.put("Meinung", "Lieber keine\n\nOder doch?");
mailer.sendMail(m, portletPreferences, themeDisplay, "info@politaktiv.org");
*/


%>

<aui:form action="<%= userDataSubmit %>" method="post" name="fm">
	<aui:input name="firstname" label="Vorname" placeholder="Vorname" required="<%= true %>" type="text" >
    	 <aui:validator name="alpha" />
    </aui:input>
    <aui:input name="lastname" label="Nachname" placeholder="Nachname" required="<%= true %>" type="text" >
    	 <aui:validator name="alpha" />
    </aui:input>
	<aui:input name="email" label="E-Mail" placeholder="E-Mail" required="<%= true %>" type="text" >
		<aui:validator name="email" />
	</aui:input>
	<aui:input name="oppinion" label="Meinung" placeholder="Meinung" required="<%= true %>" type="text" >
		<aui:validator name="maxLength">100</aui:validator>
	</aui:input>
	<aui:input name="picture" label="Bild" required="<%= true %>" type="file" >
		<aui:validator name="acceptFiles">'jpg,png'</aui:validator>
	</aui:input>
    
    <aui:button type="submit" />
</aui:form>