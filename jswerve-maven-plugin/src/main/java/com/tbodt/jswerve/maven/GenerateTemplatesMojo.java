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
package com.tbodt.jswerve.maven;

import com.tbodt.jtl.Jtl;
import java.io.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * A mojo that generates templates from JTL input.
 *
 * @author Theodore Dubois
 */
@Mojo(name = "generate-templates")
public class GenerateTemplatesMojo extends AbstractMojo {
    @Parameter(defaultValue = "${basedir}/src/main/templates", readonly = true)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/templates", readonly = true)
    private File outputDirectory;

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    /**
     * Executes the mojo.
     *
     * @throws MojoExecutionException if something really bad happens
     */
    public void execute() throws MojoExecutionException {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.addDefaultExcludes();
        scanner.setBasedir(sourceDirectory);
        scanner.setIncludes(new String[] {"**/*.jtl"});
        scanner.scan();

        for (String templatePath : scanner.getIncludedFiles()) {
            File template = new File(sourceDirectory, templatePath);

            String templateClassName =
                    StringUtils.removeEnd(templatePath, ".jtl")
                    .replace('.', '_')
                    .replace(File.separatorChar, '.');

            int lastDot = templateClassName.lastIndexOf('.');
            String templatePackage = templateClassName.substring(0, lastDot);
            String templateName = templateClassName.substring(lastDot + 1);

            File outputFile = new File(outputDirectory,
                                       templateClassName.replace('.', File.separatorChar) + ".java");
            Reader input = null;
            PrintWriter output = null;
            try {
                input = new FileReader(template);
                getLog().debug(String.valueOf(outputFile.getParentFile().mkdirs()));
                output = new PrintWriter(new FileWriter(outputFile));

                output.println("package " + templatePackage + ";");
                output.println();
                output.println("public class " + templateName + " implements com.tbodt.jswerve.Template {");
                output.println("    @Override");
                output.println("    public String render() {");
                output.write(Jtl.generateCode(input));
                output.println("    }");
                output.println("}");
            } catch (IOException ioe) {
                throw new MojoExecutionException("IOException!", ioe);
            } finally {
                if (input != null)
                    try {
                        input.close();
                    } catch (IOException ex) {
                        getLog().error(ex); // nothing else can be done
                    }
                if (output != null)
                    output.close();
            }
        }

        // notify maven of the new output directory
        project.addCompileSourceRoot(outputDirectory.getPath());
    }
}
