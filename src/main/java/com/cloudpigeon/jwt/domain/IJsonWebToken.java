package com.cloudpigeon.jwt.domain;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public interface IJsonWebToken {

    public IJwtHeader getHeader();

    public IJwtPayload getPayload();
}
