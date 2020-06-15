<%@page contentType="application/json" pageEncoding="UTF-8" isErrorPage="true"%>
<%
    if (exception != null) {
%>
{"status":false,"message":"服务器响应发生异常","detail":"<%=exception.getClass().getName()%>:<%=exception.getMessage()%>"}
<%
    }
    else {
        %>
{"status":false,"message":"服务器响应发生异常"}
<%    
    }
%>