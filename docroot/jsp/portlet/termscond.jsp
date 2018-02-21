<%@ include file="/jsp/portlet/import.jsp"%>

<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

long groupID = themeDisplay.getScopeGroupId();

String termsCondArticleId = GetterUtil.getString(portletPreferences.getValue(OhrConfigConstants.TERMS_COND_ARTICLE_ID, ""), "");

%>

<liferay-ui:journal-article showTitle="false" groupId="<%= groupID %>" articleId="<%= termsCondArticleId %>"/>
