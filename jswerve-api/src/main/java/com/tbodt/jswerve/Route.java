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

import com.tbodt.jswerve.controller.Controller;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
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

    public String[] getPattern() {
        return pattern;
    }

    public EnumSet<HttpMethod> getMethods() {
        return methods;
    }

    public Class<? extends Controller> getController() {
        return controller;
    }

    public String getAction() {
        return action;
    }

    private static final Pattern PATH_COMPONENTS = Pattern.compile("[^/]+");
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String[] pathComponents(String path) {
        // This is, unfortunately, more complicated than it needs to be. Which is why it's a static method.
        List<String> components = new ArrayList<String>();
        Matcher matcher = PATH_COMPONENTS.matcher(path);
        while (matcher.find())
            components.add(matcher.group());
        return components.toArray(EMPTY_STRING_ARRAY);
    }
}
