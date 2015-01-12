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

    /**
     * Construct a request with the given parameters. Does nothing else.
     *
     * @param method the http method
     * @param uri the uri
     * @param headers the headers
     * @param body the body
     */
    public Request(HttpMethod method, URI uri, Headers headers, Content body) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Return the parameters of the request. This is a combination of the query parameters, form
     * parameters, and path parameters.
     *
     * @return the parameters of the request
     */
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

    /**
     * Return the query parameters of the request.
     *
     * @return the query parameters of the request
     */
    public Map<String, String> getQueryParameters() {
        if (queryParameters == null)
            if (uri.getQuery() != null)
                queryParameters = UrlEncodedFormParser.parse(uri.getQuery());
            else
                queryParameters = Collections.emptyMap();
        return queryParameters;
    }

    /**
     * Return the form parameters of the request.
     *
     * @return the form parameters of the request
     */
    public Map<String, String> getPostParameters() {
        if (postParameters == null)
            postParameters = body.parseFormParameters();
        return postParameters;
    }

    /**
     * Return the path parameters of the request.
     *
     * @return the path parameters of the request
     */
    public Map<String, String> getPathParameters() {
        if (pathParameters == null)
            return Collections.emptyMap();
        return pathParameters;
    }

    /**
     * Set the path parameters for the request. This method is intended to only be called by JSwerve
     * itself. It is public because the code that calls it is in another package.
     *
     * @param pathParameters the new path parameters
     */
    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = Collections.unmodifiableMap(pathParameters);
    }

    /**
     * Return the HTTP method.
     * @return the HTTP method
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Return the URI.
     * @return the URI
     */
    public URI getUri() {
        return uri;
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
     * @return the body
     */
    public Content getBody() {
        return body;
    }
}
