package com.tamara.authorization.bankappliauthorization;


import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.Config;

import com.nimbusds.oauth2.sdk.TokenResponse;
//import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import com.tamara.authorization.bankappliauthorization.model.Client;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet(urlPatterns = "/callback")
public class ClientCallBackServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2024032701;
	
	@Inject
    private Config config;
	
	@Context
	
	@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader;
	
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
	public void doPost(@Context HttpServletRequest request, @Context ServletResponse response,
			MultivaluedMap<String, String> params) throws Exception {
    	
	String localState = (String) request.getSession().getAttribute("CLIENT_LOCAL_STATE");
	if (!localState.equals(request.getParameter("state"))) {
	    request.setAttribute("error", "The state attribute doesn't match!");
	    request.getRequestDispatcher("/authorize.jsp").forward(request, response);
	}
	
	String code = request.getParameter("code");
	javax.ws.rs.client.Client client = ClientBuilder.newClient();
	WebTarget webTarget = client.target(config.getValue("provider.tokenUri", String.class));
	WebTarget resourceWebTarget;

	Form form = new Form();
	form.param("grant_type", "authorization_code");
	form.param("code", code);
	form.param("redirect_uri", config.getValue("client.redirectUri", String.class));
	
	TokenResponse tokenResponse = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
	  .header(HttpHeaders.AUTHORIZATION, authHeader)
	  .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), TokenResponse.class);
	
	resourceWebTarget = webTarget.path("resource/read");
	Invocation.Builder invocationBuilder = resourceWebTarget.request();
	response = (ServletResponse) invocationBuilder
	  .header("authorization", tokenResponse);
	  //.get(String.class);
	}
}
