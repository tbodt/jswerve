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
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
public final class ControllerInfo {
    private final Class<? extends Controller> controllerClass;
    private final Map<String, Method> actions;

    public ControllerInfo(Class<? extends Controller> controllerClass) {
        this.controllerClass = controllerClass;
        Map<String, Method> actionsMap = new HashMap<String, Method>();
        for (Method action : controllerClass.getMethods())
            if (action.getParameterCount() == 0 && action.getReturnType() == void.class)
                actionsMap.put(action.getName(), action);
        this.actions = Collections.unmodifiableMap(actionsMap);
    }
    
    public Controller instantiate() {
        try {
            return controllerClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void invoke(Controller controller, String action) {
    }

    public Map<String, Method> getActions() {
        return actions;
    }

    public Class<? extends Controller> getControllerClass() {
        return controllerClass;
    }
}
