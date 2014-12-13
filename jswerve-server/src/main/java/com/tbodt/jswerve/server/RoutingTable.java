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
import com.tbodt.jswerve.controller.Controller;

/**
 *
 * @author Theodore Dubois
 */
public final class RoutingTable {
    private final Routes routes;

    private RoutingTable(Class<?>[] classes) throws InvalidWebsiteException {
        for (Class<?> klass : classes) {
            if (Routes.class.isAssignableFrom(klass)) {
                try {
                    routes = ((Class<? extends Routes>) klass).newInstance();
                } catch (InstantiationException ex) {
                    throw new InvalidWebsiteException("You defined a routes constructor!!!");
                } catch (IllegalAccessException ex) {
                    throw new InvalidWebsiteException("You defined a routes constructor!!!");
                }
                break;
            }
        }
    }

    public static RoutingTable extract(Class<?>[] classes) throws InvalidWebsiteException {
        return new RoutingTable(classes);
    }
    
    public Response route(Request request) {
        for (Route route : routes.getRoutes()) {
            if (route.getMethods().contains(request.getMethod()) &&
                route.getPath().equals(request.getUri().getPath())) {
                Controller controller = Controller.instantiate((Class<? extends Controller>) route.action.getDeclaringClass());
                controller.invoke(route.action);
                return new Response
            }
        }
    }
}
