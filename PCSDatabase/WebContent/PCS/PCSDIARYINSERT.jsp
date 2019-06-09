<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="connect.db.ConnectMySQL"%>

<%
	ConnectMySQL connect = ConnectMySQL.getInstance();
	request.setCharacterEncoding("UTF-8");

	//String NO = request.getParameter("NO");
	String CONTENT = request.getParameter("CONTENT");
	String DATESTAMP = request.getParameter("DATESTAMP");

	String insertResult = connect.insertDiary(CONTENT, DATESTAMP);

	System.out.println(CONTENT);
	System.out.println(DATESTAMP);

	out.clear();
	out.print(insertResult);
	out.flush();
%>