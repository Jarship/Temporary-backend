package com.temporary.backend.rest;

import com.temporary.backend.dao.ScopeDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.manager.ScopeManager;
import com.temporary.backend.model.AccountType;
import com.temporary.backend.model.Scope;
import com.temporary.backend.rest.auth.AccountTypesAllowed;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("scope")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ScopeRestService extends BaseRestService {

    @Path("{scopeId}")
    @GET
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response getScope(@PathParam("scopeId") Integer scopeId) {
        try {
            ScopeManager manager = new ScopeManager();
            return successOrNotFoundResponse(manager.getScopeWithParents(scopeId));
        } catch(Exception e) {
            return handleException(e);
        }
    }

    @Path("children/{scopeId}")
    @GET
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response getChildScopes(@PathParam("scopeId") Integer scopeId) {
        try {
            ScopeDAO dao = new ScopeDAO();
            return successOrNotFoundResponse(dao.getScopesOfParent(scopeId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("root")
    @GET
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response getRootScopes() {
        try {
            ScopeDAO dao = new ScopeDAO();
            return successOrNotFoundResponse(dao.getScopesWithoutParent());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response createScope(Scope scope) {
        try {
            ScopeManager manager = new ScopeManager();
            return successResponse(manager.createScope(scope));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PUT
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response updateScope(Scope scope) {
        try {
            if (scope.getScopeId() <= 0) {
                throw new ApplicationException("A scope must have an id to be updated");
            }
            ScopeDAO dao = new ScopeDAO();
            dao.updateScope(scope);
            return successOrNotFoundResponse(dao.getScope(scope.getScopeId()));

        } catch (Exception e) {
            return handleException(e);
        }
    }
}
