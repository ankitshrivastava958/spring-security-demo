package com.security.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.security.demo.entity.Contact;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {
	
	
}
