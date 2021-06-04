package com.security.demo.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

public class AuthorityUtils {

	public static List<SimpleGrantedAuthority> covertToAuthorities(String authorities) {
		if(!StringUtils.isEmpty(authorities)) {
			String[] arr = authorities.split(",");
			return Arrays.stream(arr).map(auth -> new SimpleGrantedAuthority(auth)).collect(Collectors.toList());
			
		}
		return Collections.emptyList();
	}
}
