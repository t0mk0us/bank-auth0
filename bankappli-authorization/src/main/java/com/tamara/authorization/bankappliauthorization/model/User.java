package com.tamara.authorization.bankappliauthorization.model;

import java.security.Principal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Principal {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "roles")
    private String roles;

    @Column(name = "scopes")
    private String scopes;
    
    private String name;


	public String getUserId() {
		
		return userId;
	}

	public void setUserId(String userId) {
		
		this.userId = userId;
	}

	public String getPassword() {
		
		return password;
	}

	public void setPassword(String password) {
		
		this.password = password;
	}

	public String getRoles() {
		
		return roles;
	}

	public void setRoles(String roles) {
		
		this.roles = roles;
	}

	public String getScopes() {
		
		return scopes;
	}

	public void setScopes(String scopes) {
		
		this.scopes = scopes;
	}
	
	public String checkUserScopes(String s1, List<String> s2) {
		
		String newScopes = s1;
		
		for(String s : s2) {
			
			newScopes += " " + s;
		}
			
		return newScopes;	
	}

	@Override
	public String getName() {

		return this.name;
	}
    // ...
}