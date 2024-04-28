package com.temporary.backend.rest;

import com.temporary.backend.dao.AssemblyDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.model.Account;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("assembly-user")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class AssemblyUserRestService extends BaseRestService {
    @Path("follow/{assemblyId}")
    @GET
    public Response followAssembly(@Context HttpServletRequest request, @PathParam("assemblyId") Integer assemblyId) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            Account account = this.getCurrentAccount(request);
            if (account == null || account.getAccountId() == 0)
                throw new ApplicationException("User must be logged in to follow an assembly");
            return successResponse(dao.followAssemblyUser(account.getAccountId(), assemblyId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("join/{assemblyId}")
    @GET
    public Response joinAssembly(@Context HttpServletRequest request, @PathParam("assemblyId") Integer assemblyId) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            Account account = this.getCurrentAccount(request);
            if (account == null || account.getAccountId() == 0)
                throw new ApplicationException("User must be logged in to join an assembly");
            return successResponse(dao.joinAssemblyUser(account.getAccountId(), assemblyId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("ignore/{assemblyId}")
    @GET
    public Response ignoreAssembly(@Context HttpServletRequest request, @PathParam("assemblyId") Integer assemblyId) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            Account account = this.getCurrentAccount(request);
            if (account == null || account.getAccountId() == 0)
                throw new ApplicationException("User must be logged in to ignore an assembly");
            return successResponse(dao.ignoreAssemblyUser(account.getAccountId(), assemblyId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("reset/{assemblyId}")
    @GET
    public Response resetAssembly(@Context HttpServletRequest request, @PathParam("assemblyId") Integer assemblyId) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            Account account = this.getCurrentAccount(request);
            if (account == null || account.getAccountId() == 0)
                throw new ApplicationException("User must be logged in to reset an assembly relationship");
            return successResponse(dao.removeAssemblyUser(account.getAccountId(), assemblyId));
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
