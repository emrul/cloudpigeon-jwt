package com.cloudpigeon.commons.file;

import org.boon.Exceptions;
import org.boon.IO;
import org.boon.primitive.ByteBuf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.boon.Exceptions.*;
/**
 * Created by emrul on 27/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class BatchWriter {
    private OutputStream outputStream;
    private String fileName;

    //@Override
    public void nextBufferToWrite ( final ByteBuf bufferOut ) {
        final int size = bufferOut.len ();
        final byte[] bytes = bufferOut.readForRecycle ();
        write ( bytes, size );
    }

    private void write ( final byte[] bytes, int size ) {

        //initOutputStreamIfNeeded();
        try {
            outputStream.write ( bytes, 0, size );
        } catch ( IOException e ) {
            //diagnose();
            Exceptions.handle ( "Unable to write out the bytes to the outputstream " + fileName, e );
        }

    }
    private void initOutputStream () {
        try {
            outputStream = streamCreator ();
        } catch ( Exception ex ) {
            Exceptions.handle(ex);
        }
    }

    protected OutputStream streamCreator () throws Exception {
        return Files.newOutputStream(IO.path(fileName)) ;
    }

    public void syncToDisk() {

    }
}
