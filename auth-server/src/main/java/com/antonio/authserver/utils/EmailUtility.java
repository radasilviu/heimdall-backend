package com.antonio.authserver.utils;
import javax.servlet.http.HttpServletRequest;
public class EmailUtility {
	public static String getSiteUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");
	}
}
