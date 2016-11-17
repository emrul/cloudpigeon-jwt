package com.cloudpigeon.jwt;

import com.cloudpigeon.commons.Base64;
import com.cloudpigeon.commons.Base64.Decoder;
import com.cloudpigeon.commons.Base64.Encoder;
import com.cloudpigeon.jwt.domain.IJsonWebToken;
import com.cloudpigeon.jwt.domain.IJwtKeyProvider;
import com.cloudpigeon.jwt.domain.IJwtPayload;
import com.cloudpigeon.jwt.domain.impl.JsonWebToken;
import com.cloudpigeon.jwt.domain.impl.JwtHeader;
import com.cloudpigeon.jwt.domain.impl.JwtKeyProvider;
import com.cloudpigeon.jwt.domain.impl.JwtPayload;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import javax.crypto.Mac;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class JWT {
    private final ObjectMapper mapper;

    private static final int JWT_DELIMITER      = '.';

    private final Encoder b64encoder = Base64.getUrlEncoder();
    private final Decoder b64decoder = Base64.getUrlDecoder();

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

    private IJwtKeyProvider keyProvider;

    public IJwtKeyProvider getKeyProvider() {
        return keyProvider;
    }

    public JWT() {
        this(JwtKeyProvider.getInstance());
    }


    public JWT(IJwtKeyProvider keyProvider) {
        this(keyProvider, new ObjectMapper()
                            .registerModule(new AfterburnerModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                            .setSerializationInclusion(JsonInclude.Include.NON_NULL));
    }


    public JWT(IJwtKeyProvider keyProvider, ObjectMapper mapper) {
        this.keyProvider = keyProvider;
        this.mapper = mapper;
    }

    public void registerPayloadClass(String name, Class clazz ) {
        payloadClasses.put(name, clazz);
    }

    public String signAndGetToken(IJsonWebToken tokenToSign) throws JsonProcessingException, CloneNotSupportedException, InvalidKeyException, UnsupportedEncodingException {
        byte[] header = b64encoder.encode(mapper.writeValueAsBytes(tokenToSign.getHeader()));
        byte[] payload = b64encoder.encode(mapper.writeValueAsBytes(tokenToSign.getPayload()));

        Mac mac = (Mac)JWT.threadLocalMac.get().clone();
        mac.init(keyProvider.getSigningKey(tokenToSign.getHeader()));
        mac.update(header);
        mac.update((byte) JWT_DELIMITER);
        mac.update(payload);

        return new String(header) + "." + new String(payload) + "." + b64encoder.encodeToString(mac.doFinal());
    }

    public IJsonWebToken parseToken(String tokenStr) throws Exception {
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

        Mac mac = (Mac)JWT.threadLocalMac.get().clone();

        try {
            mac.init(keyProvider.getVerificationKey(header));
            mac.update(tokenBytes, 0, secondDot);
            byte[] calculatedSignature = mac.doFinal();

            byte[] decodedSignature = b64decoder.decode(tokenBytes, secondDot + 1, tokenBytes.length);

            // Note: Do not use Arrays.equals here to prevent timing attack
            // MessageDigest.isEqual is safe since Java6u17
            boolean signatureValid = MessageDigest.isEqual(calculatedSignature, decodedSignature);

            if (signatureValid) {
                byte[] decodedPayload = b64decoder.decode(tokenBytes, firstDot + 1, secondDot);
                Class<? extends IJwtPayload> payloadClass = payloadClasses.get(header.getType());
                if ( payloadClass == null ) {
                    throw new Exception("Payload class not registered:" + header.getType());
                }
                IJwtPayload payload = mapper.readValue(decodedPayload, payloadClass);
                token = new JsonWebToken(header, payload);
                return token;
            }

        }
        catch (Exception e) {
            throw e;
            // noop - maybe log a quiet error.
        }
        finally {
            mac.reset();
        }
        return token;
    }


}
