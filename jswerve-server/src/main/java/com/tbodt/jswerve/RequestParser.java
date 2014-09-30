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

/**
 * An HTTP parser. Parses it one chunk/{@code ByteBuffer} at a time.
 *
 * NB: This doesn't work yet. I'm just figuring out the API. It will eventually, though.
 *
 * @author Theodore Dubois
 */
public class RequestParser {
    private State state = State.LINE;
    private boolean cr;
    private RuntimeException error;

    private enum State {
        LINE, HEADERS, DONE;
    }

    public void parseNext(ByteBuffer data) {
        if (error != null)
            return; // just hang out until eof
        try {
            switch (state) {
                case LINE:
                    parseLine(data);
                    break;
                default:
                    throw new BadRequestException();
            }
        } catch (StatusCodeException sce) {
            error = sce;
        } 
    }

    /**
     * Parse the next chunk of data.
     *
     * @param data the data
     */
    @SuppressWarnings("empty-statement")
    public void parseLine(ByteBuffer data) {
        byte ch;
        // Ignore empty lines at the beginning.
        while ((ch = next(data)) == '\n')
            ; // do nothing

    }

    private byte next(ByteBuffer data) {
        byte ch = data.get();
        if (cr) {
            if (ch != '\n')
                throw new BadRequestException();
            cr = false;
            return ch;
        }

        if (ch == '\r')
            if (data.hasRemaining()) {
                ch = data.get();
                if (ch != '\n')
                    throw new BadRequestException();
            } else {
                cr = true;
                return 0;
            }
        return ch;
    }

    /**
     * Finish it off. Return the parsed request.
     *
     * @return the parsed request
     * @throws StatusCodeException if there's an error in the request
     */
    public Request end() {
        if (error != null)
            throw error;
        return null;
    }
}
