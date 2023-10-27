package com.temporary.backend.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.codec.binary.Base64;
import com.temporary.backend.model.Password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class SecurityUtils {
    private static final String JWT_SECRET = "iBy9jsfEoppQi5nbLvFkUhjaaTTt36L_dev";
    public static final String JWT_ISSUER = "http://localhost";
    public static final String ACCOUNT_NAME_CLAIM = "accountName";
    public static final String ACCOUNT_TYPE_CLAIM = "accountType";

    public static Password hashPassword(String password) throws Exception {
        if (password == null) {
            throw new IllegalArgumentException("Password required");
        }
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] saltBytes = new byte[32];
        random.nextBytes(saltBytes);
        String salt = Base64.encodeBase64String(saltBytes);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(salt.getBytes());
        md.update(password.getBytes());
        byte[] hashedBytes = md.digest();
        StringBuilder hashedString = new StringBuilder();
        int i = 0;
        while (i < hashedBytes.length) {
            hashedString.append(Integer.toString((hashedBytes[i++] & 0xff) + 0x100, 16).substring(1));
        }
        System.out.println(hashedString.length() + " " + salt.length());
        return new Password(hashedString.toString(), salt);
    }

    public static String hashString(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] hashedBytes = md.digest();
        StringBuilder hashedString = new StringBuilder();
        int i = 0;
        while (i < hashedBytes.length) {
            hashedString.append(Integer.toString((hashedBytes[i++] & 0xff) + 0x100, 16).substring(1));
        }
        return hashedString.toString();
    }

    public static boolean validatePassword(String password, String hash, String salt) throws NoSuchAlgorithmException {
        if (password == null) {
            throw new IllegalArgumentException("Password required");
        }
        if (hash == null) {
            throw new IllegalArgumentException("Hash required");
        }
        if (salt == null) {
            throw new IllegalArgumentException("Salt required");
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(salt.getBytes());
        md.update(password.getBytes());
        byte[] hashedBytes = md.digest();
        StringBuilder hashedString = new StringBuilder();
        int i = 0;
        while (i < hashedBytes.length) {
            hashedString.append(Integer.toString((hashedBytes[i++] & 0xff) + 0x100, 16).substring(1));
        }
        return Objects.equals(hash, hashedString.toString());
    }

    public static String generateJwt(int accountId, String accountName) throws JOSEException {
        JWSSigner signer = new MACSigner(JWT_SECRET.getBytes());

        JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
        claimsSet.subject(String.valueOf(accountId));
        claimsSet.issueTime(new Date());
        claimsSet.claim(ACCOUNT_NAME_CLAIM, accountName);
        claimsSet.issuer(JWT_ISSUER);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet.build());
        // Sign, apply the HMAC protection
        signedJWT.sign(signer);

        // Serialize to compact form, produces something like:
        // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
        return signedJWT.serialize();
    }

    public static String generatePasswordResetToken() {
        return UUID.randomUUID().toString();
    }

    public static SignedJWT parse(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (!signedJWT.verify(new MACVerifier(JWT_SECRET))) {
            throw new JOSEException("Invalid token - secret does not match");
        }
        if (signedJWT.getJWTClaimsSet().getExpirationTime() != null && signedJWT.getJWTClaimsSet().getExpirationTime().compareTo(new Date()) < 0) {
            throw new JOSEException("Token expired");
        }
        return signedJWT;
    }
}
