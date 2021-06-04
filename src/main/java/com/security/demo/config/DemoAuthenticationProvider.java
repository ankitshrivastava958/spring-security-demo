package com.security.demo.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;

import com.security.demo.entity.Authority;
import com.security.demo.entity.Customer;
import com.security.demo.repository.CustomerRepository;

@Configuration
public class DemoAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		List<Customer> customers = customerRepository.findByEmail(username);
		if(CollectionUtils.isEmpty(customers)) {
			throw new BadCredentialsException("User does not exist in system");
		}
		if (passwordEncoder.matches(pwd, customers.get(0).getPwd())) {
			Set<Authority> authorities = customers.get(0).getAuthorities();
			List<GrantedAuthority> simpleAuthorities = authorities.stream()
					.map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
			return new UsernamePasswordAuthenticationToken(username, pwd, simpleAuthorities);
		} 
		throw new BadCredentialsException("Invalid Credentials");

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
