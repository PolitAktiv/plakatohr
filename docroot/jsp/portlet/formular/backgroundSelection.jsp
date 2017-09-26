<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="backgroundSelection" var="backgroundSelection" />


<style>

div.PlakatOhR_BackgroundSelect label {

  display:flex;
  vertical-align: middle;
  width:30%;
  min-width:200px;
  flex-grow: 1;
  
    background-color: #f4f4f4;
  border: 1px solid #cccccc;
  
  padding:10px !important;;
  margin:10px;
  
}

div.PlakatOhR_BackgroundSelect label img {
    border-top: 2px solid #dedede;
  border-left: 2px solid #dedede;
  border-right: 2px solid white;
  border-bottom: 2px solid white;
  border-radius:2px;
  margin-bottom:4px;
}

div.PlakatOhR_BackgroundSelect  input {
  margin :5px !important;
  margin-right:15px !important;;
}

div.PlakatOhR_BackgroundSelect div.field-wrapper {
  display:flex;
  flex-direction: row;
  flex-wrap:wrap;
}
</style>



<h2>Schritt 1: Bitte w�hlen Sie ein Hintergrundmotiv f�r Ihr Plakat aus</h2>

<%
	OhrMediaHelper media = new OhrMediaHelper();

	ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

	long sourceFolderId = GetterUtil.getLong(
			portletPreferences.getValue(OhrConfigConstants.SOURCE_FOLDER_ID, StringPool.TRUE),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

	String portletId = PortletKeys.DOCUMENT_LIBRARY;

	Map<DLFileEntry, DLFileEntry> jpegsUndSVGs = media.getBackgroundPreviewsAndTemplates(sourceFolderId,
			themeDisplay);

%>


<aui:form action="<%=backgroundSelection%>" method="post" name="fm">

<div class="PlakatOhR_BackgroundSelect">
<aui:field-wrapper name="backgroundwrapper" label="">
		
	<%	for (DLFileEntry entry : jpegsUndSVGs.keySet()) {%>
		<%
		String imgTag = "<img src=\"" + media.getDlFileUrl(themeDisplay, entry) + "\" />";
		%>
	
		<aui:input inlineLabel="right" name="background" 
			type="radio" value="<%=entry.getFileEntryId()%>" label="<%= imgTag %>" />
		

<%}%>
</aui:field-wrapper>		
</div>		
	
	<aui:button type="submit" value="N�chster Schritt: Inhalt des Plakats" />
</aui:form>