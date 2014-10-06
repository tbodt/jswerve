/*
 * Copyright (C) 2014 Theodore Dubois
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tbodt.jswerve;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A single HTTP connection. Attached to the selector key for the socket channel for the HTTP connection.
 *
 * @author Theodore Dubois
 */
public class Connection {
    private final Website website;
    private final Request.Parser parser = new Request.Parser();
    private Request request;
    
    private final Queue<ByteBuffer> outputQueue = new ArrayDeque<ByteBuffer>();
    
    public Connection(Website website) {
        this.website = website;
    }

    /**
     * Do something about when we get data. Up to 1024 bytes of data is in the buffer, and the limit is at the end of the data.
     *
     * @param data the data
     * @param key the selection key, in case you need it
     */
    public void handleRead(ByteBuffer data, SelectionKey key) {
        if (parser.parseNext(data)) {
            // An entire request was recieved! Yay!
            request = parser.getRequest();
            Response response;
            String httpVersion = request.getHttpVersion();
            try {
                response = website.service(request);
            } catch (StatusCodeException ex) {
                StatusCode status = ex.getStatusCode();
                if (ex instanceof BadRequestException)
                    httpVersion = ((BadRequestException) ex).getHttpVersion();
                else
                    httpVersion = "HTTP/1.1";
                response = new Response(status);
            }
            for (ByteBuffer buffer : response.toBytes(httpVersion))
                outputQueue.add(buffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {
        WritableByteChannel channel = (WritableByteChannel) key.channel();
        int count = -1;
        while (!outputQueue.isEmpty() && count != 0) {
            count = channel.write(outputQueue.poll());
        }
        if (outputQueue.isEmpty())
            channel.close(); // done with output
    }
}
