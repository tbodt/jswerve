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
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Theodore Dubois
 */
public class RouteDefinerTest {
    @Test
    public void testNoRoutes() {
        class NoRoutes extends RoutesDefiner {
            public NoRoutes() {
                draw();
            }
        }
        assertEquals(new NoRoutes().getRoutes(), Collections.emptyList());
    }
    
    @Test
    public void testGet() {
        class GetRoutes extends RoutesDefiner {
            public GetRoutes() {
                draw(
                        get("/wonderful/path").to("controller", "action")
                );
            }
        }
        List<Route> routes = new GetRoutes().getRoutes();
        assertEquals(routes.size(), 1);
        Route route = routes.get(0);
        assertEquals(route.getMethods(), EnumSet.of(Request.Method.GET));
        assertEquals(route.getPath(), "/wonderful/path");
        assertEquals(route.getController(), "controller");
        assertEquals(route.getAction(), "action");
    }
    
    @Test
    public void testMatch() {
        class MatchRoutes extends RoutesDefiner {
            public MatchRoutes() {
                draw(
                        match("/wonderful/path").via(Request.Method.GET).to("controller", "action")
                );
            }
        }
        List<Route> routes = new MatchRoutes().getRoutes();
        assertEquals(routes.size(), 1);
        Route route = routes.get(0);
        assertEquals(route.getMethods(), EnumSet.of(Request.Method.GET));
        assertEquals(route.getPath(), "/wonderful/path");
        assertEquals(route.getController(), "controller");
        assertEquals(route.getAction(), "action");
    }
}
