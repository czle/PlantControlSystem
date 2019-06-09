<%@page import="com.sun.xml.internal.bind.CycleRecoverable.Context"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="connect.db.ConnectMySQL"%>
<%@ page import="org.json.simple.*"%>
<!DOCTYPE html>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<%
	String returns = "";
	// returns 라는 String 성격의 객체를 선언 및 초기화
	String ip = "192.168.45.164"; // 아두이노 고유의 IP
	request.setCharacterEncoding("UTF-8");
	// 언어셋 설정
	String pin = request.getParameter("pinID");
	// pinID라는 이름을 가진 객체를 가져와 pin 이라는 String 객체에 넣음
	System.out.println(pin);
	try {

		if (pin.equals("11")) {
	returns = "11";
		} else if (pin.equals("12")) {
	returns = "12";
		} else if (pin.equals("13")) {
	returns = "13";
		} else if (pin.equals("14")) {
	returns = "14";
		} else if (pin.equals("15")) {
	returns = "15";
		} else if (pin.equals("16")) {
	returns = "16";
		} else if (pin.equals("17")) {
	returns = "17";
		} else if (pin.equals("18")) {
	returns = "18";
		}
		response.sendRedirect("http://" + ip + ":8008/" + "pin=" + returns);
		returns = "";
		pin = "";
		return;

	} catch (NullPointerException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}
%>




