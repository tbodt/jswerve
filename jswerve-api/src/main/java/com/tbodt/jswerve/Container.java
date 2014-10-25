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

import java.util.HashMap;
import java.util.Map;

/**
 * A container for all the objects that would like to live in a container, and those that don't.
 *
 * @author Theodore Dubois
 */
public final class Container {
    private final Map<Class<?>, InstanceCache<?>> components = new HashMap<Class<?>, InstanceCache<?>>();

    public <T> T get(Class<T> clazz, Request context) throws ReflectionException {
        if (!components.containsKey(clazz))
            components.put(clazz, new SingleInstanceCache<T>(clazz));
        InstanceCache<T> instances = (InstanceCache<T>) components.get(clazz);
        return instances.get(context);
    }

    private static abstract class InstanceCache<T> {
        protected final Class<T> clazz;

        protected InstanceCache(Class<T> clazz) {
            this.clazz = clazz;
        }

        public abstract T get(Request context) throws ReflectionException;
    }

    private static final class SingleInstanceCache<T> extends InstanceCache<T> {
        private T instance;

        public SingleInstanceCache(Class<T> clazz) {
            super(clazz);
        }

        @Override
        public T get(Request context) throws ReflectionException {
            if (instance == null)
                try {
                    instance = clazz.newInstance();
                } catch (InstantiationException ex) {
                    throw new ReflectionException(ex);
                } catch (IllegalAccessException ex) {
                    throw new ReflectionException(ex);
                }
            return instance;
        }
    }
}
