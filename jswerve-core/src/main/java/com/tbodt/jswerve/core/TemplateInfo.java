/*
 * Copyright (C) 2015 Theodore Dubois
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
package com.tbodt.jswerve.core;

import com.tbodt.jswerve.Template;
import com.tbodt.jswerve.controller.Controller;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Information about a template.
 *
 * @author Theodore Dubois
 */
public final class TemplateInfo {
    private final Class<? extends Template> templateClass;
    private final String name;
    private final String format;
    private final Template templateInstance;

    private static final Map<Class<? extends Template>, TemplateInfo> templates = new HashMap<Class<? extends Template>, TemplateInfo>();

    /**
     * Return all the {@code TemplateInfo}s for templates in the same package as
     * the given controller.
     *
     * @param controller the controller's class
     * @return all the {@code TemplateInfo}s for templates in the same package
     * as the given controller
     */
    public static Set<TemplateInfo> templatesForController(Class<? extends Controller> controller) {
        Set<TemplateInfo> templateSet = new HashSet<TemplateInfo>();
        Package controllerPackage = controller.getPackage();
        for (Class<? extends Template> templateClass : templates.keySet())
            if (templateClass.getPackage() == controllerPackage)
                templateSet.add(templates.get(templateClass));
        return templateSet;
    }

    /**
     * Renders the template and returns the result.
     *
     * @return the result of rendering the template
     */
    public String render() {
        return templateInstance.render();
    }

    /**
     * Return the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the format.
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    static void fillCache(Set<Class<?>> allClasses) throws InvalidWebsiteException {
        for (Class<?> klass : allClasses)
            if (Template.class.isAssignableFrom(klass)) {
                @SuppressWarnings("unchecked")
                Class<? extends Template> templateClass = (Class<? extends Template>) klass;
                templates.put(templateClass, new TemplateInfo(templateClass));
            }
    }

    private TemplateInfo(Class<? extends Template> templateClass) throws InvalidWebsiteException {
        this.templateClass = templateClass;
        try {
            templateInstance = templateClass.getConstructor().newInstance();
        } catch (NoSuchMethodException ex) {
            throw new InvalidWebsiteException("template has no no-arg constructor");
        } catch (InstantiationException ex) {
            throw new InvalidWebsiteException("template is an abstract class");
        } catch (IllegalAccessException ex) {
            throw new InvalidWebsiteException("template no-arg constructor is not public");
        } catch (InvocationTargetException ex) {
            throw new InvalidWebsiteException("template constructor threw an exception");
        }

        String className = templateClass.getSimpleName();
        int underscoreIndex = className.lastIndexOf('_');
        name = className.substring(0, underscoreIndex).replace('_', '.');
        format = className.substring(underscoreIndex + 1);
    }
}
