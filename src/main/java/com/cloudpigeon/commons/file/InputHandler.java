package com.cloudpigeon.commons.file;



import org.boon.core.reflection.BeanUtils;
import org.boon.primitive.ByteBuf;


import static org.boon.Boon.*;
import static org.boon.Lists.*;
import static org.boon.Str.*;
import static org.boon.core.Dates.*;
import static org.boon.Maps.map;
import static org.boon.Ok.okOrDie;
import static org.boon.primitive.Chr.multiply;

/**
 * Created by emrul on 27/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class InputHandler {

    /** How big our buffer size is, this is the max size of each write. */
    public final static int BUFFER_OUT_SIZE_MAX
            = Integer.parseInt ( System.getProperty ( "my.cool.company.BUFFER_OUT_SIZE_MAX", "100000" ) );

    /** Current output buffer. */
    private ByteBuf buffer = ByteBuf.create(BUFFER_OUT_SIZE_MAX);

    private int index = 0;

    public InputHandler(ChannelManager channelManager) {

        /* If the buffer is bigger than max or if the writer is waiting then send
        buffer on output channel.  */
        if ( buffer.len () >= BUFFER_OUT_SIZE_MAX || channelManager.isWriterWaiting () ) {
              channelManager.sendBufferToBeWritten( buffer );
              buffer = channelManager.allocateBuffer( BUFFER_OUT_SIZE_MAX );
        }
    }

    public void receive ( byte[] bodyOfPost, String desc ) { //called by outside world

        //wrap JSON in JSON array to make it valid JSON, and add header
        //[sequence, timestamp, [...original array]]
        buffer.add((byte) '[');
        buffer.add("" + index++);     //add sequence number
        buffer.add((byte) ',');
        buffer.add("" + euroUTCSystemDateNowString());     //add time stamp
        buffer.add((byte) ',');
        buffer.add(str(desc)); //add desc with quotes for valid JSON
        buffer.add((byte) ',');
        buffer.add(bodyOfPost);
        buffer.add("]\n");
    }
}
