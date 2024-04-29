package com.temporary.backend.rest;

import com.temporary.backend.dao.StructureDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.manager.StructureManager;
import com.temporary.backend.model.AccountType;
import com.temporary.backend.model.Structure;
import com.temporary.backend.rest.auth.AccountTypesAllowed;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("structure")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class StructureRestService extends BaseRestService {
    @Path("{structureId}")
    @GET
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response getStructure(@PathParam("structureId") Integer structureId) {
        try {
            StructureDAO dao = new StructureDAO();
            return successOrNotFoundResponse(dao.getStructure(structureId));
        } catch(Exception e) {
            return handleException(e);
        }
    }

    @GET
    @PermitAll
    public Response getStructures() {
        try {
            StructureDAO dao = new StructureDAO();
            return successOrNotFoundResponse(dao.getStructures());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    @AccountTypesAllowed({AccountType.ADMIN, AccountType.USER})
    public Response createStructure(Structure structure) {
        try {
            StructureManager manager = new StructureManager();
            return successResponse(manager.createStructure(structure));
        } catch (Exception e) {
            return handleException(e);
        }
    }
    @Path("{structureId}/front/{frontId}")
    @PUT
    public Response addToFront(@PathParam("structureId") Integer structureId, @PathParam("frontId") Integer frontId) {
        try {
            StructureDAO dao = new StructureDAO();
            if (dao.addStructureToFront(structureId, frontId))
                return successNoResponse();
            else
                throw new ApplicationException("Error adding Structure to front");
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Path("{structureId}/front/{frontId}")
    @DELETE
    public Response removeFromFront(@PathParam("frontId") Integer frontId, @PathParam("structureId") Integer structureId) {
        try {
            StructureDAO dao = new StructureDAO();
            if (dao.removeStructureFromFront(structureId, frontId))
                return successNoResponse();
            else
                throw new ApplicationException("Error removing structure from front");
        } catch (Exception e) {
            return handleException(e);
        }
    }

}
