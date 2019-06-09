<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="connect.db.ConnectMySQL"%>

<%
	ConnectMySQL connect = ConnectMySQL.getInstance();
	request.setCharacterEncoding("UTF-8");

	String NO = request.getParameter("NO");
	String CONTENT = request.getParameter("CONTENT");
	
	connect.updateDiary(NO, CONTENT);
	
	System.out.println(NO);
	System.out.println(CONTENT);
	
	
	out.clear();
	out.print("전송완료!");
	out.flush();
%>