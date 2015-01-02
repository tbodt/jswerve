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
package com.tbodt.jswerve.core;

import com.tbodt.jswerve.*;
import com.tbodt.jswerve.util.UrlUtils;
import java.net.*;
import java.util.*;

/**
 * An RFC-2616 compliant HTTP request.
 *
 * @see RFC-2616 section 5
 * @author Theodore Dubois
 */
public final class Request {
    private final HttpMethod method;
    private final URI uri;
    private final Headers headers;
    private final Content body;

    private Map<String, String> parameters;
    private Map<String, String> queryParameters;
    private Map<String, String> postParameters;
    private Map<String, String> pathParameters;

    public Request(HttpMethod method, URI uri, Headers headers) {
        this(method, uri, headers, Content.EMPTY);
    }
    
    public Request(HttpMethod method, URI uri, Headers headers, Content body) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> getParameters() {
        if (parameters == null) {
            Map<String, String> parametersMap = new HashMap<String, String>();
            parametersMap.putAll(getQueryParameters());
            parametersMap.putAll(getPostParameters());
            parametersMap.putAll(getPathParameters());
            parameters = Collections.unmodifiableMap(parametersMap);
        }
        return parameters;

    }

    public Map<String, String> getQueryParameters() {
        if (queryParameters == null)
            if (uri.getQuery() != null)
                queryParameters = decodeParameters(uri.getQuery());
            else
                queryParameters = Collections.emptyMap();
        return queryParameters;
    }

    public Map<String, String> getPostParameters() {
        if (postParameters == null)
            if (body.getContentType() != null && body.getContentType().equals("application/x-www-form-urlencoded"))
                postParameters = decodeParameters(body.toString());
            else
                postParameters = Collections.emptyMap();
        return postParameters;
    }

    private static Map<String, String> decodeParameters(String encoded) {
            Map<String, String> map = new HashMap<String, String>();
            StringTokenizer tok = new StringTokenizer(encoded, "&");
            while (tok.hasMoreTokens()) {
                String parameter = tok.nextToken();
                int equalIndex = parameter.indexOf("=");
                if (equalIndex == -1)
                    continue;
                String key = UrlUtils.decode(parameter.substring(0, equalIndex));
                String value = UrlUtils.decode(parameter.substring(equalIndex + 1));
                if (key.equals(""))
                    continue;
                map.put(key, value);
            }
            return Collections.unmodifiableMap(map);
    }

    public Map<String, String> getPathParameters() {
        if (pathParameters == null)
            return Collections.emptyMap();
        return pathParameters;
    }

    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = Collections.unmodifiableMap(pathParameters);
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

    public Content getBody() {
        return body;
    }
}
