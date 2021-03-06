package com.cloudpigeon.jwt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public interface IJwtHeader {

    @JsonProperty(value="alg")
    public String getAlgorithm();

    @JsonProperty(value="alg")
    public void setAlgorithm(String alg);

    @JsonProperty(value="cty")
    public String getContentType();

    @JsonProperty(value="cty")
    public void setContentType(String cty);


    @JsonProperty(value="typ")
    public String getType();

    @JsonProperty(value="typ")
    public void setType(String typ);

    @JsonProperty(value="kid")
    public String getKeyId();

    @JsonProperty(value="kid")
    public void setKeyId(String kid);

}
