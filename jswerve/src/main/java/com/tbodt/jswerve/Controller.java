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

import java.nio.charset.Charset;

/**
 * An abstract class for controllers.
 *
 * @author Theodore Dubois
 */
public abstract class Controller {
    private Request request;
    private Response response;
    private String template;

    /**
     * Return the request that this controller is currently servicing.
     *
     * @return the request that this controller is currently servicing
     */
    public final Request getRequest() {
        return request;
    }

    /**
     * Set the request that this controller is currently servicing.
     *
     * @param request the new request
     */
    public final void setRequest(Request request) {
        this.request = request;
    }

    /**
     * Return the request parameter with the given name.
     *
     * @param name the name
     * @return the request parameter with the given name
     */
    protected final String getParam(String name) {
        return request.getParameters().get(name);
    }

    /**
     * Render the given text, with the text/plain MIME type.
     *
     * @param text the text
     */
    protected final void renderText(String text) {
        renderText(text, "text/plain");
    }

    /**
     * Render the given text with the given MIME type.
     *
     * @param text the text
     * @param contentType the MIME type
     */
    protected final void renderText(String text, String contentType) {
        renderText(text, contentType, StatusCode.OK);
    }

    /**
     * Render the given text with the given MIME type and status code.
     *
     * @param text the text
     * @param contentType the MIME type
     * @param status the status code
     */
    protected final void renderText(String text, String contentType, StatusCode status) {
        render(status, Headers.EMPTY_HEADERS, new Content(text.getBytes(Charset.forName("UTF-8")), contentType));
    }

    protected final void renderAction(String action) {

    }

    /**
     * Render a redirect to the given path with a status of See Other.
     *
     * @param path the path
     */
    protected final void redirectTo(String path) {
        redirectTo(path, StatusCode.SEE_OTHER);
    }

    /**
     * Render a redirect to the given path with the given status.
     *
     * @param path the path
     * @param status the status
     */
    protected final void redirectTo(String path, StatusCode status) {
        render(status, Headers.builder().header("location", path).build(), Content.EMPTY);
    }

    private void render(StatusCode status, Headers headers, Content content) {
        if (template != null || response != null)
            throw new DoubleRenderException();
        response = new Response(status, headers, content);
    }

    /**
     * Return the rendered response, or {@code null} if a render has not happened yet.
     *
     * @return the rendered response, or {@code null} if a render has not happened yet
     */
    public final Response getResponse() {
        return response;
    }
}
