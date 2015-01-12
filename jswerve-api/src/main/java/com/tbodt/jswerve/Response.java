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

/**
 * An HTTP response.
 *
 * @see Request
 * @author Theodore Dubois
 */
public class Response {
    private final StatusCode status;
    private final Headers headers;
    private final Content body;

    /**
     * Construct a response with the given status and headers, and with no body.
     *
     * @param status the status
     * @param headers the headers
     */
    public Response(StatusCode status, Headers headers) {
        this(status, headers, Content.EMPTY);
    }

    /**
     * Construct a response with the given status, headers, and body.
     *
     * @param status the status
     * @param headers the headers
     * @param body the body
     */
    public Response(StatusCode status, Headers headers, Content body) {
        this.status = status;
        Headers.Builder builder = headers.getBuilder();
        builder.setHeaders(headers);
        if (body.getMimeType() != null)
            builder.header("Content-Type", body.getMimeType());
        this.headers = builder.build();
        this.body = body;
    }

    /**
     * Return the status code.
     *
     * @return the status code
     */
    public StatusCode getStatus() {
        return status;
    }

    /**
     * Return the headers.
     *
     * @return the headers
     */
    public Headers getHeaders() {
        return headers;
    }

    /**
     * Return the body.
     *
     * @return the body
     */
    public Content getBody() {
        return body;
    }
}
