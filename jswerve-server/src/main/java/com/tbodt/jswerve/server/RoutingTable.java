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

import com.tbodt.jswerve.*;
import com.tbodt.jswerve.controller.Controller;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
public final class RoutingTable {
    private final Route[] routes;

    private RoutingTable(Class<?>[] classes) {
        List<Route> routesList = new ArrayList<Route>();
        
        for (Class<?> klass : classes) {
            if (Controller.class.isAssignableFrom(klass)) {
                for (Method method : klass.getMethods()) {
                    for (Annotation annotation : method.getAnnotations()) {
                        if (isRoutingAnnotation(annotation)) {
                            routesList.add(new Route(annotation, method));
                        }
                    }
                }
            }
        }
        
        routes = routesList.toArray(new Route[routesList.size()]);
    }

    public static RoutingTable build(Class<?>[] classes) {
        return new RoutingTable(classes);
    }
    
    public Response route(Request request) {
        for (Route route : routes) {
            if (route.methods.contains(request.getMethod()) &&
                route.path.equals(request.getUri().getPath())) {
                Controller controller = Controller.instantiate((Class<? extends Controller>) route.action.getDeclaringClass());
                controller.invoke(route.action);
            }
        }
    }

    private final class Route {
        private final String path;
        private final EnumSet<Request.Method> methods;
        private final Method action;

        public Route(Annotation a, Method action) {
            this.action = action;
            if (a instanceof Match) {
                Match annotation = (Match) a;
                path = annotation.path();
                methods = EnumSet.copyOf(Arrays.asList(annotation.method()));
            } else
                throw new IllegalArgumentException("Annotation isn't a routing annotation");
        }

    }

    public static boolean isRoutingAnnotation(Annotation a) {
        return a instanceof Match;
    }
}
