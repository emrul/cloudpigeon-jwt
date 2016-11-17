package com.cloudpigeon.jwt.domain.impl;

import com.cloudpigeon.jwt.domain.IJwtHeader;
import com.cloudpigeon.jwt.domain.IJwtKeyProvider;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class JwtKeyProvider implements IJwtKeyProvider {
    private static JwtKeyProvider provider;

    private SecretKeySpec secret_key;

    public static synchronized JwtKeyProvider getInstance() {
        if (provider == null) {
            provider = new JwtKeyProvider();
        }
        return provider;
    }

    public JwtKeyProvider() {
        byte[] secret = "tokenkey".getBytes();
        secret_key = new SecretKeySpec(secret, "HmacSHA256");
    }

    @Override
    public Key getVerificationKey(IJwtHeader header) {
        return secret_key;
    }
    @Override
    public Key getSigningKey(IJwtHeader header) {
        return secret_key;
    }
}
