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

import com.tbodt.jswerve.BadRequestException;
import com.tbodt.jswerve.Headers;
import com.tbodt.jswerve.HttpMethod;
import com.tbodt.jswerve.StatusCodeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

/**
 * An RFC-2616 compliant HTTP request.
 *
 * @see RFC-2616 section 5
 * @author Theodore Dubois
 */
public final class Request {
    private final HttpMethod method;
    private final URI uri;
    private final String httpVersion;
    private final Headers headers;

    private Request(HttpMethod method, URI uri, String httpVersion, Headers headers) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public static class Parser {
        private ByteBuffer data;
        private State state = State.START;
        private final StringBuilder string = new StringBuilder();
        private String headerName;
        private boolean cr;
        private boolean newline;
        private RuntimeException error;

        private HttpMethod method;
        private URI uri;
        private String httpVersion;
        private final Headers.Builder headersBuilder = new Headers.Builder();

        private enum State {
            START {
                @Override
                public State parse(Parser p) {
                    char ch;
                    // Ignore empty lines at the beginning.
                    do
                        ch = p.next();
                    while (ch == '\n');
                    p.string.setLength(0);
                    p.string.append(ch);
                    return State.METHOD;
                }
            },
            METHOD {
                @Override
                public State parse(Parser p) {
                    p.method = HttpMethod.forName(p.readChunk());
                    return State.URI;
                }
            },
            URI {
                @Override
                public State parse(Parser p) {
                    try {
                        p.uri = new URI(p.readChunk());
                    } catch (URISyntaxException ex) {
                        throw new BadRequestException();
                    }
                    return State.VERSION;
                }
            },
            VERSION {
                @Override
                public State parse(Parser p) {
                    p.httpVersion = p.readLastChunk();
                    return State.HEADER;
                }
            },
            HEADER {
                @Override
                public State parse(Parser p) {
                    char ch = p.next();
                    if (ch == '\n')
                        return State.DONE;
                    else {
                        p.string.setLength(0);
                        p.string.append(ch);
                        return State.HEADER_NAME;
                    }
                }
            },
            HEADER_NAME {
                @Override
                public State parse(Parser p) {
                    p.headerName = p.readChunk();
                    if (!p.headerName.endsWith(":"))
                        throw new BadRequestException();
                    p.headerName = p.headerName.substring(0, p.headerName.length() - 1);
                    return State.HEADER_VALUE;
                }
            },
            HEADER_VALUE {
                @Override
                public State parse(Parser p) {
                    String headerValue = p.readLastChunk();
                    p.headersBuilder.setHeader(p.headerName, headerValue);
                    return State.HEADER;
                }
            },
            DONE {
                @Override
                public State parse(Parser p) {
                    // We're done. Just absorb any more input.
                    p.data.position(0).limit(0);
                    return State.DONE;
                }
            },
            ERROR {
                @Override
                public State parse(Parser p) {
                    do
                        while (!p.newline)
                            p.newline = p.next() == '\n';
                    while (p.next() != '\n');
                    return State.DONE;
                }
            };

            public abstract State parse(Parser p);
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
            if (ch == '\n') {
                newline = true;
                throw new BadRequestException();
            }
            String chunk = string.toString();
            string.setLength(0);
            return chunk;
        }

        private String readLastChunk() {
            char ch;
            while ((ch = next()) != '\n')
                string.append(ch);
            String chunk = string.toString();
            string.setLength(0);
            return chunk;
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
                    try {
                        state = state.parse(this);
                    } catch (StatusCodeException ex) {
                        error = ex;
                        state = State.ERROR;
                    }
            } catch (NeedMoreInputException ex) {
                // just catch it, and exit
            }
            this.data = null;
            return state == State.DONE;
        }

        public Request getRequest() {
            if (state == State.DONE && error != null)
                throw error;
            Headers headers = headersBuilder.build();
            URI finalUri;
            if (headers.contains("Host"))
                finalUri = URI.create("http://" + headers.get("Host")).resolve(uri);
            else
                finalUri = uri;
            return new Request(method, finalUri, httpVersion, headers);
        }

        private static class NeedMoreInputException extends RuntimeException {
            // No actual code is needed.
        }
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public Headers getHeaders() {
        return headers;
    }
}
