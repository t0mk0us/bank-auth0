package com.tamara.authorization.bankappliauthorization.repository;

import org.springframework.data.repository.CrudRepository;

import com.tamara.authorization.bankappliauthorization.model.Client;

public interface ClientRepository extends CrudRepository{
	
	default Client getClient(String clientId) {
		
		return null;
		
		
	}

}
