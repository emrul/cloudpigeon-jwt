package com.cloudpigeon.jwt.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public interface IJwtPayload {

    @JsonProperty(value="iss")
    public String getIssuer();

    @JsonProperty(value="iss")
    public void setIssuer(String iss);

    @JsonProperty(value="exp")
    public Date getExpiry();

    @JsonProperty(value="exp")
    public void setExpiry(Date exp);

    @JsonProperty(value="iat")
    public Date getIssuedAt();

    @JsonProperty(value="iat")
    public void setIssuedAt(Date iat);

    @JsonProperty(value="nbf")
    public Date getNotBefore();

    @JsonProperty(value="nbf")
    public void setNotBefore(Date nbf);

    @JsonProperty(value="sub")
    public String getSubject();

    @JsonProperty(value="sub")
    public void setSubject(String sub);

    @JsonProperty(value="aud")
    public String getAudience();

    @JsonProperty(value="aud")
    public void setAudience(String aud);

    @JsonProperty(value="jti")
    public String getTokenId();

    @JsonProperty(value="jti")
    public void setTokenId(String jti);
}
