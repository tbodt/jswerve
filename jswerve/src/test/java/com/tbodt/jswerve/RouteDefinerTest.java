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
                super();
            }
        }
        assertEquals(new NoRoutes().getRoutes(), Collections.emptyList());
    }
    
    @Test
    public void testGet() {
        class GetRoutes extends RoutesDefiner {
            public GetRoutes() {
                super(
                        get("/wonderful/path").to(Controller.class, "action")
                );
            }
        }
        List<Route> routes = new GetRoutes().getRoutes();
        assertEquals(1, routes.size());
        Route route = routes.get(0);
        assertEquals(EnumSet.of(HttpMethod.GET), route.getMethods());
        System.out.println(Arrays.toString(route.getPattern()));
        assertArrayEquals(new String[] {"wonderful", "path"}, route.getPattern());
        assertEquals(Controller.class, route.getController());
        assertEquals("action", route.getAction());
    }
    
    @Test
    public void testMatch() {
        class MatchRoutes extends RoutesDefiner {
            public MatchRoutes() {
                super(
                        match("/wonderful/path").via(HttpMethod.GET).to(Controller.class, "action")
                );
            }
        }
        List<Route> routes = new MatchRoutes().getRoutes();
        assertEquals(1, routes.size());
        Route route = routes.get(0);
        assertEquals(EnumSet.of(HttpMethod.GET), route.getMethods());
        assertArrayEquals(new String[] {"wonderful", "path"}, route.getPattern());
        assertEquals(Controller.class, route.getController());
        assertEquals("action", route.getAction());
    }
}
