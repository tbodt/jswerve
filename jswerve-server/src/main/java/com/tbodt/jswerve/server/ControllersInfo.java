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

import com.tbodt.jswerve.controller.Controller;
import com.tbodt.jswerve.util.Inflections;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Theodore Dubois
 */
public final class ControllersInfo {
    private final Map<String, ControllerInfo> controllers;

    public ControllersInfo(Collection<? extends Class<?>> classes) {
        Map<String, ControllerInfo> controllersMap = new HashMap<String, ControllerInfo>();
        for (Class<?> klass : classes) {
            String simpleName = klass.getSimpleName();
            if (Controller.class.isAssignableFrom(klass) && simpleName.endsWith("Controller"))
                controllersMap.put(StringUtils.removeEnd(simpleName, "Controller"), new ControllerInfo((Class<? extends Controller>) klass));
        }
        controllers = Collections.unmodifiableMap(controllersMap);
    }
    
    public ControllerInfo getControllerInfo(String controller) {
        return controllers.get(Inflections.camelize(controller));
    }
}
