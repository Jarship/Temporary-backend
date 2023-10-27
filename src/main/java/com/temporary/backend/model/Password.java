package com.temporary.backend.model;

public class Password {
    private final String hash;
    private final String salt;

    public Password(String hash, String salt) {
        this.hash = hash;
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }
}
