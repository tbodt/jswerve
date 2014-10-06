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

import java.nio.ByteBuffer;
import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
public class ByteBufferList implements Iterable<ByteBuffer> {
    private final ByteBuffer[] buffers;

    public ByteBufferList(ByteBuffer buf) {
        buffers = new ByteBuffer[] {buf.duplicate()};
    }

    public ByteBufferList(ByteBuffer... bufs) {
        buffers = new ByteBuffer[bufs.length];
        for (int i = 0; i < bufs.length; i++) {
            buffers[i] = bufs[i].duplicate();
        }
    }

    public ByteBuffer[] getBuffers() {
        return buffers.clone();
    }

    public Iterator<ByteBuffer> iterator() {
        return Arrays.asList(buffers).iterator();
    }
}
