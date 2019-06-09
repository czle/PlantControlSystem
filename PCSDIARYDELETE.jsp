<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="connect.db.ConnectMySQL"%>

<%
	ConnectMySQL connect = ConnectMySQL.getInstance();
	request.setCharacterEncoding("UTF-8");

	String NO = request.getParameter("NO");

	connect.deleteDiary(NO);

	System.out.println(NO);

	out.clear();
	out.print("삭제완료!");
	out.flush();
%>