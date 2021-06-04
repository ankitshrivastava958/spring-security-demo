package com.security.demo.config;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.security.demo.filter.AuthoritiesLoggingAfterFilter;
import com.security.demo.filter.AuthoritiesLoggingAtFilter;
import com.security.demo.filter.JWTTokenGeneratorFilter;
import com.security.demo.filter.JWTTokenValidatorFilter;
import com.security.demo.filter.RequestValidationBeforeFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * 
	 * 
	 * 
	 * notices -> not secure
	 * contact -> not secure
	 */	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/**
		 * Default Configuration
		 */
		/*
		http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
		http.formLogin();
		http.httpBasic();
		*/
		
		
		/**
		 * Custom Configutation
		 */
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // disabled the default JSESSIONID from spring
		.and()
		.cors().configurationSource(new CorsConfigurationSource() {
			
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration corsConfig = new CorsConfiguration();
				corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
				corsConfig.setAllowedMethods(Collections.singletonList("*"));
				corsConfig.setAllowedHeaders(Collections.singletonList("*"));
				corsConfig.setAllowCredentials(true);
				corsConfig.setExposedHeaders(Arrays.asList("Authorization")); //exposing authorization header to UI, to leverage the generated token
				corsConfig.setMaxAge(3600L);
				return corsConfig;
			}
		}).and()
		.csrf().disable() //disabled the csrf for JWT
		// .csrf().ignoringAntMatchers("/contact").csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and() //CSRF token handeling with Cookie and ignoring contact
		.addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
		.addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
		.addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
		.addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
		.addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
		.authorizeRequests()
	//	.antMatchers("/myAccount").authenticated() //Only authentication enabled 
	//	.antMatchers("/myBalance").authenticated() //Only authentication enabled
	//	.antMatchers("/myCards").authenticated() //Only authentication enabled
	//	.antMatchers("/myAccount").hasAuthority("WRITE") //Both authorization & authentication enabled 
	//	.antMatchers("/myBalance").hasAuthority("READ") //Both authorization & authentication enabled
	//	.antMatchers("/myCards").hasAuthority("DELETE") //Both authorization & authentication enabled
		.antMatchers("/myAccount").hasRole("USER") // only for USER role 
		.antMatchers("/myBalance").hasAnyRole("USER","ADMIN") //Both for USER and ADMIN role
		.antMatchers("/myLoans").authenticated() //method level security
		.antMatchers("/myCards").authenticated()
		.antMatchers("/contact").permitAll()
		.antMatchers("/notices").permitAll();		
		http.formLogin();
		http.httpBasic();
		
		/**
		 * Deny All configuration
		 */
	/*	http.authorizeRequests().anyRequest().denyAll();	
		http.formLogin();
		http.httpBasic();
		*/
		
		/**
		 * Permit All configuration
		 */
	/*	http.authorizeRequests().anyRequest().permitAll();	
		http.formLogin();
		http.httpBasic();
		*/
	}

	/**
	 * user declaration with inmemory authentication
	 */
/*	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("12345").authorities("admin").and()
		.withUser("user").password("12345").authorities("read").and()
		.passwordEncoder(NoOpPasswordEncoder.getInstance());
	}
	*/
	
	/**
	 * user decalaration with Inmemory user details manager
	 */
	
	/*
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		InMemoryUserDetailsManager userDetailService = new InMemoryUserDetailsManager();
		UserDetails user1 = User.withUsername("admin").password("12345").authorities("admin").build();
		UserDetails user2 = User.withUsername("user").password("12345").authorities("read").build();
		userDetailService.createUser(user1);
		userDetailService.createUser(user2);
		auth.userDetailsService(userDetailService);
		
	}
  */
	/**
	 * JdbcUserDetailsManager example
	 * 
	 */
/*	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource) {
		return new JdbcUserDetailsManager(dataSource);
	}*/
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	//	return NoOpPasswordEncoder.getInstance();
		return new BCryptPasswordEncoder();
	}
	
	
}
