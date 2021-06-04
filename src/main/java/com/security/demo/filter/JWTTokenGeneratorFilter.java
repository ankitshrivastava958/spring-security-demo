package com.security.demo.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.demo.constants.SecurityConstants;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!Objects.isNull(authentication)) {
			SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
			String jwt = Jwts.builder().setIssuer("security demo").setSubject("JWT Token")
					.claim("username", authentication.getName())
					.claim("authorities", populateAuthorities(authentication.getAuthorities()))
					.setIssuedAt(new Date())
					.setExpiration(new  Date(new Date().getTime() + 30000))
					.signWith(key).compact();
			response.setHeader(SecurityConstants.JWT_HEADER, jwt);
		}
		chain.doFilter(request, response);
	}

	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return !request.getServletPath().equals("/user");
	}
	
	private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
		if(!CollectionUtils.isEmpty(authorities)) {
			 return authorities.stream().map(authority -> authority.getAuthority()).collect(Collectors.joining(","));
		}
		return null;
		
	}
	
}
