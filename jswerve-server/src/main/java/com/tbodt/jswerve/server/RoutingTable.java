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

import com.tbodt.jswerve.Route;
import com.tbodt.jswerve.*;
import java.util.List;

/**
 *
 * @author Theodore Dubois
 */
public final class RoutingTable {
    private final List<Route> routes;

    private RoutingTable(Class<?>[] classes) throws InvalidWebsiteException {
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

    public static RoutingTable extract(Class<?>[] classes) throws InvalidWebsiteException {
        return new RoutingTable(classes);
    }

    public Response route(Request request) {
        for (Route route : routes)
            if (route.getMethods().contains(request.getMethod())
                    && pathsMatch(request.getUri().getPath(), route.getPattern())) {
                return new Response(StatusCode.OK, Headers.EMPTY_HEADERS);
            }
        return new Response(StatusCode.NOT_FOUND, Headers.EMPTY_HEADERS);
    }
    
    private boolean pathsMatch(String path, String[] pattern) {
        int i, j;
        String[] pathComponents = path.split("/+");
        for (i = 0, j = 0; i < pattern.length && j < pathComponents.length; i++, j++) {
            String patternComponent = pattern[i];
            String pathComponent = pattern[j];
            if (!patternComponent.startsWith(":")) {
                if (!patternComponent.equals(pathComponent))
                    return false;
            }
        }
        return i + 2 == pattern.length && j + 2 == pathComponents.length;
    }
}
