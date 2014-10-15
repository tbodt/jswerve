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

import java.io.*;
import java.util.logging.*;

/**
 * Contains code to manage the logging stuff.
 *
 * @author Theodore Dubois
 */
public final class Logging {
    public static final Logger LOG = Logger.getLogger("com.tbodt.jswerve");

    public static void initialize() throws IOException {
        /*
        PrintStream out = new PrintStream(new OutputStream() {
            private StringBuilder line = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                write(new byte[] {(byte) b}, 0, 1);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                line.append(new String(b, off, len));
                if (line.toString().endsWith("\n")) {
                    LOG.info(line.toString().substring(0, line.length() - 1));
                    line = new StringBuilder();
                }
            }
        });
                */
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
        System.setIn(in);

        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);

        Handler logHandler = new FileHandler(Constants.HOME.getAbsolutePath() + File.separator + "jswerve.log");
        logHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return "[" + record.getLevel() + "] " + formatMessage(record) + "\n";
            }
        });
        logHandler.setLevel(Level.ALL);
        LOG.addHandler(logHandler);
    }

    private Logging() {
    }
}
