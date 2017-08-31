
<%@page import="org.politaktiv.portlet.plakatohr.configurator.OhrConfigConstants"%>
<%@page
	import="org.politaktiv.portlet.plakatohr.controller.OhrMediaHelper"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>
<%@page
	import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/security"
	prefix="liferay-security"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page
	import="com.liferay.portlet.documentlibrary.model.DLFolderConstants"%>

<% // das hier lädt die "implicit Objects", die dann in Eclipse trotzdem rote Bobbels kriegen %>
<liferay-theme:defineObjects />
<portlet:defineObjects />
<liferay-portlet:actionURL portletConfiguration="true"
	var="configurationURL" />



<% 


final String sourceFolderLabel = "Vorlagen-Ordner:";
final String sourceFolderHelp = "In diesem Ordner liegen die Vorlagen (Hintergründe) " + 
	"für die Kampagne dieses PlakatOhR-Portlets sowohl als JPG-Dateien " +
	"wie auch die dazugehörigen SVG-Dateien.";

final String targetFolderLabel = "Ziel-Ordner:";
final String targetFolderHelp = "In diesem Ordner werden die fertigen Plakate abgespeichert, die von den Benutzern erstellt werden.";	


final String eMailRecipientLabel = "E-Mail-Empfänger:";
final String eMailSubjectLabel = "E-Mail-Betreff:";
final String eMailIntroText = "Wenn ein neues Plakat in der Medien-Bibliothek abgelegt wird, kann automatisch eine E-Mail verschickt werden." +
	" Füllen Sie hierzu die folgenden Fehlder aus:";


%>


<%@ page contentType="text/html; charset=UTF-8"%>

<%

OhrMediaHelper media = new OhrMediaHelper();

// get source folder ID, or use default
long sourceFolderId = GetterUtil.getLong(
		portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
String sourceFolderName = StringPool.BLANK;
String tmpName = media.getFolderName(sourceFolderId);
if (tmpName != null) {
	sourceFolderName = tmpName;
}
	
//get source target ID, or use default
long targetFolderId = GetterUtil.getLong(
		portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
String targetFolderName = StringPool.BLANK;
tmpName = media.getFolderName(targetFolderId);
if (tmpName != null) {
	targetFolderName = tmpName;
}

// get preference values for e-mail
String eMailRecipient = GetterUtil.getString(
		portletPreferences.getValue(OhrConfigConstants.E_MAIL_RECIPIENT, ""),
		"");
String eMailSubject = GetterUtil.getString(
		portletPreferences.getValue(OhrConfigConstants.E_MAIL_SUBJECT, ""),
		"");




    
String portletId = PortletKeys.DOCUMENT_LIBRARY;
%>


<liferay-portlet:renderURL portletName="<%=portletId%>"
	var="selectFolderURL"
	windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="struts_action"
		value='/document_library/select_folder' />
</liferay-portlet:renderURL>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden"
		value="<%= Constants.UPDATE %>" />

	<aui:layout>
			<aui:input name="<%= OhrConfigConstants.SOURCE_FOLDER_ID %>" type="hidden"
				value="<%= sourceFolderId %>" />
			<aui:field-wrapper
				label="<%= HtmlUtil.escapeAttribute(sourceFolderLabel) %>"
				helpMessage="<%= HtmlUtil.escapeAttribute(sourceFolderHelp) %>">
				<div class="input-append">
					<liferay-ui:input-resource id="sourceFolderName"
						url="<%=sourceFolderName%>" />

					<aui:button name="sourceFolderSelectorButton" value="select" />

					<%
                String taglibRemoveFolder = "Liferay.Util.removeFolderSelection('" + OhrConfigConstants.SOURCE_FOLDER_ID + "', 'sourceFolderName', '" + renderResponse.getNamespace() + "');";
                %>

					<aui:button disabled="<%= sourceFolderId <= 0 %>"
						name="removeSourceFolderButton"
						onClick="<%= taglibRemoveFolder %>" value="remove" />
				</div>
			</aui:field-wrapper>

			<aui:input name="<%= OhrConfigConstants.TARGET_FOLDER_ID %>" type="hidden"
				value="<%= targetFolderId %>" />
			<aui:field-wrapper
				label="<%= HtmlUtil.escapeAttribute(targetFolderLabel) %>"
				helpMessage="<%= HtmlUtil.escapeAttribute(targetFolderHelp) %>">
				<div class="input-append">
					<liferay-ui:input-resource id="targetFolderName"
						url="<%=targetFolderName%>" />

					<aui:button name="targetFolderSelectorButton" value="select" />

					<%
                String taglibRemoveFolder = "Liferay.Util.removeFolderSelection('" + OhrConfigConstants.TARGET_FOLDER_ID + "', 'targetFolderName', '" + renderResponse.getNamespace() + "');";
                %>

					<aui:button disabled="<%= targetFolderId <= 0 %>"
						name="removeTargetFolderButton"
						onClick="<%= taglibRemoveFolder %>" value="remove" />
				</div>
			</aui:field-wrapper>

				<p><%= HtmlUtil.escape(eMailIntroText) %>

				<aui:input type="email" name="<%=  OhrConfigConstants.E_MAIL_RECIPIENT %>" 
					label="<%= HtmlUtil.escapeAttribute(eMailRecipientLabel) %>"
					value="<%= HtmlUtil.escapeAttribute(eMailRecipient) %>" />

				<aui:input type="text" 
					name="<%=  OhrConfigConstants.E_MAIL_SUBJECT %>" 
					label="<%= HtmlUtil.escapeAttribute(eMailSubjectLabel) %>" 
					value="<%= HtmlUtil.escapeAttribute(eMailSubject) %>" />


	</aui:layout>


	<aui:button-row>
		<aui:button type="submit" />
		<aui:button name="cancelButton" type="button" value="Abbrechen" />
	</aui:button-row>




</aui:form>

<aui:script use="aui-base">

    A.one('#<portlet:namespace />sourceFolderSelectorButton').on(
        'click',
        function(event) {
            Liferay.Util.selectEntity(
                {
                    dialog: {
                        constrain: true,
                        modal: true
                    },
                    id: '_<%= HtmlUtil.escapeJS(portletId) %>_selectFolder',
                    title: '<liferay-ui:message arguments="folder" key="select-x" />',
                    uri: '<%= selectFolderURL.toString() %>'
                },
                function(event) {
                    var folderData = {
                        idString: '<%= OhrConfigConstants.SOURCE_FOLDER_ID %>',
                        idValue: event.folderid,
                        nameString: 'sourceFolderName',
                        nameValue: event.foldername
                    };
                    Liferay.Util.selectFolder(folderData, '<portlet:namespace />');
                }
            );
        });
    
    
    A.one('#<portlet:namespace />targetFolderSelectorButton').on(
            'click',
            function(event) {
                Liferay.Util.selectEntity(
                    {
                        dialog: {
                            constrain: true,
                            modal: true
                        },
                        id: '_<%= HtmlUtil.escapeJS(portletId) %>_selectFolder',
                        title: '<liferay-ui:message arguments="folder" key="select-x" />',
                        uri: '<%= selectFolderURL.toString() %>'
                    },
                    function(event) {
                        var folderData = {
                            idString: '<%= OhrConfigConstants.TARGET_FOLDER_ID %>',
                            idValue: event.folderid,
                            nameString: 'targetFolderName',
                            nameValue: event.foldername
                        };
                        Liferay.Util.selectFolder(folderData, '<portlet:namespace />');
                    }
                );
            });
    
</aui:script>
