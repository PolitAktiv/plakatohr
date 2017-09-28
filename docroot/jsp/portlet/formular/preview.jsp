<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="publish" var="publish" />
<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="backgroundSelection" var="backgroundSelection" />

<%
OhrMediaHelper media = new OhrMediaHelper();
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

Long targetDirectoryID = GetterUtil.getLong(
		portletPreferences.getValue(OhrConfigConstants.TARGET_FOLDER_ID, StringPool.TRUE),
		DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

//String jpgIDString = renderRequest.getParameter("picture");
String email = renderRequest.getParameter("email");
String lastname = renderRequest.getParameter("lastname");
String firstname = renderRequest.getParameter("firstname");
//long jpgID = Long.parseLong(jpgIDString);
//DLFileEntry jpg = DLFileEntryLocalServiceUtil.getDLFileEntry(jpgID);
%>

<style>

div.PlakatOhR_FinalPreview img {
    border-top: 2px solid #dedede;
  border-left: 2px solid #dedede;
  border-right: 2px solid white;
  border-bottom: 2px solid white;
  border-radius:2px;
  margin-bottom:4px;
}

div.PlakatOhR_FinalPreview {
   background-color: #f4f4f4;
  border: 1px solid #cccccc;
  
  padding:10px !important;;
  margin:10px;
}

div.PlakatOhR_DownloadLinks {
	text-align: center;
	padding-top:10px;

}


</style>

<h2>Fertig! Sie können nun Ihr Plakat veröffentlichen oder herunterladen</h2>

<div class="PlakatOhR_FinalPreview">

<div class="PlaktOhR_Img">
<img src="<%= jpgDownloadUrl %>"/>
</div>

<div class="PlakatOhR_DownloadLinks">	
	Plakat herunterladen: <a href="<%= jpgDownloadUrl %>"><i class="icon-download-alt">&nbsp;</i>JPG-Datei</a>
	<a href="<%= pdfDownloadUrl %>"><i class="icon-download-alt">&nbsp;</i>PDF-Datei</a>
</div>

</div>

<aui:form action="<%= publish %>" method="post" name="fm" enctype="multipart/form-data">
	<aui:input name="email" value="<%= email %>" type="hidden" />
	<aui:input name="lastname" value="<%= lastname %>" type="hidden" />
	<aui:input name="firstname" value="<%= firstname %>" type="hidden" />


    
    <aui:button-row>
    <aui:button type="cancel" value="Zurück: Daten ändern" onClick="<%= backgroundSelection %>" />
   	 <aui:button type="cancel" value="Verwerfen und neu anfangen" onClick="<%= initializePlakatohr %>" />
    	<aui:button type="submit" value="Veröffentlichen" />
    </aui:button-row>
</aui:form>
