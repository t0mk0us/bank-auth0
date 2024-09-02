package com.tamara.authorization.bankappliauthorization.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tamara.authorization.bankappliauthorization.model.AuthorizationCode;
import com.tamara.authorization.bankappliauthorization.model.User;

@Repository
public interface AuthorizationRepository extends CrudRepository<AuthorizationCode, String>  {
	
	AuthorizationCode save(AuthorizationCode authorizationCode);

}
