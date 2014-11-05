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

import com.tbodt.jswerve.*;
import com.tbodt.jswerve.annotation.Path;
import com.tbodt.jswerve.annotation.PathParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Theodore Dubois
 */
public class AnnotatedMethodPage implements Page {
    private final Container container;
    private final Method method;
    private Pattern pathPattern;
    private List<String> pathParamNames;
    private final ParameterGenerator<?>[] paramGenerators;

    private static final Pattern PATH_PARSING_PATTERN = Pattern.compile("(?:\\*\\*?)|(?:\\{(\\w+)\\})");

    private AnnotatedMethodPage(Method method, Container container) throws InvalidWebsiteException {
        this.container = container;
        this.method = method;
        assert method.isAnnotationPresent(Path.class);

        parsePathPattern(method.getAnnotation(Path.class).value());

        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        // Interesting fact: You can't create arrays of non-wildcard generic types. Normally, you create an Object[] and cast it.
        paramGenerators = new ParameterGenerator<?>[paramAnnotations.length];
        for (int i = 0; i < paramAnnotations.length; i++) {
            if (paramAnnotations[i].length != 1)
                throw new InvalidWebsiteException("each parameter must have one annotation");
            paramGenerators[i] = ParameterGenerator.create(paramAnnotations[i][0]);
        }
    }

    private void parsePathPattern(String path) {
        pathParamNames = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PATH_PARSING_PATTERN.matcher(path);
        int lastEnd;
        while (matcher.find()) {
            sb.append(Pattern.quote(path.substring(0, matcher.start())));
            if (matcher.group().equals("*"))
                sb.append("[^/]+");
            else if (matcher.group().equals("**"))
                sb.append("[^/](?:.*[^/])?");
            else {
                pathParamNames.add(matcher.group(1));
                sb.append("([^/]+)");
            }
            lastEnd = matcher.end();
        }
        pathPattern = Pattern.compile(sb.toString());
        pathParamNames = Collections.unmodifiableList(pathParamNames);
    }

    private static abstract class ParameterGenerator<T> {
        public static ParameterGenerator create(Annotation annotation) throws InvalidWebsiteException {
            if (PathParam.class.isAssignableFrom(annotation.getClass()))
                return new PathParameterGenerator((PathParam) annotation);
            else
                return null;
        }

        public abstract T generateParameter(Request request, Map<String, String> pathParams);
    }

    private static final class PathParameterGenerator extends ParameterGenerator<String> {
        private final String paramName;

        private PathParameterGenerator(PathParam annotation) throws InvalidWebsiteException {
            paramName = annotation.value();
        }

        @Override
        public String generateParameter(Request request, Map<String, String> pathParams) {
            return pathParams.get(paramName);
        }
    }

    @Override
    public Response tryService(Request request) {
        Matcher matcher = pathPattern.matcher(request.getUri().getPath());
        if (!matcher.matches())
            return null;
        
        Map<String, String> pathParams = new HashMap<String, String>();
        for (int i = 0; i < matcher.groupCount(); i++)
            pathParams.put(pathParamNames.get(i), matcher.group(i + 1)); // i + 1 because matcher groups start with 1
        pathParams = Collections.unmodifiableMap(pathParams);
        
        Object[] parameters = new Object[paramGenerators.length];
        for (int i = 0; i < parameters.length; i++)
            parameters[i] = paramGenerators[i].generateParameter(request, pathParams);
        try {
            return (Response) method.invoke(container.get(method.getDeclaringClass(), request), parameters);
        } catch (IllegalAccessException ex) {
            throw new StatusCodeException(StatusCode.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException ex) {
            throw new StatusCodeException(StatusCode.INTERNAL_SERVER_ERROR);
        } catch (InvocationTargetException ex) {
            throw new StatusCodeException(StatusCode.INTERNAL_SERVER_ERROR);
        } catch (ReflectionException ex) {
            throw new StatusCodeException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public static Page[] introspect(Class<?> clazz, Container container) throws InvalidWebsiteException {
        Set<Page> pages = new HashSet<Page>();
        for (Method method : clazz.getMethods())
            if (method.isAnnotationPresent(Path.class))
                pages.add(new AnnotatedMethodPage(method, container));

        return pages.toArray(new Page[pages.size()]);
    }
}
