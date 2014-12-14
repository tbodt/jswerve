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

import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
public abstract class RoutesDefiner {
    private Route[] routes;

    protected final void draw(RouteInfo... routesInfo) {
        if (routes == null) {
            routes = new Route[routesInfo.length];
            for (int i = 0; i < routesInfo.length; i++)
                routes[i] = routesInfo[i].build();
        } else
            throw new IllegalStateException("draw already called");
    }

    public static class RouteInfo {
        private final String[] pattern;
        private EnumSet<Request.Method> methods;
        private String controller;
        private String action;
        private boolean built = false;

        public RouteInfo(String pattern) {
            this.pattern = pattern.split("/+");
        }

        public RouteInfo to(String controller, String action) {
            if (this.controller != null || this.action != null)
                throw new IllegalStateException("to has already been specified");
            this.controller = controller;
            this.action = action;
            return this;
        }

        public RouteInfo via(Request.Method first, Request.Method... rest) {
            if (methods != null)
                throw new IllegalStateException("Via has already been specified");
            methods = EnumSet.of(first, rest);
            return this;
        }

        Route build() {
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

    protected final RouteInfo match(String path) {
        return new RouteInfo(path);
    }
    
    protected final RouteInfo get(String path) {
        return new RouteInfo(path).via(Request.Method.GET);
    }

    public List<Route> getRoutes() {
        return Arrays.asList(routes);
    }
}
