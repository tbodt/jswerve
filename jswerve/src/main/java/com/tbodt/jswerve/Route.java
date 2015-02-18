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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A route.
 *
 * @author Theodore Dubois
 */
public final class Route {
    private final String[] pattern;
    private final EnumSet<HttpMethod> methods;
    private final Class<? extends Controller> controller;
    private final String action;

    Route(String[] pattern, EnumSet<HttpMethod> methods, Class<? extends Controller> controller, String action) {
        this.pattern = pattern;
        this.methods = methods;
        this.controller = controller;
        this.action = action;
    }

    /**
     * Return the pattern.
     *
     * @return the pattern
     */
    public String[] getPattern() {
        return pattern;
    }

    /**
     * Return the HTTP methods this route matches.
     *
     * @return the HTTP methods this route matches
     */
    public EnumSet<HttpMethod> getMethods() {
        return methods;
    }

    /**
     * Return the class of the controller who will service matching requests.
     *
     * @return the class of the controller who will service matching requests
     */
    public Class<? extends Controller> getController() {
        return controller;
    }

    /**
     * Return the action of the controller that will be invoked.
     *
     * @return the action of the controller that will be invoked
     */
    public String getAction() {
        return action;
    }

    /**
     * Whether the path matches this route.
     *
     * @param path the path
     * @return whether the path matches this route
     */
    public boolean matchesPath(String path) {
        int i, j;
        Map<String, String> parameters = new HashMap<String, String>();
        String[] pathComponents = Route.pathComponents(path);
        for (i = 0, j = 0; i < pattern.length && j < pathComponents.length; i++, j++) {
            String patternComponent = pattern[i];
            String pathComponent = pathComponents[j];
            if (patternComponent.startsWith(":")) {
                if (!patternComponent.equals(":"))
                    parameters.put(patternComponent.substring(1), pathComponent);
            } else if (!patternComponent.equals(pathComponent))
                return false;
        }
        return i == pattern.length && j == pathComponents.length;
    }

    private static final Pattern PATH_COMPONENTS = Pattern.compile("[^/]+");
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Return the components of a path.
     *
     * @param path the path
     * @return the components of that path
     */
    public static String[] pathComponents(String path) {
        // This is, unfortunately, more complicated than it needs to be. Which is why it's a static method.
        List<String> components = new ArrayList<String>();
        Matcher matcher = PATH_COMPONENTS.matcher(path);
        while (matcher.find())
            components.add(matcher.group());
        return components.toArray(EMPTY_STRING_ARRAY);
    }
}
