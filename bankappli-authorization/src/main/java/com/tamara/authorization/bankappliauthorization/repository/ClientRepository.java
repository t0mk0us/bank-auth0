package com.tamara.authorization.bankappliauthorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tamara.authorization.bankappliauthorization.model.AuthorizationCode;
import com.tamara.authorization.bankappliauthorization.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String>, JpaSpecificationExecutor<Client> {

	default Client getClient(String clientId) {
		
		return getReferenceById(clientId);
	};

}
