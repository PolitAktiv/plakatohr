<%-- DEPRECATED --%>

<%@ include file="/jsp/portlet/import.jsp"%>

<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>

<%
	ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

	long groupID = themeDisplay.getScopeGroupId();

	String termsCondArticleId = GetterUtil
			.getString(portletPreferences.getValue(OhrConfigConstants.TERMS_COND_ARTICLE_ID, ""), "");
%>
<%-- 
<liferay-ui:journal-article showTitle="false" groupId="<%=groupID%>"
	articleId="<%=termsCondArticleId%>" />
--%>
<p>Hallo, ich bin ein Test</p>

<aui:button name="closeDialog" type="button" value="close" />
<aui:button type="button" onClick="testClose()">TeeestClose</aui:button>

<aui:script use="aui-base">
    A.one('#<portlet:namespace />closeDialog').on('click', function(event) {
    	Liferay.Util.getOpener().<portlet:namespace />closePopup(data, '<portlet:namespace />dialog');
    });
</aui:script>