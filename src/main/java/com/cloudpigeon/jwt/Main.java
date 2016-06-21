package com.cloudpigeon.jwt;

import com.cloudpigeon.jwt.domain.IJsonWebToken;

/**
 * Created by emrul on 30/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class Main {
    public static void main(String[] args) throws Exception{
        byte[] sharedSecret;
        JWT jwt;
        sharedSecret = "tokenkey".getBytes();
        jwt = JWT.getInstance();
        IJsonWebToken token = jwt.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUmlkIjoiIzU6MyIsImV4cCI6MTQxMDgxOTE5NiwiZGF0YWJhc2VOYW1lIjoiVGVzdFRva2VuQXV0aCIsInN1YiI6ImVtcnVsQGVtcnVsLmNvbSIsImF1ZCI6Ik9yaWVudERiIiwiaXNzIjoiT3JpZW50RGIiLCJqdGkiOiI4N2YxZjIzNy1jMjhmLTRjMjctOWM0Yy05MDQ1MjgyMjgwYTEiLCJpYXQiOjE0MTA4MTkxODZ9.GD2r5Hf1hXSE_0R4BOMjfeZ8y_kBS2ysZvngAPTjjN8", sharedSecret);

    }
}
