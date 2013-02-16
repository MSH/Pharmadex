<%@ page language="java" isELIgnored="false" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Login</title>
</head>
<body>

<div id="contentarea" >
	<div id="lb"><div id="rb"><div id="bb"><div id="blc">
	<div id="brc"><div id="tb"><div id="tlc"><div id="trc">
		<div id="content" class="content">
			<h1>Login with Username and Password</h1>
			<form name='f' action='${pageContext.request.contextPath}/j_spring_security_check' method='POST'>
			 <table>
			    <tr><td>User:</td><td><input type='text' name='j_username' value='<c:out value="${SPRING_SECURITY_LAST_USERNAME}"/>'></td></tr>
			    <tr><td>Password:</td><td><input type='password' name='j_password'/></td></tr> 
			    <tr><td><input type='checkbox' name='_spring_security_remember_me'/></td><td>Remember me on this computer.</td></tr> 
			    <tr><td colspan='2'><span class="inputbutton"><input class="button" name="submit" type="submit" value="Submit"/></span></td></tr> 
			  </table> 
			</form>
			<c:if test="${not empty param.login_error}">
				<div class="error">Your login attempt was not successful, try again.<br/>
				Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></div>
			</c:if>
		</div>
        <div>
            <p>You need to be a registered user of Pharamdex to register a product. Please follow the link to register yourself.
            <a href="${pageContext.request.contextPath}/pages/registeruser.faces"> Register me</a></p>

        </div>
	</div></div></div></div>
	</div></div></div></div>
</div>
</body>
</html> 