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
    
    Request(Request.Method method, URI uri, String httpVersion, Map<String, String> headers) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
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
