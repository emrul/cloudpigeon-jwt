package com.cloudpigeon.jwt.domain.impl;

import com.cloudpigeon.jwt.domain.IJwtPayload;

import java.util.Date;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class JwtPayload implements IJwtPayload {
    /*extends HashMap<String,Object> {
    public static final String PROPERTY_ISSUER = "iss";
    public static final String PROPERTY_ISSUED_AT = "iat";
    public static final String PROPERTY_EXPIRY = "exp";
    public static final String PROPERTY_NOT_BEFORE = "nbf";*/

    public String iss;
    public String sub;
    public String aud;
    public String jti;
    public Date exp;
    public Date iat;
    public Date nbf;

    @Override
    public String getIssuer() {
        //
        //Maps.fromMap()
        // JsonFactory. .fromJson("", JWTClaim.class);
        return iss;
    }

    @Override
    public void setIssuer(String iss) {
        this.iss = iss;
    }

    @Override
    public Date getExpiry() {
        return exp;
    }

    @Override
    public void setExpiry(Date exp) {
        this.exp = exp;
    }

    @Override
    public Date getIssuedAt() {
        return iat;
    }

    @Override
    public void setIssuedAt(Date iat) {
        this.iat = iat;
    }

    @Override
    public Date getNotBefore() {
        return nbf;
    }

    @Override
    public void setNotBefore(Date nbf) {
        this.nbf = nbf;
    }

    @Override
    public String getSubject() {
        return sub;
    }

    @Override
    public void setSubject(String sub) {
        this.sub = sub;
    }

    @Override
    public String getAudience() {
        return aud;
    }

    @Override
    public void setAudience(String aud) {
        this.aud = aud;
    }

    @Override
    public String getTokenId() {
        return jti;
    }

    @Override
    public void setTokenId(String jti) {
        this.jti = jti;
    }
}
