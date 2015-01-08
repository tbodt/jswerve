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

import com.tbodt.jswerve.util.UrlUtils;
import java.nio.charset.Charset;
import java.util.*;

/**
 *
 * @author Theodore Dubois
 */
public final class Content {
    private final byte[] data;
    private final String mimeType;
    private final Charset encoding;

    public static Content EMPTY = new Content(new byte[0], null);

    public Content(byte[] data, String mimeType) {
        this.data = data;
        if (mimeType == null) {
            this.mimeType = null;
            encoding = null;
        } else if (mimeType.contains(";")) {
            this.mimeType = mimeType.substring(0, mimeType.indexOf(";")).toLowerCase();
            Map<String, String> mimeParameters = decodeMimeParameters(mimeType.substring(mimeType.indexOf(";") + 1));
            encoding = Charset.forName(mimeParameters.get("charset"));
        } else {
            this.mimeType = mimeType;
            encoding = Charset.forName("ISO-8859-1"); // relatively well supported
        }
    }

    private static Map<String, String> decodeMimeParameters(String parameters) {
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer tok = new StringTokenizer(parameters, ";");
        while (tok.hasMoreTokens()) {
            String parameter = tok.nextToken().trim();
            int equalIndex = parameter.indexOf("=");
            if (equalIndex == -1)
                continue;
            String key = UrlUtils.decode(parameter.substring(0, equalIndex)).trim().toLowerCase();
            String value = UrlUtils.decode(parameter.substring(equalIndex + 1));
            if (key.equals(""))
                continue;
            if (value.startsWith("\"") && value.endsWith("\""))
                value = value.substring(1, value.length() - 1);
            map.put(key, value);
        }
        return Collections.unmodifiableMap(map);

    }

    public byte[] getData() {
        return data.clone();
    }
    
    public String getMimeType() {
        return mimeType;
    }

    public Charset getEncoding() {
        return encoding;
    }

    @Override
    public String toString() {
        if (encoding == null)
            return "";
        else
            return new String(data, encoding);
    }
}
