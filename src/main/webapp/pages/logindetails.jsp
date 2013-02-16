<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" isELIgnored="false" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.authentication.AnonymousAuthenticationToken" %>
<%
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth != null && (!AnonymousAuthenticationToken.class.isAssignableFrom(auth.getClass()))) {%>
		<div class="right"><a class="button" href="<c:url value="/j_spring_security_logout"/>"><span>Logout</span></a></div><div class="welcome">Welcome <sec:authentication property="principal.username"/>!</div>
	<%}
	else	{%>
		<div class="right"><a class="button" href="${pageContext.request.contextPath}/secure/welcome.jsp"><span>Login</span></a></div>
	<%}
%>