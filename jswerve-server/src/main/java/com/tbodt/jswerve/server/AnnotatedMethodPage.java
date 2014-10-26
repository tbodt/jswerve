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

import com.tbodt.jswerve.Request;
import com.tbodt.jswerve.Response;
import com.tbodt.jswerve.annotation.Path;
import com.tbodt.jswerve.annotation.PathParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Theodore Dubois
 */
public class AnnotatedMethodPage implements Page {
    private final Method method;
    private final String path;
    private final ParameterGenerator[] parameters;
    
    public AnnotatedMethodPage(Method method) {
        this.method = method;
        assert method.isAnnotationPresent(Path.class);
        
        path = method.getAnnotation(Path.class).value();
        parameters = method.getParameterAnnotations();
    }
    
    private static abstract class ParameterGenerator<T> {
        public static ParameterGenerator create(Annotation annotation) {
            if (annotation.getClass() == PathParam.class)
                return new PathParameterGenerator((PathParam) annotation);
            else
                return null;
        }
        
        public abstract T generateParameter(Request request);
    }
    
    private static final class PathParameterGenerator extends ParameterGenerator<String> {
        private final PathParam annotation;
        
        private PathParameterGenerator(PathParam annotation) {
            this.annotation = annotation;
        }

        @Override
        public String generateParameter(Request request) {
            
        }
    }
    
    @Override
    public Response tryService(Request request) {
        throw new UnsupportedOperationException("Method tryService in class AnnotatedMethodPage is not implemented");
    }
    
    public static Page[] introspect(Class<?> clazz) throws ReflectionException {
        Set<Page> pages = new HashSet<Page>();
        for (Method method : clazz.getMethods())
            if (method.isAnnotationPresent(Path.class))
                pages.add(new AnnotatedMethodPage(method));
        
        return pages.toArray(new Page[pages.size()]);
    }
}
