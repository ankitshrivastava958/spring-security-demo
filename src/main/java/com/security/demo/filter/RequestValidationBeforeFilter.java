package com.security.demo.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

public class RequestValidationBeforeFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpServletResponse httpResponse = (HttpServletResponse) res;
		String header = httpRequest.getHeader("Authorization");
		if(!StringUtils.isEmpty(header)) {
			header = header.trim();
			if(StringUtils.startsWithIgnoreCase(header, "Basic")) {
				byte[] base64 = header.substring(6).getBytes(StandardCharsets.UTF_8);
				byte[] decoded;
				try {
					decoded = Base64.getDecoder().decode(base64);
					String token = new String(decoded, StandardCharsets.UTF_8);
					int delimeter = token.indexOf(":");
					if(delimeter == -1) {
						throw new BadCredentialsException("Invalid basic authentication token");
					}
					String email = token.substring(0, delimeter);
					if(email.toLowerCase().contains("test")) {
						httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return;
					}
				} catch(IllegalArgumentException ex) {
					throw new BadCredentialsException("Failed to decode basic authentication token");
				}
			}
		}
		chain.doFilter(httpRequest, httpResponse);
	}

	
}
