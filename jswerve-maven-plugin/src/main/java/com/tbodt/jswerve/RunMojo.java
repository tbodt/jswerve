package com.tbodt.jswerve;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.tbodt.jswerve.server.HttpProtocol;
import com.tbodt.jswerve.server.Server;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;

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
    private Set<Artifact> pluginArtifacts;
    
    /**
     * Location of the archive.
     */
    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.jar", required = true)
    private File archive;

    public void execute() throws MojoExecutionException {
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
        Set<URL> urls = new HashSet<URL>();
        try {
            for (Artifact artifact : projectArtifacts)
                if (!pluginArtifacts.contains(artifact))
                    urls.add(artifact.getFile().toURI().toURL());
            urls.add(archive.toURI().toURL());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex); // this really shouldn't happen
        }
        ClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), RunMojo.class.getClassLoader());

        Server server;
        try {
            server = new Server(new Website(loader), new HttpProtocol());
            server.start();
            server.join();
        } catch (InterruptedException ex) {
            throw new MojoExecutionException("Interrupted rudely", ex);
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed to create website", ex);
        }
    }
}
