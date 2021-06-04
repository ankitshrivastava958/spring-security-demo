package com.security.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.security.demo.entity.Customer;
import com.security.demo.entity.Loans;
import com.security.demo.repository.LoanRepository;

@RestController
public class LoansController {
	
	@Autowired
	private LoanRepository loanRepository;
	
	@PostMapping("/myLoans")
	@PostAuthorize("hasRole('ROOT')")
	public List<Loans> getLoanDetails(@RequestBody Customer customer) {
		List<Loans> loans = loanRepository.findByCustomerIdOrderByStartDtDesc(customer.getId());
		if (loans != null ) {
			return loans;
		}else {
			return null;
		}
	}

}
