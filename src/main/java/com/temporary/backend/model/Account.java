package com.temporary.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account extends BaseModel{
    private int accountId;
    private String email;
    private String username;
    private String externalCredential;
    private boolean accountEnabled;
    private AccountType accountType;

    // Transient fields used for new account creation
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String  password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordSalt;

    public Account() {}

    public Account (ResultSet rs) throws SQLException {
        super(rs);
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType type) {
        accountType = type;
    }

    public boolean isAccountEnabled() {
        return accountEnabled;
    }

    public void setAccountEnabled(boolean accountEnabled) {
        this.accountEnabled = accountEnabled;
    }

    public boolean isAdmin() {
        return accountType != null && accountType == AccountType.ADMIN;
    }

    public String getExternalCredential() { return externalCredential; }
}
