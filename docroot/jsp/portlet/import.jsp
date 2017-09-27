<%@page import="java.util.Map"%>
<%@page import="java.io.InputStream"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLAppServiceUtil"%>
<%@page import="com.liferay.portal.service.ServiceContextFactory"%>
<%@page import="com.liferay.portal.service.ServiceContext"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>
<%@page import="com.liferay.portal.kernel.util.PropsKeys"%>
<%@page import="com.liferay.portal.kernel.util.PrefsPropsUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.politaktiv.portlet.plakatohr.configurator.OhrConfigConstants"%>
<%@page import="org.politaktiv.portlet.plakatohr.controller.OhrMediaHelper"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFileEntry"%>
<%@page import="com.liferay.portal.util.PortletKeys"%>
<%@page import="javax.portlet.PortletSession"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %> 
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>

<%@page import="com.liferay.portlet.documentlibrary.model.DLFolderConstants"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>

<liferay-theme:defineObjects />
<portlet:defineObjects />