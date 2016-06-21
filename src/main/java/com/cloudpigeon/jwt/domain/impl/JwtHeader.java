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

    @Override
    public String getAlggorithm() {
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

    /*
    public Object get(String property) {
        switch (property) {
            case JWTHeader.PROPERTY_TYPE:
                return typ;
            case JWTHeader.PROPERTY_ALGORITHM:
                return alg;
            default:
                return super.get(property);
        }
    }

    @Override
    public Object put(String property, Object value) {
        switch (property) {
            case JWTHeader.PROPERTY_TYPE:
                typ = value;
            case JWTHeader.PROPERTY_ALGORITHM:
                alg = value;
            default:
                super.put(property, value);
        }
        return null;
    }
    */
}
