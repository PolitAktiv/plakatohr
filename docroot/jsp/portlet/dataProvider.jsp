<%@page import="javax.portlet.PortletRequest"%>
<%@page language="java" contentType="image/jpeg"
    %>
<%@page import="java.io.OutputStream"%>
<%@page import="javax.portlet.PortletSession"%>
<%@page trimDirectiveWhitespaces="true" %>
<%

PortletRequest pR = (PortletRequest) request.getAttribute("javax.portlet.request");

PortletSession pS = pR.getPortletSession();

byte[] daten = (byte[])pS.getAttribute("daten");

OutputStream o = response.getOutputStream();
o.write(daten, 0, daten.length);
o.flush();
o.close();// *important* to ensure no more jsp output

return; 
%>