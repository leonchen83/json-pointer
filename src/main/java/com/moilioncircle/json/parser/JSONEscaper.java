/*
 * Copyright 2016-2017 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.json.parser;

import java.io.OutputStream;

/**
 * @author Baoyi Chen
 */
public class JSONEscaper {
    
    public final static int[] ESCAPES;

    static {
        int[] table = new int[128];
        for (int i = 0; i < 32; ++i) {
            table[i] = -1;
        }
        table['"'] = '"';
        table['/'] = '/';
        table['\\'] = '\\';
        table[0x08] = 'b';
        table[0x09] = 't';
        table[0x0C] = 'f';
        table[0x0A] = 'n';
        table[0x0D] = 'r';
        ESCAPES = table;
    }

    public static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public final static int SURR1_FIRST = 0xD800;
    public final static int SURR2_FIRST = 0xDC00;
    public final static int SURR1_LAST = 0xDBFF;
    public final static int SURR2_LAST = 0xDFFF;

    public static String escape(String value) {
        return escape(value, 0, value.length());
    }

    public static String escape(String value, int off, int len) {
        StringBuilder builder = new StringBuilder();
        while (off < len) {
            int ch = value.charAt(off++);
            if (ch <= 0x7F) {
                if (ESCAPES[ch] == 0) {
                    builder.append((byte) ch);
                    continue;
                }
                int escape = ESCAPES[ch];
                if (escape > 0) {
                    builder.append('\\');
                    builder.append((byte) escape);
                } else {
                    // ctrl-char, 6-byte escape...
                    writeGenericEscape(ch, builder);
                }
                continue;
            }
            if (ch <= 0x7FF) {
                builder.append((byte) (0xc0 | (ch >> 6)));
                builder.append((byte) (0x80 | (ch & 0x3f)));
            } else {
                writeMultiByteChar(ch, builder);
            }
        }
        return builder.toString();
    }

    private static void writeGenericEscape(int ch, StringBuilder builder) {
        builder.append((byte) '\\');
        builder.append((byte) 'u');
        if (ch > 0xFF) {
            int hi = (ch >> 8) & 0xFF;
            builder.append((byte) HEX[hi >> 4]);
            builder.append((byte) HEX[hi & 0xF]);
            ch &= 0xFF;
        } else {
            builder.append((byte) '0');
            builder.append((byte) '0');
        }
        builder.append((byte) HEX[ch >> 4]);
        builder.append((byte) HEX[ch & 0xF]);
    }

    private static void writeMultiByteChar(int ch, StringBuilder builder) {
        if (ch >= SURR1_FIRST && ch <= SURR2_LAST) {
            builder.append((byte) '\\');
            builder.append((byte) 'u');
            builder.append((byte) HEX[(ch >> 12) & 0xF]);
            builder.append((byte) HEX[(ch >> 8) & 0xF]);
            builder.append((byte) HEX[(ch >> 4) & 0xF]);
            builder.append((byte) HEX[ch & 0xF]);
        } else {
            builder.append((byte) (0xe0 | (ch >> 12)));
            builder.append((byte) (0x80 | ((ch >> 6) & 0x3f)));
            builder.append((byte) (0x80 | (ch & 0x3f)));
        }
    }
}
