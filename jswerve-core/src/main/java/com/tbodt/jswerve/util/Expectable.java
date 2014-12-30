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
package com.tbodt.jswerve.util;

import java.io.*;
import java.net.Socket;

/**
 *
 * @author Theodore Dubois
 */
public final class Expectable {
    private final BufferedReader in;
    private final Writer out;

    public Expectable(Reader in, Writer out) {
        this.in = new BufferedReader(in);
        this.out = out;
    }

    public Expectable(InputStream in, OutputStream out) {
        this(new InputStreamReader(in), new OutputStreamWriter(out));
    }

    public Expectable(Socket s) throws IOException {
        this(s.getInputStream(), s.getOutputStream());
    }

    public void expect(String text) throws IOException {
        String line = in.readLine();
        if (line == null)
            throw new UnexpectedDataException("\"" + text + "\" is not in EOF");
        if (!line.contains(text))
            throw new UnexpectedDataException("\"" + text + "\" is not in \"" + line + "\"");
    }

    public void expectExact(String text) throws IOException {
        String line = in.readLine();
        if (line == null)
            throw new UnexpectedDataException("\"" + text + "\" is not EOF");
        if (!line.equals(text))
            throw new UnexpectedDataException("\"" + text + "\" is not \"" + line + "\"");
    }

    public void write(String text) throws IOException {
        out.write(text);
        out.flush();
    }

    public void writeln(String line) throws IOException {
        out.write(line + "\n");
        out.flush();
    }
    
    public void writeln() throws IOException {
        out.write("\n");
        out.flush();
    }
    

    public void close() throws IOException {
        in.close();
        out.close();
    }
}
