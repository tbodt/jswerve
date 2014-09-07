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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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

    public Request(InputStream input) throws IOException, StatusCodeException {
        // First, slurp up the request (using the ASCII encoding).
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "ASCII"));
        Queue<String> lines = new ArrayDeque<String>();
        String line;
        while (!(line = reader.readLine()).equals(""))
            lines.add(line);
        // Remove initial blank lines, as reccomended by specification.
        while (lines.peek() != null && lines.peek().equals(""))
            lines.remove();
        ensure(!lines.isEmpty());

        // Second, process the request.
        String[] requestLine = lines.remove().split(" ");
        ensure(requestLine.length == 3);
        method = Method.forName(requestLine[0]);
        httpVersion = requestLine[2];
        ensure(httpVersion.matches("HTTP/\\d+\\.\\d+"));
        URI unresolvedUri;
        try {
            unresolvedUri = new URI(requestLine[1]);
        } catch (URISyntaxException ex) {
            throw new BadRequestException();
        }

        Map<String, String> theHeaders = new HashMap<String, String>();
        String header;
        while ((header = lines.poll()) != null) {
            String[] keyAndValue = header.split(":[ \t]*", 2);
            ensure(keyAndValue.length == 2, httpVersion);
            while (lines.peek() != null && (lines.peek().startsWith(" ") || lines.peek().startsWith("\t")))
                keyAndValue[1] += lines.remove().substring(1);
            theHeaders.put(keyAndValue[0], keyAndValue[1]);
        }
        headers = Collections.unmodifiableMap(theHeaders);
        
        if (headers.containsKey("Host"))
            uri = URI.create("http://" + headers.get("Host")).resolve(unresolvedUri);
        else
            uri = unresolvedUri;
        // TODO implement request bodies
    }

    private static void ensure(boolean what) throws BadRequestException {
        if (!what)
            throw new BadRequestException();
    }

    private static void ensure(boolean what, String httpVersion) throws BadRequestException {
        if (!what)
            throw new BadRequestException(httpVersion);
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
