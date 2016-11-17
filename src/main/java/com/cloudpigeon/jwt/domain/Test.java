package com.cloudpigeon.jwt.domain;

import com.cloudpigeon.jwt.JWT;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by emrul on 28/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class Test {

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, IOException, CloneNotSupportedException, Exception {
        byte[] secret = "tokenkey".getBytes();
        JWT jwt = JWT.getInstance();
        IJsonWebToken token = jwt.parseToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjEyMzQ1Njc4OTAsIm5hbWUiOiJKb2huIERvZSIsImFkbWluIjp0cnVlfQ.l0zhNDK42j-jH4EoSJMte5j4bpBYaQxnRfW0GRT4KjY");
        //IJsonWebToken token = jwt.parseToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyUmlkIjoiIzU6MyIsImV4cCI6MTQxMDgxOTE5NiwiZGF0YWJhc2VOYW1lIjoiVGVzdFRva2VuQXV0aCIsInN1YiI6ImVtcnVsQGVtcnVsLmNvbSIsImF1ZCI6Ik9yaWVudERiIiwiaXNzIjoiT3JpZW50RGIiLCJqdGkiOiI4N2YxZjIzNy1jMjhmLTRjMjctOWM0Yy05MDQ1MjgyMjgwYTEiLCJpYXQiOjE0MTA4MTkxODZ9.GD2r5Hf1hXSE_0R4BOMjfeZ8y_kBS2ysZvngAPTjjN8", secret);

        System.out.println(jwt.signAndGetToken(token));
        return;
    }
}
