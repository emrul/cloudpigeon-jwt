package com.cloudpigeon.jwt.domain.impl;

import com.cloudpigeon.jwt.domain.IJwtPayload;

/**
 * Created by emrul on 28/09/2014.
 *
 * If extending this class then check http://www.iana.org/assignments/jwt/jwt.xhtml to see
 * if additional claims are already registered with IANA.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class JwtPayload implements IJwtPayload {
    public String iss;
    public String sub;
    public String aud;
    public String jti;
    public Long exp;
    public Long iat;
    public Long nbf;

    @Override
    public String getIssuer() {
        return iss;
    }

    @Override
    public void setIssuer(String iss) {
        this.iss = iss;
    }

    @Override
    public Long getExpiry() {
        return exp;
    }

    @Override
    public void setExpiry(Long exp) {
        this.exp = exp;
    }

    @Override
    public Long getIssuedAt() {
        return iat;
    }

    @Override
    public void setIssuedAt(Long iat) {
        this.iat = iat;
    }

    @Override
    public Long getNotBefore() {
        return nbf;
    }

    @Override
    public void setNotBefore(Long nbf) {
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
