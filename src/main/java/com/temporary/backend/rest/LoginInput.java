package com.temporary.backend.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.temporary.backend.model.AccountType;
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true, allowSetters = true)
public class LoginInput {

    private String email;
    private String password;
    private String externalCredential;
    private AccountType accountType;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getExternalCredential() {
        return externalCredential;
    }

    public AccountType getAccountType() {
        return accountType != null ? accountType : AccountType.USER;
    }
}
