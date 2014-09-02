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
import java.util.*;

/**
 * An RFC-2616 compliant HTTP request. 
 * 
 * @see RFC-2616 section 5
 * @author Theodore Dubois
 */
public abstract class Request {
    private String httpVersion;
    private Map<String, String> headers;
    
    public static Request readRequest(InputStream input) throws IOException, StatusCodeException {
        // First, slurp up the request (using the ASCII encoding).
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "ASCII"));
        Queue<String> lines = new ArrayDeque<String>();
        String line;
        while (!(line = reader.readLine()).equals(""))
            lines.add(line);
        // Remove initial blank lines, as reccomended by specification.
        while (lines.peek() != null && lines.peek().equals(""))
            lines.remove();
        
        // Second, process the request.
        String[] requestLine = lines.remove().split(" ");
        String method = requestLine[0];
        String requestUri = requestLine[1];
        String httpVersion = requestLine[2];
        Map<String, String> headers = new HashMap<String, String>();
        String header;
        while ((header = lines.poll()) != null) {
            String[] keyAndValue = header.split(":[ \t]*", 2);
            while (lines.peek() != null && (lines.peek().startsWith(" ") || lines.peek().startsWith("\t")))
                keyAndValue[1] += lines.poll().substring(1);
            headers.put(keyAndValue[0], keyAndValue[1]);
        }
        
        Request request;
        if (method.equals("GET"))
            request = new GetRequest(requestUri);
        else
            throw new StatusCodeException(StatusCode.NOT_IMPLEMENTED);
        request.httpVersion = httpVersion;
        
        System.out.println(request);
        return request;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
