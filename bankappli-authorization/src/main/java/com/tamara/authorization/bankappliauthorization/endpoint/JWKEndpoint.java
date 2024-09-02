package com.tamara.authorization.bankappliauthorization.endpoint;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tamara.authorization.bankappliauthorization.model.AuthorizationCode;
import com.tamara.authorization.bankauthorization.util.PEMKeyUtils;

@Path("jwk")
@ApplicationScoped
public class JWKEndpoint {
	
	@Inject
	private AuthorizationCode authorizationCode;
	
	Config config = ConfigProvider.getConfig();

    @GET
    public Response getKey(@QueryParam("format") String format) throws Exception {
        //...
    	String signingkey = config.getValue("signingkey", String.class);
        String verificationkey = config.getValue("verificationkey", String.class);
        String pemEncodedRSAPrivateKey = PEMKeyUtils.readKeyAsString(signingkey);
        RSAKey rsaKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemEncodedRSAPrivateKey);
        
        String pemEncodedRSAPublicKey = PEMKeyUtils.readKeyAsString(verificationkey);
        if (format == null || format.equals("jwk")) {
            JWK jwk = JWK.parseFromPEMEncodedObjects(pemEncodedRSAPublicKey);
            return Response.ok(jwk.toJSONString()).type(MediaType.APPLICATION_JSON).build();
        } else if (format.equals("pem")) {
            return Response.ok(pemEncodedRSAPublicKey).build();
        }
		return null;

        //...
    }
    
    JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
    
    public JsonObject signToApp() throws Exception {
    
    Instant now = Instant.now();
    Long expiresInMin = 30L;
    Date in30Min = Date.from(now.plus(expiresInMin, ChronoUnit.MINUTES));
    
	String signingkey = config.getValue("signingkey", String.class);
    String verificationkey = config.getValue("verificationkey", String.class);
    String pemEncodedRSAPrivateKey = PEMKeyUtils.readKeyAsString(signingkey);
    RSAKey rsaKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemEncodedRSAPrivateKey);

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
    
    signedJWT.sign((JWSSigner) new RSASSASigner(rsaKey.toRSAPrivateKey()));
    String accessToken = signedJWT.serialize();
    
    return Json.createObjectBuilder()
    		  .add("token_type", "Bearer")
    		  .add("access_token", accessToken)
    		  .add("expires_in", expiresInMin * 60)
    		  .add("scope", authorizationCode.getApprovedScopes())
    		  .build();
  //...
    }

}
