package com.temporary.backend.dao;

import com.temporary.backend.exception.DatabaseException;
import com.temporary.backend.model.Account;
import com.temporary.backend.model.AccountType;
import com.temporary.backend.model.Password;

import java.sql.ResultSet;
import java.util.*;

public class AccountDAO  extends BaseDAO {
    public AccountDAO() {}

    public AccountDAO(BaseDAO dao) {
        super(dao);
    }

    public int createAccount(Account account, Password password) throws DatabaseException {
        String sql = "INSERT INTO account(email, phone, password, password_salt, account_enabled, account_type, username)  " +
                "values(?,?,?,?,?,?,?)";
        return this.insertWithIntegerAutokey(sql, account.getEmail(), account.getPhone(), password.getHash(), password.getSalt(), account.isAccountEnabled() ? 1 : 0,
                account.getAccountType().name(), account.getUsername());
    }

    public boolean confirmAccount(int accountId, String confirmationCode) throws DatabaseException {
        String sql = "UPDATE account SET account_enabled=1, confirmation_code=NULL WHERE account_id=? AND confirmation_code=? AND account_enabled=0";
        return this.executeUpdate(sql, accountId, confirmationCode) > 0;
    }

    public int deleteAccount(int accountId) throws DatabaseException {
        String[] sqls = new String[]{
//                "delete from account_relationships WHERE primary_id = ?",
//                "delete from account_relationships WHERE secondary_id = ?",
                "delete from account WHERE account_id = ?"
        };
        int result = 0;
        for (String sql: sqls) {
            result = this.executeUpdate(sql, accountId);
        }
        return result;
    }

    public Account getAccountByCredential(String externalCredential) throws DatabaseException {
        String sql = "SELECT * from account where external_credential=? AND account_enabled=1";
        return this.queryForObject(sql, Account.class, externalCredential);
    }
    public Account getAccount(int accountId, boolean enabledOnly) throws DatabaseException {
        String sql = "SELECT * from account where account_id=?" + (enabledOnly ? " AND account_enabled=1" : "");
        return this.queryForObject(sql, Account.class, accountId);
    }

    public Account getAccount(String email, AccountType accountType, boolean enabledOnly) throws DatabaseException {
        String sql = "SELECT * from account where email=? AND account_type=?" + (enabledOnly ? " AND account_enabled=1" : "");
        return this.queryForObject(sql, Account.class, email, accountType);
    }

    public Account getAccountByPhone(String phone, AccountType accountType, boolean enabledOnly) throws DatabaseException {
        String sql = "SELECT * from account where phone=? AND account_type=?" + (enabledOnly ? " AND account_enabled=1" : "");
        return this.queryForObject(sql, Account.class, phone, accountType);
    }

    public AccountType getAccountType(String email, String confirmationCode) throws DatabaseException {
        String sql = "SELECT account_type FROM account WHERE email=? AND confirmation_code=? AND confirmation_code IS NOT NULL";
        String result = queryForString(sql, email, confirmationCode);
        return result != null ? AccountType.valueOf(result) : null;
    }

    public boolean updateConfirmationCode(String email, String confirmationCode) throws DatabaseException {
        String sql = "UPDATE account SET confirmation_code=? WHERE email=?";
        return this.executeUpdate(sql, confirmationCode, email) > 0;
    }

    public boolean updatePassword(String email, String confirmationCode, Password password) throws DatabaseException {
        String sql = "UPDATE account SET password=?, password_salt=?, confirmation_code=NULL, account_enabled=1 WHERE email=? AND confirmation_code=? AND confirmation_code IS NOT NULL";
        return this.executeUpdate(sql, password.getHash(), password.getSalt(), email, confirmationCode) > 0;
    }

}
