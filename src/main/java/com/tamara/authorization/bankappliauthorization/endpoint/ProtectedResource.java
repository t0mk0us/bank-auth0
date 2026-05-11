package com.tamara.authorization.bankappliauthorization.endpoint;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/resource")
@RequestScoped
public class ProtectedResource {

    @Inject
    private JsonWebToken principal;

    @GET
    @RolesAllowed("resource.read")
    @Path("/read")
    public String read() {
        return "Protected Resource accessed by : " + principal.getName();
    }

    @POST
    @RolesAllowed("resource.write")
    @Path("/write")
    public String write() {
        return "Protected Resource accessed by : " + principal.getName();
    }
}
