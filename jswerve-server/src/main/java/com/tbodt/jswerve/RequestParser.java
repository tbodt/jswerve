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
 * @author Theodore Dubois
 */
public class RequestParser {
    private ByteBuffer data;
    private State state = State.START;
    private StringBuilder string = new StringBuilder();
    private boolean cr;
    private RuntimeException error;
    
    private Request.Method method;

    private enum State {
        START {
            @Override
            public State parse(RequestParser p) {
                char ch;
                // Ignore empty lines at the beginning.
                do
                    ch = p.next();
                while (ch < ' ');
                p.string = new StringBuilder();
                p.string.append(ch);
                return State.METHOD;
            }
        }, METHOD {
            @Override
            public State parse(RequestParser p) {
                p.method = Request.Method.forName(p.readUntil(' '));
                return State.END;
            }
        }, END {
            @Override
            public State parse(RequestParser p) {
                p.data.position(0).limit(0);
                return State.END;
            }
        };

        public abstract State parse(RequestParser parser);
    }

    private char next() {
        if (!data.hasRemaining())
            throw new NeedMoreInputException();
        char ch = (char) data.get();
        if (cr) {
            if (ch != '\n')
                throw new BadRequestException();
            cr = false;
            return (char) ch;
        }

        if (ch == '\r')
            if (data.hasRemaining()) {
                ch = (char) data.get();
                if (ch != '\n')
                    throw new BadRequestException();
            } else {
                cr = true;
                throw new NeedMoreInputException();
            }
        return ch;
    }
    
    private String readUntil(char until) {
        char ch;
        while ((ch = next()) != until)
            string.append(ch);
        return string.toString();
    }

    /**
     * Parse the next chunk of data.
     *
     * @param data the data
     * @return whether parsing is done
     */
    public boolean parseNext(ByteBuffer data) {
        this.data = data;
        try {
            while (data.hasRemaining())
                state = state.parse(this);
        } catch (NeedMoreInputException ex) {
            // just catch it, and exit
        } catch (BadRequestException ex) {
            error = ex;
        }
        this.data = null;
        return state == State.END;
    }
    
    public Request getRequest() {
        if (state == State.END && error != null)
            throw error;
        return new Request(method, null, null, null);
    }

    private static class NeedMoreInputException extends RuntimeException {
        // No actual code is needed.
    }
}
