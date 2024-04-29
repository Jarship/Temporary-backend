package com.temporary.backend.rest;

import com.temporary.backend.dao.FrontDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.manager.FrontManager;
import com.temporary.backend.model.AccountType;
import com.temporary.backend.model.Front;
import com.temporary.backend.rest.auth.AccountTypesAllowed;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("front")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class FrontRestService extends BaseRestService {
    @Path("{frontId}")
    @GET
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response getFront(@PathParam("frontId") Integer frontId) {
        try {
            FrontDAO dao = new FrontDAO();
            return successOrNotFoundResponse(dao.getFront(frontId));
        } catch(Exception e) {
            return handleException(e);
        }
    }

    @Path("active")
    @GET
    @PermitAll
    public Response getActiveFronts() {
        try {
            FrontDAO dao = new FrontDAO();
            return successOrNotFoundResponse(dao.getActiveFronts());
        } catch(Exception e) {
            return handleException(e);
        }
    }

    @POST
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response createFront(Front front) {
        try {
            FrontManager manager = new FrontManager();
            return successOrNotFoundResponse(manager.createFront(front));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("{frontId}")
    @DELETE
    @AccountTypesAllowed({AccountType.ADMIN})
    public Response endFront(@PathParam("frontId") Integer frontId) {
        try {
            FrontDAO dao = new FrontDAO();
            dao.endFront(frontId);
            return successNoResponse();
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
