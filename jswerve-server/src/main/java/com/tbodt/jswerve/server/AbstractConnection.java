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
    protected final Website website;
    protected final Queue<ByteBuffer> queue = new ArrayDeque<ByteBuffer>();

    public AbstractConnection(Website website, SocketChannel socket) {
        this.website = website;
        this.socket = socket;
    }

    private static final ByteBuffer[] EMPTY_BYTE_BUFFER_ARRAY = new ByteBuffer[0];

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        while (!queue.isEmpty()) {
            ByteBuffer[] queueArray = queue.toArray(EMPTY_BYTE_BUFFER_ARRAY);
            socket.write(queueArray);
            while (!queue.isEmpty() && !queue.peek().hasRemaining())
                queue.remove();
            if (queue.isEmpty())
                queueEmpty();
        }
    }

    protected abstract void queueEmpty() throws IOException;
}
