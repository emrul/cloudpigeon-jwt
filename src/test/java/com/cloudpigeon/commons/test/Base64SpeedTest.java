package com.cloudpigeon.commons.test;

import com.cloudpigeon.commons.util.Base64;
import junit.framework.TestCase;
import org.openjdk.jmh.annotations.*;

import java.beans.Encoder;
import java.io.IOException;
import java.util.Random;

/**
 * Created by emrul on 27/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
@State(Scope.Thread)
public class Base64SpeedTest extends TestCase
{
	private byte[] randomData;
    private java.util.Base64.Encoder java8Encoder;
    private java.util.Base64.Decoder java8Decoder;

    @Setup
	public void setUp() throws Exception
	{
		randomData = new byte[5000];
		new Random().nextBytes(randomData);
        java8Encoder = java.util.Base64.getEncoder();
        java8Decoder = java.util.Base64.getDecoder();
	}

    @TearDown
	public void tearDown() throws Exception
	{
		randomData = null;
	}

	public void test01() throws IOException
	{
		long n;

        String enc;
        byte[] dec;

        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        n = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
              enc = encoder.encodeToString(randomData);
              dec = decoder.decode(enc);
            //byte[] enc = Base64.encodeToByte(randomData, true);
            //byte[] dec = Base64.decode(enc);
        }

        System.out.println("Java8 Timed: " + ((System.nanoTime() - n) / 1000000f));

        /*
        n = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            enc = net.iharder.Base64.encodeBytes(randomData);
            dec = net.iharder.Base64.decode(enc, net.iharder.Base64.DONT_GUNZIP);
          //byte[] enc = Base64.encodeToByte(randomData, true);
          //byte[] dec = Base64.decode(enc);
        }
        */
        System.out.println("iHarder Timed: " + ((System.nanoTime() - n) / 1000000f));

        n = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
              enc = Base64.encodeToString(randomData, true);
              dec = Base64.decode(enc);
            //byte[] enc = Base64.encodeToByte(randomData, true);
            //byte[] dec = Base64.decode(enc);
        }
        System.out.println("Mig Timed: " + ((System.nanoTime() - n) / 1000000f));

	}

    @Benchmark
    public void measureMig() {
        String enc;
        byte[] dec;

        enc = Base64.encodeToString(randomData, true);
        dec = Base64.decode(enc);
    }


    @Benchmark
    public void measureJava8() {
        String enc;
        byte[] dec;

        enc = java8Encoder.encodeToString(randomData);
        dec = java8Decoder.decode(enc);
    }
}