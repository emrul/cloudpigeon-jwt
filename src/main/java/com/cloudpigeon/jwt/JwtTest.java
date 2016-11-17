package com.cloudpigeon.jwt;

import com.cloudpigeon.jwt.domain.IJsonWebToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

/**
 * Created by emrul on 27/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
@State(Scope.Thread)
public class JwtTest
{
    private byte[] sharedSecret;

    private JWT jwt;
    @Setup
	public void setUp() throws Exception
	{
        sharedSecret = "tokenkeytokenkeytokenkeytokenkey".getBytes();
        jwt = JWT.getInstance();
	}

    @TearDown
	public void tearDown() throws Exception
	{
	}


    @Benchmark
    public void measureCpJwtParse() throws InvalidKeyException, NoSuchAlgorithmException, IOException, CloneNotSupportedException, Exception {
        //JWT jwt = new JWT();
        IJsonWebToken token = jwt.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUmlkIjoiIzU6MyIsImV4cCI6MTQxMDgxOTE5NiwiZGF0YWJhc2VOYW1lIjoiVGVzdFRva2VuQXV0aCIsInN1YiI6ImVtcnVsQGVtcnVsLmNvbSIsImF1ZCI6Ik9yaWVudERiIiwiaXNzIjoiT3JpZW50RGIiLCJqdGkiOiI4N2YxZjIzNy1jMjhmLTRjMjctOWM0Yy05MDQ1MjgyMjgwYTEiLCJpYXQiOjE0MTA4MTkxODZ9.GD2r5Hf1hXSE_0R4BOMjfeZ8y_kBS2ysZvngAPTjjN8");
    }


    @Benchmark
    public void measureNimbusJwtParse() throws ParseException, JOSEException {

        // To parse the JWS and verify it, e.g. on client-side
        SignedJWT signedJWT = SignedJWT.parse("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUmlkIjoiIzU6MyIsImV4cCI6MTQxMDgxOTE5NiwiZGF0YWJhc2VOYW1lIjoiVGVzdFRva2VuQXV0aCIsInN1YiI6ImVtcnVsQGVtcnVsLmNvbSIsImF1ZCI6Ik9yaWVudERiIiwiaXNzIjoiT3JpZW50RGIiLCJqdGkiOiI4N2YxZjIzNy1jMjhmLTRjMjctOWM0Yy05MDQ1MjgyMjgwYTEiLCJpYXQiOjE0MTA4MTkxODZ9.GD2r5Hf1hXSE_0R4BOMjfeZ8y_kBS2ysZvngAPTjjN8");

        JWSVerifier verifier = new MACVerifier(sharedSecret);

        signedJWT.verify(verifier);
        signedJWT.getHeader();
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
    }
}