<%@ include file="/jsp/portlet/import.jsp" %>

<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />

<p>Der Plakatohr ist ein ganz tolles Tool, mit dem Sie Ihre eigenen Plakate mit Meinungen erstellen können. Hier ein schönes Beispiel bla bla bla...</p>

<aui:button type="button" value="Plakat erstellen" onClick="<%=initializePlakatohr%>"/>