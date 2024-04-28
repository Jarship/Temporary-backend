package com.temporary.backend.rest;

import com.temporary.backend.dao.AssemblyDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.model.Account;
import com.temporary.backend.model.AccountType;
import com.temporary.backend.model.Assembly;
import com.temporary.backend.rest.auth.AccountTypesAllowed;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
@Path("assembly")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class AssemblyRestService extends BaseRestService {

    @Path("")
    @POST
    @AccountTypesAllowed({AccountType.ADMIN})
    public Response createAssembly(@Context HttpServletRequest request, Assembly assembly) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            int assemblyId = dao.createAssembly(assembly);
            return successOrNotFoundResponse(dao.getAssembly(assemblyId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("")
    @PUT
    @AccountTypesAllowed({AccountType.ADMIN})
    public Response updateAssembly(Assembly assembly) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            dao.updateAssembly(assembly);
            return successOrNotFoundResponse(dao.getAssembly(assembly.getAssemblyId()));
        } catch(Exception e){
            return handleException(e);
        }
    }

    @Path("")
    @GET
    public Response getUserAssemblies(@Context HttpServletRequest request) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            Account account = this.getCurrentAccount(request);
            return successResponse(dao.getUserAssemblies(account.getAccountId()));
        } catch (Exception e) {
            return handleException(e);
        }
    }
    @Path("{assemblyId}")
    @GET
    @PermitAll
    public Response getAssembly(@PathParam("assemblyId") int assemblyId) {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            return successOrNotFoundResponse(dao.getAssembly(assemblyId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("public")
    @GET
    @PermitAll
    public Response getPublicAssemblies() {
        try {
            AssemblyDAO dao = new AssemblyDAO();
            return successOrNotFoundResponse(dao.getPublicAssemblies());
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
