<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="connect.db.ConnectMySQL"%>
<%@ page import="org.json.simple.*"%>

<%
	ConnectMySQL connect = ConnectMySQL.getInstance();
	request.setCharacterEncoding("UTF-8");

	JSONObject returns = connect.Read();

	out.clear();
	out.println(returns);
	out.flush();
%>