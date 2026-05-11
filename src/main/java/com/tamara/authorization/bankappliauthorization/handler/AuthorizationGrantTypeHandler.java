package com.tamara.authorization.bankappliauthorization.handler;

import javax.ws.rs.core.MultivaluedMap;

import com.nimbusds.oauth2.sdk.TokenResponse;

public interface AuthorizationGrantTypeHandler {
	
    TokenResponse createAccessToken(String clientId, MultivaluedMap<String, String> params) throws Exception;
}
