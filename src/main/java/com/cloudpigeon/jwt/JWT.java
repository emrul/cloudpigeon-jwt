package com.cloudpigeon.jwt;

import com.cloudpigeon.jwt.domain.IJsonWebToken;
import com.cloudpigeon.jwt.domain.impl.JwtKeyProvider;
import com.cloudpigeon.jwt.domain.impl.JwtPayload;
import com.cloudpigeon.jwt.domain.impl.JwtHeader;
import com.cloudpigeon.jwt.domain.impl.JsonWebToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.cloudpigeon.commons.Base64;
import com.cloudpigeon.commons.Base64.*;

import javax.crypto.Mac;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class JWT {
    private static final ObjectMapper mapper =  new ObjectMapper().registerModule(new AfterburnerModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final int JWT_DELIMITER      = '.';

    public static final Encoder b64encoder = Base64.getUrlEncoder();
    public static final Decoder b64decoder = Base64.getUrlDecoder();

    private static JWT instance;
    protected final ConcurrentHashMap<String, Class> payloadClasses = new ConcurrentHashMap<>();

    private static final ThreadLocal<Mac> threadLocalMac =
      new ThreadLocal<Mac>(){
      @Override
      protected Mac initialValue() {
        try {
          return Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException nsa) {
          throw new IllegalArgumentException("Can't find algorithm.");
        }
      }
    };

    public static JWT getInstance() {
        if ( instance == null ) {
            instance = new JWT();
            instance.payloadClasses.put("JWT", JwtPayload.class);
        }
        return instance;
    }

    public void registerPayloadClass(String name, Class clazz ) {
        payloadClasses.put(name, clazz);
    }

    public IJsonWebToken parseToken(String tokenStr, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        IJsonWebToken token = null;
        byte[] tokenBytes = tokenStr.getBytes();

        /// <header>.<payload>.<signature>
        int firstDot = -1, secondDot = -1;
        int x;
        for ( x = 0; x < tokenBytes.length; x++) {
            if ( tokenBytes[x] == JWT_DELIMITER ) {
                firstDot = x; // stores reference to first '.' character in JWT token
                break;
            }
        }
        if ( firstDot == -1) return null;

        for ( x = firstDot+1; x < tokenBytes.length; x++) {
            if ( tokenBytes[x] == JWT_DELIMITER ) {
                secondDot = x; // stores reference to second '.' character in JWT token
                break;
            }
        }
        if ( secondDot == -1) return null;

        byte[] decodedHeader = b64decoder.decode( tokenBytes, 0, firstDot);
        JwtHeader header = mapper.readValue(decodedHeader, JwtHeader.class);

        Mac mac = JWT.threadLocalMac.get();

        try {
            mac.init(JwtKeyProvider.getInstance().getKey(header));
            mac.update(tokenBytes, 0, secondDot);
            byte[] calculatedSignature = mac.doFinal();

            byte[] decodedSignature = b64decoder.decode(tokenBytes, secondDot + 1, tokenBytes.length);

            boolean signatureValid = Arrays.equals(calculatedSignature, decodedSignature);

            if (signatureValid) {
                byte[] decodedPayload = b64decoder.decode(tokenBytes, firstDot + 1, secondDot);
                Class payloadClass = payloadClasses.get(header.getType());
                if ( payloadClass == null ) {
                    throw new Exception("Payload class not registered:" + header.getType());
                }
                JwtPayload payload = mapper.readValue(decodedPayload, JwtPayload.class);
                token = new JsonWebToken(header, payload);
                return token;
            }

        }
        catch (Exception e) {
            // noop - maybe log a quiet error.
        }
        finally {
            mac.reset();
        }
        return token;
    }


}
