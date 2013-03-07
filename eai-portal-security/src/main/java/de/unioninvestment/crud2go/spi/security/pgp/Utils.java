package de.unioninvestment.crud2go.spi.security.pgp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public final class Utils {

    public static ByteArrayOutputStream read(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fastCopy(stream, baos);
        return baos;
    }

    public static void fastCopy(InputStream input, OutputStream output) throws IOException {
        ReadableByteChannel src = Channels.newChannel(input);
        WritableByteChannel dest = Channels.newChannel(output);
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
        src.close();
        dest.close();
    }
}
