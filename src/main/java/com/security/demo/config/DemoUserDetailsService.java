package com.security.demo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.CollectionUtils;

import com.security.demo.entity.Customer;
import com.security.demo.repository.CustomerRepository;

@Configuration
public class DemoUserDetailsService implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Customer> customers = customerRepository.findByEmail(username);
		if(CollectionUtils.isEmpty(customers)) {
			throw new UsernameNotFoundException("User does not exist in system");
		}
		
		return new SecurityCustomer(customers.get(0));
	}

}
