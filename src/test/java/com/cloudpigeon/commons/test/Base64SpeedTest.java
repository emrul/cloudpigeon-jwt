package com.cloudpigeon.commons.test;

import com.cloudpigeon.commons.util.Base64;
import com.cloudpigeon.commons.util.Base64Groovy;
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

    @Setup
	public void setUp() throws Exception
	{
		randomData = new byte[5000];
		new Random().nextBytes(randomData);
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

        n = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
              enc = Base64Groovy.encode(randomData);
              dec = Base64Groovy.decode(enc);
            //byte[] enc = Base64.encodeToByte(randomData, true);
            //byte[] dec = Base64.decode(enc);
        }

        System.out.println("Groovy Timed: " + ((System.nanoTime() - n) / 1000000f));

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
    public void measureGroovyBase64() {
        String enc;
        byte[] dec;

        enc = Base64Groovy.encode(randomData);
        dec = Base64Groovy.decode(enc);
    }


    @Benchmark
    public void measureMig() {
        String enc;
        byte[] dec;

        enc = Base64.encodeToString(randomData, true);
        dec = Base64.decode(enc);
    }
}