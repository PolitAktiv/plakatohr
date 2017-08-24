
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="javax.portlet.PortletMode"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortletURL"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<% 
  
  ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
  
  long dlScopeGroupId = themeDisplay.getScopeGroupId();

  LiferayPortletURL documentLibURL = PortletURLFactoryUtil.create(request,
          PortletKeys.JOURNAL, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);

  documentLibURL.setWindowState(LiferayWindowState.POP_UP);

  documentLibURL.setPortletMode(PortletMode.VIEW);

  documentLibURL.setParameter("struts_action","/dynamic_data_mapping/select_document_library");

  documentLibURL.setParameter("groupId", String.valueOf(dlScopeGroupId));

     %>


<input type="hidden" name="<portlet:namespace/>documentLibraryInput"
	id="<portlet:namespace/>documentLibraryInput" />

<div class="profile-image">

	<img class="journal-image" hspace="0"
		id="<portlet:namespace/>article-image" src=""
		style="width: 82px; height: 83px" vspace="0" />

</div>

<div class="image-data">

	<label> <a style="text-decoration: none; cursor: pointer;"
		onclick="javaScript:<portlet:namespace/>showImages()"> Select a
			thumbnail image </a>

	</label>

</div>

<script charset="utf-8" type="text/javascript">

function <portlet:namespace/>showImages(){

	AUI().ready('aui-modal','aui-dialog-iframe','aui-overlay-manager','liferay-portlet-url', function(A) {
		var url = '<%= documentLibURL %>';

		window.myDialog = new A.Modal(
			{
				title: '',
				width: 1050,
				draggable: true,
				modal: true,
				xy: [150, 50],
				id:'documentDetails'
			}
		).plug(
			A.Plugin.DialogIframe,
				{
					uri: url,
					iframeCssClass: 'dialog-iframe'
				}
			).render();
		});
}

 

</script>


 <script charset="utf-8" type="text/javascript">

function _15_selectDocumentLibrary(url) {

document.getElementById("<portlet:namespace/>documentLibraryInput").value = url;

var pic1 = document.getElementById("<portlet:namespace/>article-image");

pic1.src = url;

var iframe = document.getElementById('documentDetails');

iframe.parentNode.removeChild(iframe);

}

</script>
