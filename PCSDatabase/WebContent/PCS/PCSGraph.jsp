<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="connect.db.ConnectMySQL"%>
<%@ page import="org.json.simple.*"%>

<%
	ConnectMySQL connect = ConnectMySQL.getInstance();
	request.setCharacterEncoding("UTF-8");

	JSONObject returns = connect.TotalGraph();

	out.clear();
	out.print(returns);
	out.flush();
%>