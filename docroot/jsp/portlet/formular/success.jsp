<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />

<div class="PlakatOhR_FinalFeedback">
<%
String finalMessage = portletPreferences.getValue(OhrConfigConstants.USER_FEEDBACK_HTML, StringPool.TRUE);

response.getWriter().println(finalMessage);

%>
</div>

<aui:form>
   	 <aui:button type="cancel" value="Ein weiteres Plakat entwerfen" onClick="<%= initializePlakatohr %>" />
</aui:form>