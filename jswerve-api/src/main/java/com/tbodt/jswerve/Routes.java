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
public abstract class Routes {
    private final List<Route> routes;
    private List<Route.Builder> builders;

    protected Routes() {
        builders = new ArrayList<Route.Builder>();
        defineRoutes();
        Route[] routesArray = new Route[builders.size()];
        for (int i = 0; i < builders.size(); i++)
            routesArray[i] = builders.get(i).build();
        routes = Arrays.asList(routesArray);
        builders = null; // we really shouldn't use it anymore
    }

    protected abstract void defineRoutes();

    protected final Route.Builder match(String path) {
        return new Route.Builder(path);
    }
}
