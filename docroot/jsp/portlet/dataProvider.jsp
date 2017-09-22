<%@ include file="/jsp/portlet/import.jsp" %>

<%@page import="javax.portlet.PortletSession"%>
<%@ page language="java" contentType="text/plain; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

PortletSession pS = renderRequest.getPortletSession();





%>
<%=pS.getAttribute("testTextText") %>

