package com.cloudpigeon.commons.filetest;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Integer.MAX_VALUE;

public final class TestSequentialIoPerf {
    public static final int FILE_SIZE = 1_000_000_000;
    public static final int BUF_SIZE = 5_000;
    public static final String FILE_NAME = "test.dat";
    public static final byte[] BLANK_PAGE = new byte[FILE_SIZE / 10];

    private static PerfTestCase[] testCases =
            {
                    new PerfTestCase("RandomAccessFile JDK1.0      ") {
                        public int testWrite(final String fileName) throws Exception {
                            RandomAccessFile file = new RandomAccessFile(fileName, "rw");
                            final byte[] buffer = new byte[BUF_SIZE];
                            int pos = 0;
                            int checkSum = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                byte b = (byte) i;
                                checkSum += b;
                                buffer[pos++] = b;
                                if (BUF_SIZE == pos) {
                                    file.write(buffer, 0, BUF_SIZE);
                                    pos = 0;
                                }
                            }
                            file.close();
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            RandomAccessFile file = new RandomAccessFile(fileName, "r");
                            final byte[] buffer = new byte[BUF_SIZE];
                            int checkSum = 0;
                            int bytesRead;
                            while (-1 != (bytesRead = file.read(buffer))) {
                                for (int i = 0; i < bytesRead; i++) {
                                    checkSum += buffer[i];
                                }
                            }
                            file.close();
                            return checkSum;
                        }
                    },
                    new PerfTestCase("BufferedStreamFile JDK 1.0   ") {
                        public int testWrite(final String fileName) throws Exception {
                            int checkSum = 0;
                            OutputStream out =
                                    new BufferedOutputStream(new FileOutputStream(fileName));
                            for (long i = 0; i < FILE_SIZE; i++) {
                                byte b = (byte) i;
                                checkSum += b;
                                out.write(b);
                            }
                            out.close();
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            int checkSum = 0;
                            InputStream in =
                                    new BufferedInputStream(new FileInputStream(fileName));
                            int b;
                            while (-1 != (b = in.read())) {
                                checkSum += (byte) b;
                            }
                            in.close();
                            return checkSum;
                        }
                    },
                    new PerfTestCase("BufferedChannelFile NIO      ") {
                        public int testWrite(final String fileName) throws Exception {
                            FileChannel channel =
                                    new RandomAccessFile(fileName, "rw").getChannel();
                            ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
                            int checkSum = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                byte b = (byte) i;
                                checkSum += b;
                                buffer.put(b);
                                if (!buffer.hasRemaining()) {
                                    buffer.flip();
                                    channel.write(buffer);
                                    buffer.clear();
                                }
                            }
                            channel.close();
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            FileChannel channel =
                                    new RandomAccessFile(fileName, "rw").getChannel();
                            ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
                            int checkSum = 0;
                            while (-1 != (channel.read(buffer))) {
                                buffer.flip();
                                while (buffer.hasRemaining()) {
                                    checkSum += buffer.get();
                                }
                                buffer.clear();
                            }
                            return checkSum;
                        }
                    },
                    new PerfTestCase("MemoryMappedFile NIO         ") {
                        public int testWrite(final String fileName) throws Exception {
                            FileChannel channel =
                                    new RandomAccessFile(fileName, "rw").getChannel();
                            MappedByteBuffer buffer =
                                    channel.map(MapMode.READ_WRITE, 0,
                                            Math.min(channel.size(), MAX_VALUE));
                            int checkSum = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                if (!buffer.hasRemaining()) {
                                    buffer =
                                            channel.map(MapMode.READ_WRITE, i,
                                                    Math.min(channel.size() - i, MAX_VALUE));
                                }
                                byte b = (byte) i;
                                checkSum += b;
                                buffer.put(b);
                            }
                            channel.close();
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            FileChannel channel =
                                    new RandomAccessFile(fileName, "rw").getChannel();
                            MappedByteBuffer buffer =
                                    channel.map(MapMode.READ_ONLY, 0,
                                            Math.min(channel.size(), MAX_VALUE));
                            int checkSum = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                if (!buffer.hasRemaining()) {
                                    buffer =
                                            channel.map(MapMode.READ_WRITE, i,
                                                    Math.min(channel.size() - i, MAX_VALUE));
                                }
                                checkSum += buffer.get();
                            }
                            channel.close();
                            return checkSum;
                        }
                    },
                    new PerfTestCase("StreamFile1xPage JDK 1.0     ") {
                        public final byte[] buffer = new byte[BUF_SIZE];

                        public int testWrite(final String fileName) throws Exception {
                            int checkSum = 0;
                            OutputStream out =
                                    new FileOutputStream(fileName);
                            int index = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                byte b = (byte) i;
                                checkSum += b;
                                buffer[index] = b;
                                index++;
                                if (index == buffer.length) {
                                    index = 0;
                                    out.write(buffer);
                                }
                            }
                            out.close();
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            int checkSum = 0;
                            InputStream in =
                                    new BufferedInputStream(new FileInputStream(fileName));
                            int b;
                            while (-1 != (b = in.read())) {
                                checkSum += (byte) b;
                            }
                            in.close();
                            return checkSum;
                        }
                    },
                    new PerfTestCase("StreamFile2xPage JDK1.0      ") {
                        public final byte[] buffer = new byte[BUF_SIZE * 2];

                        public int testWrite(final String fileName) throws Exception {
                            int checkSum = 0;
                            OutputStream out =
                                    new FileOutputStream(fileName);
                            int index = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                byte b = (byte) i;
                                checkSum += b;
                                buffer[index] = b;
                                index++;
                                if (index == buffer.length) {
                                    index = 0;
                                    out.write(buffer);
                                }
                            }
                            out.close();
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            int checkSum = 0;
                            InputStream in =
                                    new FileInputStream(fileName);
                            int count = buffer.length;
                            while (count == buffer.length) {
                                count = in.read(buffer);
                                for (int index = 0; index < count; index++) {
                                    checkSum += buffer[index];
                                }
                            }
                            in.close();
                            return checkSum;
                        }
                    },
                    new PerfTestCase("StreamFile10xPage JDK1.0     ") {
                        public final byte[] buffer = new byte[BUF_SIZE * 10];

                        public int testWrite(final String fileName) throws Exception {
                            int checkSum = 0;
                            OutputStream out =
                                    new FileOutputStream(fileName);
                            int index = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                byte b = (byte) i;
                                checkSum += b;
                                buffer[index] = b;
                                index++;
                                if (index == buffer.length) {
                                    index = 0;
                                    out.write(buffer);
                                }
                            }
                            out.close();
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            int checkSum = 0;
                            InputStream in =
                                    new FileInputStream(fileName);
                            int count = buffer.length;
                            while (count == buffer.length) {
                                count = in.read(buffer);
                                for (int index = 0; index < count; index++) {
                                    checkSum += buffer[index];
                                }
                            }
                            in.close();
                            return checkSum;
                        }
                    },
                    new PerfTestCase("Files readAll/writeAll JDK1.7") {
                        public final byte[] buffer = new byte[FILE_SIZE];

                        public int testWrite(final String fileName) throws Exception {
                            final Path filePath = Paths.get(fileName);
                            int checkSum = 0;
                            for (long i = 0; i < FILE_SIZE; i++) {
                                byte b = (byte) i;
                                checkSum += b;
                                buffer[(int) i] = b;
                            }
                            Files.write(filePath, buffer);
                            return checkSum;
                        }

                        public int testRead(final String fileName) throws Exception {
                            final Path filePath = Paths.get(fileName);
                            final byte[] inBuffer = Files.readAllBytes(filePath);
                            int checkSum = 0;
                            for (int index = 0; index < inBuffer.length; index++) {
                                checkSum += inBuffer[index];
                            }
                            return checkSum;
                        }
                    },
            };

    public static void main(final String... args) throws Exception {
        deleteFile(FILE_NAME);
        System.out.printf("ABOUT TO PREALLOCATE %,d\n", FILE_SIZE);
        preallocateTestFile(FILE_NAME);
        System.out.printf("ABOUT TO WRITE %,d\n", FILE_SIZE);
        for (final PerfTestCase testCase : testCases) {
            long bytesReadPerSecSum = 0;
            long bytesWrittenPerSecSum = 0;
            int numRuns = 5;
            for (int i = 0; i < numRuns; i++) {
                System.gc();
                long writeDurationMs = testCase.test(PerfTestCase.Type.WRITE,
                        FILE_NAME);
                System.gc();
                long readDurationMs = testCase.test(PerfTestCase.Type.READ,
                        FILE_NAME);
                long bytesReadPerSecond = (FILE_SIZE * 1000L) / readDurationMs;
                long bytesWrittenPerSecond = (FILE_SIZE * 1000L) / writeDurationMs;
                bytesWrittenPerSecSum += bytesWrittenPerSecond;
                bytesReadPerSecSum += bytesReadPerSecond;
            }
            System.out.format("AVG %s\twrite=%,d\tread=%,d bytes/sec\n",
                    testCase.getName(),
                    (bytesWrittenPerSecSum / numRuns), (bytesReadPerSecSum / numRuns));
        }
        System.out.printf("ABOUT TO DELETE %,d", FILE_SIZE);
        deleteFile(FILE_NAME);
    }

    private static void preallocateTestFile(final String fileName)
            throws Exception {
        RandomAccessFile file = new RandomAccessFile(fileName, "rw");
        for (long i = 0; i < FILE_SIZE; i += BLANK_PAGE.length) {
            file.write(BLANK_PAGE, 0, BLANK_PAGE.length);
        }
        file.close();
    }

    private static void deleteFile(final String testFileName) throws Exception {
        File file = new File(testFileName);
        if (!file.delete()) {
            System.out.println("Failed to delete test file=" + testFileName);
            System.out.println("Windows does not allow mapped files to be deleted.");
        }
    }

    public abstract static class PerfTestCase {
        private final String name;
        private int checkSum;

        public PerfTestCase(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public long test(final Type type, final String fileName) {
            long start = System.currentTimeMillis();
            try {
                switch (type) {
                    case WRITE: {
                        checkSum = testWrite(fileName);
                        break;
                    }
                    case READ: {
                        final int checkSum = testRead(fileName);
                        if (checkSum != this.checkSum) {
                            final String msg = getName() +
                                    " expected=" + this.checkSum +
                                    " got=" + checkSum;
                            throw new IllegalStateException(msg);
                        }
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return System.currentTimeMillis() - start;
        }

        public abstract int testWrite(final String fileName) throws Exception;

        public abstract int testRead(final String fileName) throws Exception;

        public enum Type {READ, WRITE}
    }
}
