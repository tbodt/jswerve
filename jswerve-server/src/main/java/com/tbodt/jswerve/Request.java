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

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * An RFC-2616 compliant HTTP request.
 *
 * @see RFC-2616 section 5
 * @author Theodore Dubois
 */
public final class Request {
    private final Request.Method method;
    private final URI uri;
    private final String httpVersion;
    private final Map<String, String> headers;

    public enum Method {
        GET;

        public static Method forName(String name) throws StatusCodeException {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException e) {
                throw new StatusCodeException(StatusCode.NOT_IMPLEMENTED);
            }
        }
    }

    private Request(Request.Method method, URI uri, String httpVersion, Map<String, String> headers) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public static class Parser {
        private ByteBuffer data;
        private State state = State.START;
        private StringBuilder string = new StringBuilder();
        private boolean cr;
        private RuntimeException error;

        private Request.Method method;

        private enum State {
            START {
                @Override
                public State parse(Parser p) {
                    char ch;
                    // Ignore empty lines at the beginning.
                    do
                        ch = p.next();
                    while (ch == '\n');
                    p.string = new StringBuilder();
                    p.string.append(ch);
                    return State.METHOD;
                }
            }, METHOD {
                @Override
                public State parse(Parser p) {
                    p.method = Method.forName(p.readChunk());
                    return State.END;
                }
            }, END {
                @Override
                public State parse(Parser p) {
                    p.data.position(0).limit(0);
                    return State.END;
                }
            };

            public abstract State parse(Parser parser);
        }

        /**
         * Return the next character. Converts CRLFs to LFs.
         * 
         * @return the next character, converting CRLFs to LFs.
         */
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

        /**
         * Return the next chunk of data, such as a method or version number. It can't end a line.
         * 
         * @return just look up
         */
        private String readChunk() {
            char ch;
            while ((ch = next()) != ' ' && ch != '\n')
                string.append(ch);
            if (ch == '\n')
                throw new BadRequestException();
            return string.toString();
        }
        
        private String readLastChunk() {
            char ch;
            while ((ch = next()) != '\n')
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

    public String getHttpVersion() {
        return httpVersion;
    }

    public Method getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
