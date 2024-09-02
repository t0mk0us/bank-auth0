package com.tamara.authorization.bankappliauthorization.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.inject.Named;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Named("authorization_code")
@Entity
@Table(name ="authorization_code")
public class AuthorizationCode {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "code")
	private String code;
	
	@Column(name = "client_id")
	private String clientId;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "approved_scopes")
	private String approvedScopes;
	
	@Column(name = "expiration_date")
	private LocalDateTime expirationDate;
	
	@Column(name = "redirect_uri")
	private String redirectUri;

	public String getCode() {
		
		return code;
	}

	public void setCode(String code) {
		
		this.code = code;
	}

	public String getClientId() {
		
		return clientId;
	}

	public void setClientId(String clientId) {
		
		this.clientId = clientId;
	}

	public String getUserId() {
		
		return userId;
	}

	public void setUserId(String userId) {
		
		this.userId = userId;
	}

	public String getApprovedScopes() {
		
		return approvedScopes;
	}

	public void setApprovedScopes(String approvedScopes) {
		
		this.approvedScopes = approvedScopes;
	}

	public LocalDateTime getExpirationDate() {
		
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime localDateTime) {
		
		this.expirationDate = localDateTime;
	}

	public String getRedirectUri() {
		
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		
		this.redirectUri = redirectUri;
	}
	
	//...

}
