package com.temporary.backend.rest.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import com.temporary.backend.exception.ErrorCode;
import com.temporary.backend.model.Account;
import com.temporary.backend.rest.BaseRestService;
import com.temporary.backend.rest.config.RestError;
import com.temporary.backend.util.SecurityUtils;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoleBasedContainerRequestFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest servletRequest;

    private static final Logger LOG = Logger.getLogger(RoleBasedContainerRequestFilter.class.getSimpleName());

    @SuppressWarnings("RedundantThrows")
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        PostMatchContainerRequestContext postMatchRequestContext = (PostMatchContainerRequestContext) requestContext;
        Method requestMethod = postMatchRequestContext.getResourceMethod().getMethod();
        // Clear out user on the request
        requestContext.setProperty(BaseRestService.USER_ATTRIBUTE, null);
        // No access for any user - abort
        if (denyAllUsers(requestMethod)) {
            abortRequestUnauthorized(requestContext);
            return;
        }

        // TODO: Authentication work
//        boolean permitAll = permitAllUsers(requestMethod);
//        boolean abortRequestOnAuthFailure = !permitAll;
//        Account account = authenticate(requestContext, abortRequestOnAuthFailure);
//        if (account == null && abortRequestOnAuthFailure) {
//            return;
//        }
//
//        AccountTypesAllowed accountTypesAllowedAnnotation = getAccountTypesAllowed(requestMethod);
//    if (account != null && accountTypesAllowedAnnotation != null) {
//        HashSet<AccountType> userTypesAllowed = new HashSet<>(Arrays.asList(accountTypesAllowedAnnotation.value()));
//        if (!userTypesAllowed.contains(account.getAccountType())) {
//            abortRequestUnauthorized(requestContext, "Authorization failed - user role does not have access");
//        }
//    }
    }

    private Account authenticate(ContainerRequestContext requestContext, boolean abortRequestOnFailure) {
        String token = servletRequest.getHeader(BaseRestService.TOKEN_HEADER);

        if (token != null) {
            try {
                SignedJWT jwt = SecurityUtils.parse(token);
                int accountId = Integer.parseInt(jwt.getJWTClaimsSet().getSubject());

                // TODO: Implement Accounts
//                Account account = new AccountManager().getAccount(accountId);
//                if (account!= null) {
//                    setRequestContextFromUser(account, token, requestContext);
//                    return account;
//                }
                return null;
            } catch(JOSEException e) {
                if (abortRequestOnFailure) {
                    abortRequestUnauthorized(requestContext, e.getLocalizedMessage());
                }
                return null;
            } catch(Exception e) {
                LOG.log(Level.SEVERE, "An exception occurred when setting user", e);
                if (abortRequestOnFailure) {
                    abortRequestUnauthorized(requestContext, "An error occured when attempting to authorize the user.");
                }
                return null;
            }
        }
        return null;
    }

    private void setRequestContextFromUser(Account account, String token, ContainerRequestContext requestContext) {
        requestContext.setProperty(BaseRestService.TOKEN_ATTRIBUTE, token);
        requestContext.setProperty(BaseRestService.USER_ATTRIBUTE, account);
    }

//    private AccountTypesAllowed getAccountTypesAllowed(Method requestMethod) {
//        if (requestMethod.getDeclaringClass().isAnnotationPresent(AccountTypesAllowed.class)) {
//            return requestMethod.getDeclaringClass().getAnnotation(AccountTypesAllowed.class);
//        }
//        return requestMethod.getAnnotation(AccountTypesAllowed.class);
//    }

    private boolean permitAllUsers(Method requestMethod) {
        return requestMethod.getDeclaringClass().isAnnotationPresent(PermitAll.class) || requestMethod.isAnnotationPresent(PermitAll.class);
    }

    private boolean denyAllUsers(Method requestMethod) {
        return requestMethod.getDeclaringClass().isAnnotationPresent(DenyAll.class) || requestMethod.isAnnotationPresent(DenyAll.class);
    }

    private void abortRequestUnauthorized(ContainerRequestContext requestContext) {
        abortRequestUnauthorized(requestContext, "The user is unauthorized to perform this action.");
    }

    private void abortRequestUnauthorized(ContainerRequestContext requestContext, String errorMessage) {
        requestContext.abortWith((Response.status(Response.Status.UNAUTHORIZED)).entity(new RestError(ErrorCode.NOT_AUTHORIZED, "Unauthorized Access", errorMessage)).build());
    }
}
