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

import com.tbodt.jswerve.Website;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 *
 * @author Theodore Dubois
 */
public abstract class AbstractConnection implements Connection {
    protected final SocketChannel socket;
    private Interest interest;
    protected final Website website;
    private final Queue<ByteBuffer> queue = new ArrayDeque<ByteBuffer>();
    private static final ByteBuffer[] EMPTY_BYTE_BUFFER_ARRAY = new ByteBuffer[0];

    public AbstractConnection(Website website, SocketChannel socket, Interest interest) {
        this.website = website;
        this.socket = socket;
        this.interest = interest;
    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
        ByteBuffer data = ByteBuffer.allocate(1024);
        int count;
        while ((count = socket.read(data)) > 0) {
            data.flip();
            process(data);
            data.clear();
        }
        if (count == -1)
            close(); // don't infinitely try to read a connection closed by the other end
        
        if (key.isValid()) // it isn't if close was called
            key.interestOps(interest.getOps());
        key.selector().wakeup();
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        do {
            ByteBuffer[] queueArray = queue.toArray(EMPTY_BYTE_BUFFER_ARRAY);
            socket.write(queueArray);
            while (!queue.isEmpty() && !queue.peek().hasRemaining())
                queue.remove();
            if (queue.isEmpty())
                respond();
        } while (!queue.isEmpty());
        if (key.isValid()) // it isn't if close was called
            key.interestOps(interest.getOps());
        key.selector().wakeup();
    }

    protected abstract void process(ByteBuffer data);

    protected abstract void respond() throws IOException;

    protected final void send(ByteBuffer data) {
        queue.add(data);
    }

    protected final void close() throws IOException {
        socket.close();
    }

    @Override
    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }
}
