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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * A mojo that runs JSwerve with the artifact.
 *
 * @author Theodore Dubois
 */
@Mojo(name = "run", requiresDirectInvocation = true, requiresDependencyResolution = ResolutionScope.COMPILE)
public class RunMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.artifacts}", readonly = true)
    private Set<Artifact> projectArtifacts;

    @Parameter(defaultValue = "${plugin.artifacts}", readonly = true)
    private List<Artifact> pluginArtifacts;
    
    /**
     * Location of the archive.
     */
    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.jar", required = true)
    private File archive;

    public void execute() throws MojoExecutionException, MojoFailureException {
        initLogging();
        
        Set<URL> urls = new HashSet<URL>();
        try {
            for (Artifact artifact : projectArtifacts)
                urls.add(artifact.getFile().toURI().toURL());
            for (Artifact artifact : pluginArtifacts)
                urls.add(artifact.getFile().toURI().toURL());
            urls.add(archive.toURI().toURL());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex); // this really shouldn't happen
        }
        ClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]));

        // We've got to reflect.
        try {
            Class<?> serverClass = loader.loadClass("com.tbodt.jswerve.server.Server");
            Class<?> websiteClass = loader.loadClass("com.tbodt.jswerve.server.Website");
            Class<?> protocolClass = loader.loadClass("com.tbodt.jswerve.server.Protocol");
            Class<?> httpProtocolClass = loader.loadClass("com.tbodt.jswerve.server.HttpProtocol");
            Class<?> fileClass = loader.loadClass("java.io.File");
            Class<?> classLoaderClass = loader.loadClass("java.lang.ClassLoader");

            Method start = serverClass.getMethod("start");
            Method join = serverClass.getMethod("join");

            Object website = websiteClass.getConstructor(fileClass, classLoaderClass).newInstance(archive, loader);
            Object protocol = httpProtocolClass.getConstructor().newInstance();
            Object server = serverClass.getConstructor(websiteClass, protocolClass).newInstance(website, protocol);

            start.invoke(server);
            join.invoke(server);
        } catch (ClassNotFoundException ex) {
            throw new MojoExecutionException("REFLECTION FAILURE", ex);
        } catch (NoSuchMethodException ex) {
            throw new MojoExecutionException("REFLECTION FAILURE", ex);
        } catch (InstantiationException ex) {
            throw new MojoExecutionException("REFLECTION FAILURE", ex);
        } catch (IllegalAccessException ex) {
            throw new MojoExecutionException("REFLECTION FAILURE", ex);
        } catch (IllegalArgumentException ex) {
            throw new MojoExecutionException("REFLECTION FAILURE", ex);
        } catch (InvocationTargetException ex) {
            // Now this means something.
            throw new MojoFailureException("Something bad happened", ex);
        }
    }

    private void initLogging() {
        final Log mavenLog = getLog();
        Logger log = Logger.getLogger("com.tbodt.jswerve");
        log.setUseParentHandlers(false);
        log.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                Level level;
                if (record.getLevel() == Level.CONFIG
                        || record.getLevel() == Level.FINE
                        || record.getLevel() == Level.FINER
                        || record.getLevel() == Level.FINEST)
                    level = Level.FINE;
                else
                    level = record.getLevel();

                if (level == Level.FINE)
                    mavenLog.debug(record.getMessage());
                else if (level == Level.INFO)
                    mavenLog.info(record.getMessage());
                else if (level == Level.WARNING)
                    mavenLog.warn(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
    }
}
