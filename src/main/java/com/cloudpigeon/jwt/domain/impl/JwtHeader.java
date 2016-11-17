package com.cloudpigeon.jwt.domain.impl;

import com.cloudpigeon.jwt.domain.IJwtHeader;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class JwtHeader implements IJwtHeader { /*extends HashMap<String,Object> {
    public static final String PROPERTY_TYPE = "typ";
    public static final String PROPERTY_ALGORITHM = "alg";
*/

    private String typ;
    private String alg;
    private String kid;
    private String cty;

    @Override
    public String getAlgorithm() {
        return alg;
    }

    @Override
    public void setAlgorithm(String alg) {
        this.alg = alg;
    }

    @Override
    public String getType() {
        return typ;
    }

    @Override
    public void setType(String typ) {
        this.typ = typ;
    }

    @Override
    public String getKeyId() {
        return kid;
    }

    @Override
    public void setKeyId(String kid) {
        this.kid = kid;
    }

    @Override
    public String getContentType() {
        return this.cty;
    }

    @Override
    public void setContentType(String cty) {
        this.cty = cty;
    }
}
