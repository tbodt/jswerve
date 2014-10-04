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

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * A single HTTP connection. Attached to the selector key for the socket channel for the HTTP connection.
 *
 * @author Theodore Dubois
 */
public class Connection {
    private final RequestParser parser = new RequestParser();
    /**
     * Do something about when we get data. Up to 1024 bytes of data is in the buffer, and the limit is at the end of the data.
     *
     * @param buffer the data
     * @param key the selection key, in case you need it
     */
    public void handleRead(ByteBuffer buffer, SelectionKey key) {
        if (parser.parseNext(buffer))
            System.out.println("Done");
    }
}
