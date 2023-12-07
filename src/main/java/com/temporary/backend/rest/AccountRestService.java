package com.temporary.backend.rest;

import com.temporary.backend.dao.AccountDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.manager.AccountManager;
import com.temporary.backend.model.Account;
import com.temporary.backend.model.AccountType;
import com.temporary.backend.rest.auth.AccountTypesAllowed;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("account")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class AccountRestService extends BaseRestService {

    @Path("create")
    @POST
    @PermitAll
    public Response createAccount(@Context HttpServletRequest request, Account account) {
        try {
            account.setAccountType(AccountType.USER);
            AccountManager manager = new AccountManager();
            account = manager.createAccount(account);
            return successResponse(account);
        } catch (Exception e) {
            // Send Debug Email
            return handleException(e);
        }
    }

    @Path("confirm/{accountId}/{confirmationCode}")
    @PUT
    @PermitAll
    public Response confirmAccount(@Context HttpServletRequest request, @PathParam("accountId") int accountId, @PathParam("confirmationCode") String confirmationCode) {
        try {
            AccountManager manager = new AccountManager();
            return successResponse(manager.confirmAccount(accountId, confirmationCode));
        } catch (ApplicationException e) {
            return handleException(e);
        }
    }

    @Path("login")
    @POST
    @PermitAll
    public Response login(@Context HttpServletRequest request, LoginInput loginInput) {
        try {
            AccountManager manager = new AccountManager();
            return successResponse(manager.login(loginInput));
        } catch (ApplicationException e) {
            return handleException(e);
        }
    }

    @Path("")
    @GET
    public Response getAccount(@Context HttpServletRequest request) {
        Account account = this.getCurrentAccount(request);
        return successResponse(account);
    }

    @Path("passwordResetRequest/{email}")
    @POST
    @PermitAll
    public Response passwordResetRequest(@Context HttpServletRequest request, @PathParam("email") String email, @QueryParam("accountType") AccountType accountType) {
        try {
            AccountManager manager = new AccountManager();
            manager.passwordResetRequest(email, accountType != null ? accountType : AccountType.USER);
            return successResponse();
        } catch (ApplicationException e) {
            return handleException(e);
        }
    }

    @Path("passwordReset/{email}/{confirmationCode}/{newPassword}")
    @PUT
    @PermitAll
    public Response passwordReset(@Context HttpServletRequest request, @PathParam("email") String email,
                                  @PathParam("confirmationCode") String confirmationCode, @PathParam("newPassword") String newPassword) {
        try {
            AccountManager manager = new AccountManager();
            return successResponse(manager.passwordReset(email, confirmationCode, newPassword));
        } catch (ApplicationException e) {
            return handleException(e);
        }
    }

    @DELETE
    @Path("")
    @AccountTypesAllowed({AccountType.USER})
    public Response deleteAccount(@Context HttpServletRequest request) {
        try {
            Account account = this.getCurrentAccount(request);
            AccountManager manager = new AccountManager();
            manager.deleteAccount(account.getAccountId());
            return successResponse();
        } catch (ApplicationException e) {
            return handleException(e);
        }
    }
}
