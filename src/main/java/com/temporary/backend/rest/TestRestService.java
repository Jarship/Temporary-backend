package com.temporary.backend.rest;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("test")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TestRestService extends BaseRestService {

    public TestRestService() {
        System.out.println("---------- init TestRestService --");
    }
    @Path("")
    @GET
    @PermitAll
    public Response testAccount(@Context HttpServletRequest request) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("name", "Hello, World!");
            return successResponse(response);
        } catch(Exception e) {
            return serverErrorResponse(e.getMessage(), e);
        }
    }
}
