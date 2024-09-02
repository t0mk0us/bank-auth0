package com.tamara.authorization.bankappliauthorization.endpoint;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tamara.authorization.bankappliauthorization.handler.AuthorizationGrantTypeHandler;
import com.tamara.authorization.bankappliauthorization.model.AuthorizationCode;
import com.tamara.authorization.bankappliauthorization.model.Client;
import com.tamara.authorization.bankappliauthorization.repository.ClientRepository;

@Path("token")
public class TokenEndpoint {

    List<String> supportedGrantTypes = Collections.singletonList("authorization_code");

    @Inject
    private ClientRepository clientRepo;

    @Inject
    Instance<AuthorizationGrantTypeHandler> authorizationGrantTypeHandlers;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response token(MultivaluedMap<String, String> params,
       @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) throws JOSEException {
    	
    	List<String> supportedGrantTypes = Collections.singletonList("authorization_code");
        
    	String grantType = params.getFirst("grant_type");
    	Objects.requireNonNull(grantType, "grant_type params is required");
    	if (!supportedGrantTypes.contains(grantType)) {
    	    JsonObject error = Json.createObjectBuilder()
    	      .add("error", "unsupported_grant_type")
    	      .add("error_description", "grant type should be one of :" + supportedGrantTypes)
    	      .build();
    	    return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    	}
    	
      	String[] clientCredentials = authHeader.split(":");
      	
      	String clientId = clientCredentials[0];
      	String clientSecret = clientCredentials[1];
      	Client client = clientRepo.getClient(clientId);
      	if (client == null || clientSecret == null || !clientSecret.equals(client.getClientSecret())) {
      	    JsonObject error = Json.createObjectBuilder()
      	      .add("error", "invalid_client")
      	      .build();
      	    return Response.status(Response.Status.UNAUTHORIZED)
      	      .entity(error).build();
      	}
      	
        AuthorizationGrantTypeHandler authorizationGrantTypeHandler = 
  			  authorizationGrantTypeHandlers.select(NamedLiteral.of(grantType)).get();
    	
    	return null;
    }
   
}
