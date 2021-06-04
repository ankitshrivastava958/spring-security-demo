package com.security.demo.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.demo.constants.SecurityConstants;
import com.security.demo.utility.AuthorityUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String token = request.getHeader(SecurityConstants.JWT_HEADER);
		if (!StringUtils.isEmpty(token)) {
			try {
				SecretKey key = Keys.hmacShaKeyFor(
						SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
				Claims claims = Jwts.parserBuilder()
						.setSigningKey(key)
						.build()
						.parseClaimsJws(token)
						.getBody();
				
				String username = String.valueOf(claims.get("username"));
				String authorities = String.valueOf(claims.get("authorities"));
				Authentication auth = new UsernamePasswordAuthenticationToken(username, null,AuthorityUtils.covertToAuthorities(authorities));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception ex) {
				throw new BadCredentialsException("Invalid Token");
			}
		}
		
		chain.doFilter(request, response);
		
	}
	
	  @Override 
	  protected boolean shouldNotFilter(HttpServletRequest request) {
		  return request.getServletPath().equals("/user"); 
		  }

}
