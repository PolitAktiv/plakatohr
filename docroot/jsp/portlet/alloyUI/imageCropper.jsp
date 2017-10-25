<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/jsp/portlet/import.jsp"%>

<portlet:actionURL name="initializePlakatohr" var="initializePlakatohr" />
<portlet:actionURL name="userDataSubmit" var="userDataSubmit" />
<portlet:actionURL name="providePreviewImage"
	windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"
	var="providePreviewImage" />
	
	<%String userPicture = renderRequest.getParameter("userPicture");%>

    <div id="imageCropDiv">
      <img id="image-src" src="<%= userPicture %>"/>
    </div>
    <aui:script>
    YUI().use(
      'aui-image-cropper',
      function(Y) {
      
    /* Rendring image*/
        var imageCropper = new Y.ImageCropper(
          {
            srcNode: "#image-src"
          }
        ).render();
        
      
     /* Image crop button*/
        var image = Y.one('#image-src');
        var cropImgButton = Y.one('#crop-Button');
        var croppedImage = Y.one('#cropped-Image');
    /*Create button at runtime */
        cropImgButton.on(
          'click',
          function(event) {
            var cropRegion = imageCropper.get('region');
            croppedImage.setStyles(
              {
                'backgroundPosition': (-cropRegion.x) + 'px ' + (-cropRegion.y) + 'px',
                height: cropRegion.height,
                width: cropRegion.width
              }
            );
          }
        );
        croppedImage.show();
      }
    );
    </aui:script>