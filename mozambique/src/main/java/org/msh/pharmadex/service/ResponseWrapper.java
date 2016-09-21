package org.msh.pharmadex.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
/**
 * Fix bug for file download. Rewrite the cookie that indicate download finish!
 * Not used yet!!!! Timeout in java script instead
 * @author Alex Kurasoff
 *
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
	private final HttpServletRequest request;
	public ResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
	    super(response);
	    this.request = request;

	}

	@Override
	public void addCookie(Cookie cookie) {
	    if("primefaces.download".equals(cookie.getName())){
	        String refererHeader = request.getHeader("Referer");


	        String contextPath = request.getContextPath();          
	        String requestURI = request.getRequestURI();

	        String refererURI = refererHeader.substring(refererHeader.indexOf(contextPath));
	        if(!requestURI.equals(refererURI)){
	            String refererPath = refererURI.substring(0, refererURI.lastIndexOf("/"));

	            cookie.setPath(refererPath);


	        }

	    }

	    super.addCookie(cookie);
	}

}
