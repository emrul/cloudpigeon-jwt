package com.cloudpigeon.commons.file;

import org.boon.primitive.ByteBuf;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.core.Dates.*;
/**
 * Created by emrul on 27/09/2014.
 *
 * @author Emrul Islam <emrul@emrul.com>
 *         Copyright 2014 Vivid Inventive Ltd
 */
public class ChannelManager {

    private final AtomicLong numberOfFlushesTotal = new AtomicLong();
    /**
     * Byte Buffers that are spent (spent = already written to disk) are put back on this queue.
     */
    private final TransferQueue<ByteBuf> recycleChannel = new LinkedTransferQueue<> ();
    /**
     * Byte buffers that have been received from HTTP post, but not written to disk.
     */
    private final TransferQueue<ByteBuf> inputChannel = new LinkedTransferQueue<>();

    private long lastFlushTime = 0;

    private BatchWriter writer;

    public ChannelManager() {
    }


    /**
     * Determines if we should see if the writer is busy before batching up a lot of
     * data. Turning this off helps with throughput at the expense of data safety.
     */
    private final static boolean TRANSFER_QUEUE_WRITER_WAITING_CHECK
            = Boolean.parseBoolean ( System.getProperty (
            "my.cool.company.TRANSFER_QUEUE_WRITER_WAITING_CHECK", "true" ) );


    public final ByteBuf allocateBuffer ( int size ) {

        ByteBuf spentBuffer = recycleChannel.poll ();
        if ( spentBuffer == null ) {
            spentBuffer = ByteBuf.create ( size );
        }
        return spentBuffer;
    }
    /**
     * This checks to see if the writer queue is waiting.
     * We don't want the writer queue to wait, but we also
     * don't want it to thread sync too much either.
     */
    //@Override
    public final boolean isWriterWaiting () {
        // This call causes about 5% write throughput
        // it has the advantage of reducing loss of buffered data to be written
        // in the very rare occurrence of an outage.
        return TRANSFER_QUEUE_WRITER_WAITING_CHECK &&
                inputChannel.hasWaitingConsumer ();
    }

    /**
     * This is the main processing loop for the batch writer processing.
     */
    private void processWrites () {
        while ( true ) {
            try {
                manageInputWriterChannel ();
            } catch ( InterruptedException e ) {
                if ( determineIfWeShouldExit () ) {
                    break;
                }
            }
        }
    }

    private boolean determineIfWeShouldExit() {
        return true;
    }


    /**
     * Queue and batch writer main logic.
     * This is where the magic happens.
     *
     * @throws InterruptedException
     */
    private void manageInputWriterChannel () throws InterruptedException {
        ByteBuf dataToWriteToFile;
        dataToWriteToFile = inputChannel.poll ();  //no wait

        //If it is null, it means the inputChannel is empty and we need to flush.
        if ( dataToWriteToFile == null ) {
            queueEmptyMaybeFlush ();
            dataToWriteToFile = inputChannel.poll ();
        }


        //If it is still null, this means that we need to wait
        //for more items to show up in the inputChannel.
        if ( dataToWriteToFile == null ) {
            dataToWriteToFile = waitForNextDataToWrite ();
        }

        //We have to check for null again because we could have been interrupted.
        if ( dataToWriteToFile != null ) {
            //Write it
            writer.nextBufferToWrite ( dataToWriteToFile );
            //Then give it back
            recycleChannel.offer ( dataToWriteToFile );

        }
    }

    /**
     * If we don't have any data, and we have flushed,
     * then we can wait on the queue. There is no sense spin-locking.
     * The poll(time, timeunit) call will block until there is something to do
     * or until the timeout.
     *
     * @return the next byte buffer.
     * @throws InterruptedException
     */
    private ByteBuf waitForNextDataToWrite () throws InterruptedException {
        ByteBuf dataToWriteToFile;

        dataToWriteToFile =
                inputChannel.poll (FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS,
                        TimeUnit.MILLISECONDS);

        return dataToWriteToFile;
    }

    /**
     * Periodic force flush. We can turn off periodic flushing and allow the OS
     * to decide best time to sync to disk for speed.
     * (Not much difference in speed on OSX).
     */
    private final static boolean PERIODIC_FORCE_FLUSH
            = Boolean.parseBoolean ( System.getProperty (
            "my.cool.company.PERIODIC_FORCE_FLUSH", "true" ) );


    /**
     * Force flush if queue is empty after this many mili-seconds.
     */
    private final static long FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS
            = Long.parseLong ( System.getProperty (
            "my.cool.company.FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS", "40" ) );

     /**
     * If we detect that the in-coming transfer queue channel is empty
     * then it could be an excellent time to sync to disk.
     */
    private void queueEmptyMaybeFlush () {
        if ( PERIODIC_FORCE_FLUSH ) {
            long currentTime = now();
            /* Try not to flush more than once every x times per mili-seconds time period. */
            if ( ( currentTime - lastFlushTime ) > FORCE_FLUSH_AFTER_THIS_MANY_MILI_SECONDS ) {
                writer.syncToDisk (); //could take 100 ms to 1 second
                lastFlushTime = now();
                this.numberOfFlushesTotal.incrementAndGet ();
            }
        }
    }

    public void sendBufferToBeWritten(ByteBuf buffer) {
        // ?
        writer.nextBufferToWrite(buffer);
    }
}
