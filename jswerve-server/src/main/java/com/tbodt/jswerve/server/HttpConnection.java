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
package com.tbodt.jswerve.server;

import com.tbodt.jswerve.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * A single HTTP connection. Attached to the selector key for the socket channel for the HTTP connection.
 *
 * @author Theodore Dubois
 */
public class HttpConnection extends AbstractConnection {
    private final Request.Parser parser = new Request.Parser();
    private Request request;
    private ReadableByteChannel responseIn;
    private final ByteBuffer outputBuffer = ByteBuffer.allocate(1024);
    
    public HttpConnection(Website website, SocketChannel socket) {
        super(website, socket);
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
            Response response;
            String httpVersion;
            try {
                request = parser.getRequest();
                response = website.service(request);
                httpVersion = request.getHttpVersion();
            } catch (StatusCodeException ex) {
                StatusCode status = ex.getStatusCode();
                if (ex instanceof BadRequestException)
                    httpVersion = ((BadRequestException) ex).getHttpVersion();
                else
                    httpVersion = "HTTP/1.1";
                response = new Response(status);
            }
            queue.add(response.toBytes(httpVersion));
            responseIn = Channels.newChannel(response.getInputStream());
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    @Override
    protected void queueEmpty() throws IOException {
        outputBuffer.clear();
        responseIn.read(outputBuffer);
        outputBuffer.flip();
        queue.add(outputBuffer);
    }
}
