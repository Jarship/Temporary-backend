package com.temporary.backend.manager;

import com.nimbusds.jose.JOSEException;
import com.temporary.backend.dao.AccountDAO;
import com.temporary.backend.dao.BaseDAO;
import com.temporary.backend.exception.ApplicationException;
import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.*;
import com.temporary.backend.rest.LoginInput;
import com.temporary.backend.util.SecurityUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;

public class AccountManager extends BaseManager {
    private AccountDAO dao;

    public AccountManager() {
        this.dao = new AccountDAO();
    }

    public AccountManager(BaseDAO dao) {
        this.dao = new AccountDAO();
    }

    public AccountManager(BaseManager manager) {
        this.dao = new AccountDAO(manager.getBaseDAO());
    }

    @Override
    protected BaseDAO getBaseDAO() {
        return dao;
    }

    public Account authenticate(LoginInput loginInput) throws ApplicationException {
        if (loginInput.getExternalCredential() != null) {
            return dao.getAccountByCredential(loginInput.getExternalCredential());
        }

        Account account = dao.getAccount(loginInput.getEmail(), loginInput.getAccountType(), true);
        if (account != null && account.getPassword() != null && loginInput.getPassword() != null) {
            try {
                boolean valid = SecurityUtils.validatePassword(loginInput.getPassword(), account.getPassword(), account.getPasswordSalt());
                if (valid) {
                    return account;
                }
            } catch (NoSuchAlgorithmException e) {
                throw new ApplicationException(e);
            }
        }
        return null;
    }

    public Map<String, Object> getTokenResponse(Account account) throws JOSEException {
        String token = SecurityUtils.generateJwt(account.getAccountId(), account.getEmail(), account.getAccountType());
        Map<String, Object> result = new HashMap<>();
        result.put("account", account);
        result.put("token", token);
        return result;
    }

    public Map<String, Object> confirmAccount(int accountId, String confirmationCode) throws ApplicationException {
        if (dao.confirmAccount(accountId, confirmationCode)) {
            try {
                Account account = dao.getAccount(accountId, true);
                return getTokenResponse(account);
            } catch (Exception e) {
                throw new ApplicationException(e);
            }
        } else
            throw new ApplicationException.UnauthorizedException();
    }

    public Map<String, Object> login(LoginInput loginInput) throws ApplicationException {
        String token;
        try {
            Account account = authenticate(loginInput);
            if (account != null) {
                return getTokenResponse(account);
            } else {
                throw new ApplicationException.UnauthorizedException();
            }
        } catch (ApplicationException.UnauthorizedException ue) {
            throw ue;
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    public void passwordResetRequest(String email, AccountType type) throws ApplicationException {
        String confirmationCode = "" + (new Random().nextInt(9000) + 1000);

        try {
            dao.beginTransaction();

            if (!dao.updateConfirmationCode(email, confirmationCode))
                throw new ApplicationException.NotFoundException("Invalid confirmation code in password reset request.");
        } catch (ApplicationException e) {
            dao.rollbackTransaction();
            throw e;
        } finally {
            dao.endTransaction();
        }

        // ToDo: Send code via email
    }

    public Map<String, Object> passwordReset(String email, String confirmationCode, String newPassword) throws ApplicationException {
        try {
            dao.beginTransaction();
            AccountType type = dao.getAccountType(email, confirmationCode);
            Password password = SecurityUtils.hashPassword(newPassword);
            if (!dao.updatePassword(email, confirmationCode, password))
                throw new ApplicationException.UnauthorizedException();
            return getTokenResponse(dao.getAccount(email, type, true));
        } catch(Exception e) {
            dao.rollbackTransaction();
            throw (e instanceof ApplicationException) ? (ApplicationException) e : new ApplicationException(e);
        } finally {
            dao.endTransaction();;
        }
    }

    public Account createAccount(Account account) throws ApplicationException {
        if (account.getEmail() == null || account.getEmail().length() < 3)
            throw new ApplicationException.ValidationException("Missing email address");
        if (account.getUsername() == null)
            throw new ApplicationException.ValidationException("Missing username");
        Account exists = dao.getAccount(account.getEmail(), account.getAccountType(), false);
        if (exists != null)
                throw new ApplicationException.ValidationException("Duplicate email address");

        try {
            beginTransaction();

            Password password = SecurityUtils.hashPassword(account.getPassword() != null ? account.getPassword() : account.getExternalCredential());
            account.setAccountId(dao.createAccount(account, password));
            return account;
        } catch(Exception e) {
            rollbackTransaction();
            throw (e instanceof ApplicationException) ? (ApplicationException) e: new ApplicationException(e);
        } finally {
            endTransaction();
        }

        // ToDo: Send confrimation email
    }

    public void deleteAccount(int accountId) throws ApplicationException {
        try {
            dao.beginTransaction();
            int result = dao.deleteAccount(accountId);
            if (result == 0) throw new ApplicationException.NotFoundException();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw (e instanceof ApplicationException) ? (ApplicationException) e : new ApplicationException(e);
        } finally {
            dao.endTransaction();
        }
    }
}
