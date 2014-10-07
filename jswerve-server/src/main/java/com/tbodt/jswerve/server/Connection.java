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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 *
 * @author Theodore Dubois
 */
public interface Connection {
    /**
     * Do something when data arrives.
     *
     * @param data the data
     * @param key the selection key
     */
    void handleRead(ByteBuffer data, SelectionKey key);

    /**
     * Do something when the socket can handle data.
     *
     * @param key the selection key
     * @throws IOException if an I/O error occurs
     */
    void handleWrite(SelectionKey key) throws IOException;
}
