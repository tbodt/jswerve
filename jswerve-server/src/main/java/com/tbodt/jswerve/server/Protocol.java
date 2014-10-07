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
import java.nio.channels.SocketChannel;

/**
 *
 * @author Theodore Dubois
 */
public abstract class Protocol {
    private int port = getDefaultPort();

    /**
     * Creates a new connection object for this protocol.
     *
     * @param website the website
     * @param socket the socket
     * @return a new connection object for this protocol
     */
    public abstract Connection newConnection(Website website, SocketChannel socket);

    /**
     * Returns the port number for this protocol.
     *
     * @return the port number for this protocol
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port number for this protocol.
     *
     * @param port the new port number
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Return the default port if none is specified via {@code setPort}.
     *
     * @return the default port
     */
    protected abstract int getDefaultPort();
}
