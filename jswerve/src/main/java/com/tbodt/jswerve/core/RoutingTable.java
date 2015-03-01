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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
public final class RoutingTable {
    private final List<Route> routes;

    @SuppressWarnings("unchecked")
    private RoutingTable(Collection<Class<?>> classes) throws InvalidWebsiteException {
        List<Route> routesList = new ArrayList<Route>();
        // look through all the classes for fields that look like this:
        // public static final RouteInfo[] ROUTES = { ... };
        // or maybe
        // public static final RouteInfo[] SOME_ROUTES = { ... };
        // public static final RouteInfo[] OTHER_ROUTES = { ... };
        // Build the RouteInfo and add it to the routes list.
        for (Class<?> klass : classes)
            for (Field field : klass.getFields()) {
                int modifiers = field.getModifiers();
                if (field.getType() == RouteBuilders.RouteInfo[].class
                    && field.getName().endsWith("ROUTES")
                    && Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers))
                    try {
                        RouteBuilders.RouteInfo[] routeInfos = (RouteBuilders.RouteInfo[]) field.get(null);
                        for (RouteBuilders.RouteInfo routeInfo : routeInfos)
                            routesList.add(routeInfo.build());
                    } catch (IllegalArgumentException ex) {
                        throw new RuntimeException(ex);
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
            }
        routes = Collections.unmodifiableList(routesList);
    }

    /**
     * Return a new {@code RoutingTable} by finding the class in the classes
     * that implements {@link RoutesDefiner} and using it to define the routes.
     *
     * @param classes the classes in which to look for {@link RoutesDefiner}
     * @return a new {@code RoutingTable} from that {@link RoutesDefiner}
     * @throws InvalidWebsiteException if no {@link RoutesDefiner} could be
     * found, or more than one, or it could not be instantiated
     */
    public static RoutingTable extract(Collection<Class<?>> classes) throws InvalidWebsiteException {
        return new RoutingTable(classes);
    }

    /**
     * Return the routes in this routing table.
     *
     * @return the routes in this routing table
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     * Return the first route that matches the given request.
     *
     * @param request the request
     * @return the first route that matches the given request
     */
    public Route route(Request request) {
        for (Route route : routes)
            if (route.getMethods().contains(request.getMethod())
                && pathsMatch(request.getUri().getPath(), route.getPattern(), request))
                return route;
        throw new RoutingException(request);
    }

    private boolean pathsMatch(String path, String[] pattern, Request request) {
        int i, j;
        Map<String, String> paremeters = new HashMap<String, String>();
        String[] pathComponents = Route.pathComponents(path);
        for (i = 0, j = 0; i < pattern.length && j < pathComponents.length; i++, j++) {
            String patternComponent = pattern[i];
            String pathComponent = pathComponents[j];
            if (patternComponent.startsWith(":")) {
                if (!patternComponent.equals(":"))
                    paremeters.put(patternComponent.substring(1), pathComponent);
            } else if (!patternComponent.equals(pathComponent))
                return false;
        }
        if (i == pattern.length && j == pathComponents.length) {
            request.setPathParameters(paremeters);
            return true;
        } else
            return false;
    }
}
