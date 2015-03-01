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

import com.tbodt.jswerve.core.Route;
import java.util.EnumSet;

/**
 * An abstract class that defines the DSL for creating routes.
 *
 * @author Theodore Dubois
 */
public class RouteBuilders {

    /**
     * A builder for routes. {@code RouteInfo} objects are obtained from the
     * static methods on {@link RouteBuilders}. If the building methods are
     * called a second time, an {@code IllegalStateException} is thrown.
     */
    public static class RouteInfo {
        private final String[] pattern;
        private EnumSet<HttpMethod> methods;
        private Class<? extends Controller> controller;
        private String action;
        private boolean built = false;

        private RouteInfo(String pattern) {
            this.pattern = Route.pathComponents(pattern);
        }

        /**
         * Sets the destination of the route.
         *
         * @param controller the controller class
         * @param action the action in the controller
         * @return this, for chaining
         */
        public RouteInfo to(Class<? extends Controller> controller, String action) {
            if (this.controller != null || this.action != null)
                throw new IllegalStateException("to has already been specified");
            this.controller = controller;
            this.action = action;
            return this;
        }

        /**
         * Sets the matching request methods. There are two parameters because
         * that requires at least one method to be specified.
         *
         * @param first the first request method
         * @param rest the rest of the request methods.
         * @return this, for chaining
         */
        public RouteInfo via(HttpMethod first, HttpMethod... rest) {
            if (methods != null)
                throw new IllegalStateException("Via has already been specified");
            methods = EnumSet.of(first, rest);
            return this;
        }

        /**
         * Builds the route. This method should not be called. It is
         * automatically called by other parts of JSwerve.
         *
         * @return a new {@link Route} with the appropriate stuff
         */
        public Route build() {
            if (methods == null)
                throw new IllegalStateException("Must specify via");
            if (controller == null || action == null)
                throw new IllegalStateException("Must specify to");
            if (built)
                throw new IllegalStateException("Already been built");
            built = true;
            return new Route(pattern, methods, controller, action);
        }

    }

    /**
     * Return a {@link RouteInfo} that matches the given pattern. You will have
     * to configure the request methods, by calling {@link RouteInfo#via}.
     *
     * @param path the path pattern
     * @return the {@code RouteInfo} I just mentioned
     */
    public static RouteInfo match(String path) {
        return new RouteInfo(path);
    }

    /**
     * Return a {@link RouteInfo} that matches the given pattern with a GET
     * request method.
     *
     * @param path the path pattern
     * @return the {@code RouteInfo} I just mentioned
     */
    public static RouteInfo get(String path) {
        return new RouteInfo(path).via(HttpMethod.GET);
    }

    /**
     * Return a {@link RouteInfo} that matches the given pattern with a POST
     * request method.
     *
     * @param path the path pattern
     * @return the {@code RouteInfo} I just mentioned
     */
    public static final RouteInfo post(String path) {
        return new RouteInfo(path).via(HttpMethod.POST);
    }

    /**
     * Return a {@link RouteInfo} that matches the given pattern with a PUT
     * request method.
     *
     * @param path the path pattern
     * @return the {@code RouteInfo} I just mentioned
     */
    public static final RouteInfo put(String path) {
        return new RouteInfo(path).via(HttpMethod.PUT);
    }

    /**
     * Return a {@link RouteInfo} that matches the given pattern with a PATCH
     * request method.
     *
     * @param path the path pattern
     * @return the {@code RouteInfo} I just mentioned
     */
    public static final RouteInfo patch(String path) {
        return new RouteInfo(path).via(HttpMethod.PATCH);
    }

    /**
     * Return a {@link RouteInfo} that matches the given pattern with a DELETE
     * request method.
     *
     * @param path the path pattern
     * @return the {@code RouteInfo} I just mentioned
     */
    public static final RouteInfo delete(String path) {
        return new RouteInfo(path).via(HttpMethod.DELETE);
    }

    private RouteBuilders() {
    }
}
