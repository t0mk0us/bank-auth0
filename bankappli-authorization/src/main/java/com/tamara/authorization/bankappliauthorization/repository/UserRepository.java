package com.tamara.authorization.bankappliauthorization.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tamara.authorization.bankappliauthorization.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

	default String checkUserScopes(String approvedScopes, List<String> requestedScope) {
		
		
		String newScopes = approvedScopes;
		
		for(String s : requestedScope) {
			
			newScopes += " " + s;
		}
			
		return newScopes;
	};

}
