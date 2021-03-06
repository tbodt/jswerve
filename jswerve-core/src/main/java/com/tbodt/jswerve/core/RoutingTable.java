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
import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
public final class RoutingTable {
    private final List<Route> routes;

    @SuppressWarnings("unchecked")
    private RoutingTable(Collection<Class<?>> classes) throws InvalidWebsiteException {
        Class<? extends RoutesDefiner> definerClass = null;
        for (Class<?> klass : classes)
            if (RoutesDefiner.class.isAssignableFrom(klass))
                if (definerClass == null)
                    definerClass = (Class<? extends RoutesDefiner>) klass;
                else
                    throw new InvalidWebsiteException("more than one RoutesDefiner");

        if (definerClass == null)
            throw new InvalidWebsiteException("no RoutesDefiner");

        try {
            routes = definerClass.newInstance().getRoutes();
        } catch (InstantiationException ex) {
            throw new InvalidWebsiteException("no no-arg constructor in RoutesDefiner");
        } catch (IllegalAccessException ex) {
            throw new InvalidWebsiteException("no public no-arg constructor in RoutesDefiner");
        }
    }

    /**
     * Return a new {@code RoutingTable} by finding the class in the classes that implements
     * {@link RoutesDefiner} and using it to define the routes.
     *
     * @param classes the classes in which to look for {@link RoutesDefiner}
     * @return a new {@code RoutingTable} from that {@link RoutesDefiner}
     * @throws InvalidWebsiteException if no {@link RoutesDefiner} could be found, or more than one,
     * or it could not be instantiated
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
