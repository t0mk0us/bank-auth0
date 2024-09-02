package com.tamara.authorization.bankappliauthorization.endpoint;

import java.io.IOException;
import java.security.KeyStore;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.inject.Inject;
import javax.inject.Scope;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tamara.authorization.bankappliauthorization.handler.AuthorizationGrantTypeHandler;
import com.tamara.authorization.bankappliauthorization.model.AuthorizationCode;
import com.tamara.authorization.bankappliauthorization.model.Client;
import com.tamara.authorization.bankappliauthorization.model.User;
import com.tamara.authorization.bankappliauthorization.repository.AuthorizationRepository;
import com.tamara.authorization.bankappliauthorization.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import javax.security.enterprise.authentication.mechanism.http.*;

import javax.ws.rs.Path;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

@FormAuthenticationMechanismDefinition(
		  loginToContinue = @LoginToContinue(loginPage = "/login.jsp", errorPage = "/login.jsp")
		)

@Path("authorize")
@ApplicationScoped
//public class FormAuthenticationConfig implements FormAuthenticationMechanism{
	
	public class AuthorizationEndpoint {
	@Inject
	private SecurityContext securityContext;
	
    @NotNull private String username;

    @NotNull private String password;
    
    private User user;
    
    List<String> supportedGrantTypes = Collections.singletonList("authorization_code");

    @Inject
    private AuthorizationRepository authorizationRepo;
    
	@Inject
	UserRepository userRepo;

    @Inject
    Instance<AuthorizationGrantTypeHandler> authorizationGrantTypeHandlers;
    
    //...    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response doGet(@Context HttpServletRequest request,
      @Context HttpServletResponse response,
      @Context UriInfo uriInfo) throws ServletException, IOException {
        
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        Principal principal = securityContext.getUserPrincipal();
        
        return (Response) response;
        // ...
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
	public Response doPost(@Context HttpServletRequest request, @Context HttpServletResponse response,
			MultivaluedMap<String, String> params) throws Exception {

		List<String> requestedScope = new ArrayList<String>();

		@SuppressWarnings("unchecked")
		MultivaluedMap<String, String> originalParams = (MultivaluedMap<String, String>) request.getSession()
				.getAttribute("ORIGINAL_PARAMS");

		// request.getSession().setAttribute("ORIGINAL_PARAMS", params);

		requestedScope = originalParams.get("scope");

		request.getRequestDispatcher(password);
		String allowedScopes = userRepo.checkUserScopes(user.getScopes(), requestedScope);

		request.setAttribute("scopes", allowedScopes);
		request.getRequestDispatcher("/authorize.jsp").forward(request, (ServletResponse) response);

		KeyStore store = KeyStore.getInstance("JKS");
		InputStream input = new FileInputStream("C:\\Users\\tomko\\.ssh\\id_rsa");
		store.load(input, "K1ev2oo1".toCharArray());

		// ...

		String approvalStatus = params.getFirst("approval_status"); // YES OR NO

		// ... if YES

		List<String> approvedScopes = params.get("scope");

		// ...

		String userId = params.getFirst("userId");
		String redirectUri = params.getFirst("redirectUri");
		String clientId = params.getFirst("clientId");

		AuthorizationCode authorizationCode = new AuthorizationCode();
		authorizationCode.setClientId(clientId);
		authorizationCode.setUserId(userId);
		authorizationCode.setApprovedScopes(String.join(" ", approvedScopes));
		authorizationCode.setExpirationDate(LocalDateTime.now().plusMinutes(2));
		authorizationCode.setRedirectUri(redirectUri);

		authorizationRepo.save(authorizationCode);
		String code = authorizationCode.getCode();

		StringBuilder sb = new StringBuilder(redirectUri);
		// ...

		sb.append("?code=").append(code);
		String state = params.getFirst("state");
		if (state != null) {
			sb.append("&state=").append(state);
		}

		URI location = UriBuilder.fromUri(sb.toString()).build();
		return Response.seeOther(location).build();

		// ...

	}
  
  private AuthorizationCode authorizationCode = new AuthorizationCode();
  
  JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
  
  Instant now = Instant.now();
  Long expiresInMin = 30L;
  Date in30Min = Date.from(now.plus(expiresInMin, ChronoUnit.MINUTES));

  JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
    .issuer("http://localhost:9080")
    .subject(authorizationCode.getUserId())
    .claim("upn", authorizationCode.getUserId())
    .audience("http://localhost:9280")
    .claim("scope", authorizationCode.getApprovedScopes())
    .claim("groups", Arrays.asList(authorizationCode.getApprovedScopes().split(" ")))
    .expirationTime(in30Min)
    .notBeforeTime(Date.from(now))
    .issueTime(Date.from(now))
    .jwtID(UUID.randomUUID().toString())
    .build();
  SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaims);

}
