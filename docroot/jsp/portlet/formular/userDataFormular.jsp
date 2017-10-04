<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="userDataSubmit" var="userDataSubmit" />
<portlet:actionURL name="providePreviewImage"
	windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"
	var="providePreviewImage" />


<style>

div.PlakatOhR_BackgroundPreview img {
    border-top: 2px solid #dedede;
  border-left: 2px solid #dedede;
  border-right: 2px solid white;
  border-bottom: 2px solid white;
  border-radius:2px;
  margin-bottom:4px;
  max-height:280px;
  width:auto;
}

div.PlakatOhR_BackgroundPreview {
   background-color: #f4f4f4;
  border: 1px solid #cccccc;
  
  padding:10px !important;;
  margin:10px;
  overflow:hidden;
  float:left;
}

div.PlakatOhR_BackgroundPreview_outer {
	width:100%;
	overflow:hidden;

}

#<portlet:namespace />spinner {
    display:none;
    
     top:50%; position:absolute;  width:100%;; z-index:100;
     
     
       background-color: white;
  height:100%;
  opacity:.8;
  top:0px !important;

  margin:0px !important;
  
     
}

.OhrPreviewOutmost {
	position: relative;
}

</style>

<div class="OhrPreviewOutmost">

<h2>Schritt 2: Bitte geben Sie die notwendigen Daten für den Inhalt
	Ihres Plakats an</h2>

<%
	OhrMediaHelper media = new OhrMediaHelper();
	ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

	int opinionMaxLen = GetterUtil
			.getInteger(portletPreferences.getValue(OhrConfigConstants.OPINION_MAX_LENGTH, ""), 100);

	String backgroundIDString = renderRequest.getParameter("backgroundID");
	String firstname = renderRequest.getParameter("firstname");
	String lastname = renderRequest.getParameter("lastname");
	String email = renderRequest.getParameter("email");
	String opinion = renderRequest.getParameter("opinion");
	long backgroundID = Long.parseLong(backgroundIDString);
	DLFileEntry background = DLFileEntryLocalServiceUtil.getDLFileEntry(backgroundID);
%>

<portlet:renderURL var="varURL"> 
<portlet:param name="mvcPath" value="<second-JSP-URL>"></portlet:param>
<portlet:param name="backURL" value="<%= themeDisplay.getURLCurrent() %>"></portlet:param>
</portlet:renderURL>


<script type="text/javascript">

function OhrShowSpinner() {
	document.getElementById("<portlet:namespace />spinner").style.display="block";
	
}


</script>



<div  class="PlakatOhR_BackgroundPreview_outer">
<div class="PlakatOhR_BackgroundPreview">
		<img src="<%=media.getDlFileUrl(themeDisplay, background)%>" />
</div>
</div>

<aui:form action="<%=userDataSubmit%>" method="post" id="fm" name="fm"
	enctype="multipart/form-data" onsubmit="OhrShowSpinner();">
	<aui:input id="pic" name="picture" label="Bild" required="<%=true%>"
		type="file" >
		<aui:validator name="acceptFiles">'jpg,png'</aui:validator>
	</aui:input>
	<aui:input id="fn" name="firstname" label="Vorname" placeholder="Vorname"
		required="<%=true%>" type="text">
		<aui:validator name="maxLength">35</aui:validator>
	</aui:input>
	<aui:input name="lastname" label="Nachname" placeholder="Nachname"
		required="<%=true%>" type="text">
		<aui:validator name="maxLength">35</aui:validator>
	</aui:input>
	<aui:input name="email" label="E-Mail" placeholder="E-Mail"
		required="<%=true%>" type="text">
		<aui:validator name="email" />
	</aui:input>
	<aui:input name="opinion" label="Meinung" placeholder="Meinung"
		required="<%=true%>" type="textarea">
		<aui:validator name="maxLength"><%=opinionMaxLen%></aui:validator>
	</aui:input>
	<aui:input name="backgroundID" value="<%=backgroundIDString%>"
		type="hidden">
	</aui:input>
	
<div class="loading-animation"  id="<portlet:namespace />spinner"></div>
	

	<aui:button-row>
		<aui:button type="cancel" value="Zurück: Hintergrundmotiv auswählen"
			onClick="history.go(-1)" />
		<aui:button type="submit" value="Nächster Schritt: Vorschau"  />
	</aui:button-row>
</aui:form>

<script type="text/javascript">
/*	document.getElementById("<portlet:namespace />pic").onchange = function() {
        document.getElementById("<portlet:namespace />fn").focus();
	}
*/
</script>
</div>

