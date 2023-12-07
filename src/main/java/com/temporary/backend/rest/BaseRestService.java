package com.temporary.backend.rest;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.exception.ErrorCode;
import com.temporary.backend.model.Account;
import com.temporary.backend.rest.config.RestError;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public abstract class BaseRestService {
    private final static Logger logger = Logger.getLogger(BaseRestService.class.getSimpleName());

    private static final String SERVER_ERROR_TITLE = "Server Error";
    private static final String BAD_REQUEST_ERROR_TITLE = "Bad Request";
    private static final String CONFLICT_ERROR_TITLE = "Conflict";
    private static final String NOT_FOUND_ERROR_TITLE = "Not Found";
    private static final String UNAUTHORIZED_TITLE = "Unauthorized Access";

    public static final String USER_ATTRIBUTE = "user";
    public static final String TOKEN_ATTRIBUTE = "token";
    public static final String APPLICATION_ATTRIBUTE = "application";
    public static final String USER_SESSION = "userSession";
    public static final String TOKEN_HEADER = "token";

    public Account getCurrentAccount(@Context HttpServletRequest request) {
        return (Account) request.getAttribute(USER_ATTRIBUTE);
    }

    public String getToken(@Context HttpServletRequest request) {
        return (String) request.getAttribute(TOKEN_ATTRIBUTE);
    }

    public static Response successResponse() {
        return Response.ok().type(MediaType.APPLICATION_JSON).build();
    }

    public static Response successResponse(Object entity) {
        if (entity instanceof String) {
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("message", entity);
            return Response.ok(data).type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.ok(entity).type(MediaType.APPLICATION_JSON).build();
        }
    }

    public static Response successOrNotFoundResponse(Object entity) {
        if (entity != null) {
            return Response.ok(entity).type(MediaType.APPLICATION_JSON).build();
        }
        return notFoundResponse();
    }

    public static Response successOrNotFoundResponse(boolean found) {
        if (found) {
            return Response.ok().type(MediaType.APPLICATION_JSON).build();
        }
        return notFoundResponse();
    }

    public static Response successNoResponse() {
        return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response serverErrorResponse(String errorMessage) {
        return Response.serverError().entity(new RestError(ErrorCode.UNKNOWN, SERVER_ERROR_TITLE, errorMessage)).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response serverErrorResponse(String errorMessage, Exception e) {
        logger.log(Level.SEVERE, errorMessage, e);
        return Response.serverError().entity(new RestError(ErrorCode.UNKNOWN, errorMessage, e.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response badRequestResponse(String errorMessage) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new RestError(ErrorCode.BAD_INPUT, BAD_REQUEST_ERROR_TITLE, errorMessage)).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response conflictRequestResponse(String errorMessage) {
        return Response.status(Response.Status.CONFLICT).entity(new RestError(ErrorCode.CONFLICT, CONFLICT_ERROR_TITLE, errorMessage)).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response badRequestResponse(String errorMessage, Exception e) {
        logger.log(Level.SEVERE, errorMessage, e);
        return badRequestResponse(errorMessage);
    }

    public static Response notFoundResponse() {
        return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response notFoundResponse(String errorMessage) {
        return Response.status(Response.Status.NOT_FOUND).entity(new RestError(ErrorCode.NOT_FOUND, NOT_FOUND_ERROR_TITLE, errorMessage)).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response unauthorizedResponse() {
        return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response unauthorizedResponse(String errorMessage) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(new RestError(ErrorCode.NOT_AUTHORIZED, UNAUTHORIZED_TITLE, errorMessage)).type(MediaType.APPLICATION_JSON).build();
    }

    public static Response handleException(Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        if (e instanceof ApplicationException) {
            return Response.status(((ApplicationException) e).getHttpStatusCode()).entity(
                    new RestError(((ApplicationException) e).getErrorCode(), ((ApplicationException) e).getErrorCode().name(), e.getMessage())
            ).type(MediaType.APPLICATION_JSON).build();
        } else {
            return serverErrorResponse(e.getMessage(), e);
        }
    }

    protected File writeFilePartToTempFile(String filename, List<InputPart> inputParts, String tempDirPrefix) throws IOException {
        File tempFile = null;
        for (InputPart inputPart: inputParts) {
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            tempFile = writeFile(bytes, filename, tempDirPrefix);
        }
        return tempFile;
    }

    protected File writeFile(byte[] content, String filename, String tempDirPrefix) throws IOException {
        java.nio.file.Path tempDir = Files.createTempDirectory(tempDirPrefix);

        File file = new File(tempDir.toFile(), filename);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } else {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }

        FileOutputStream outputStream = new FileOutputStream(file);

        outputStream.write(content);
        outputStream.flush();
        outputStream.close();
        return file;
    }
}
