package com.cloudpigeon.jwt.domain.impl;

import com.cloudpigeon.jwt.domain.IJwtHeader;
import com.cloudpigeon.jwt.domain.IJwtPayload;
import com.cloudpigeon.jwt.domain.IJsonWebToken;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class JsonWebToken implements IJsonWebToken {

    public final IJwtHeader header;
    public final IJwtPayload payload;

    public JsonWebToken() {
        header = new JwtHeader();
        payload = new JwtPayload();
    }

    public JsonWebToken(IJwtHeader header, IJwtPayload payload) {
        this.header = header;
        this.payload = payload;
    }

    @Override
    public IJwtHeader getHeader() {
        return header;
    }

    @Override
    public IJwtPayload getPayload() {
        return payload;
    }
}
