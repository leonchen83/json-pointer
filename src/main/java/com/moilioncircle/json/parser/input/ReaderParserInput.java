/*
 * Copyright 2015-2018 Leon Chen
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moilioncircle.json.parser.input;

import com.moilioncircle.json.parser.Constant;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Leon Chen
 */
public class ReaderParserInput implements ParserInput {
    private final Reader reader;
    private final char[] cbuf;
    private int clen;
    private int index;
    public static final int BUF_SIZE = 8192;

    public ReaderParserInput(Reader reader) throws IOException {
        this.reader = reader;
        cbuf = new char[BUF_SIZE];
        clen = reader.read(cbuf);
    }

    @Override
    public char read() throws IOException {
        if (clen != -1) {
            if (index < clen) {
                return cbuf[index++];
            } else {
                index = 0;
                clen = reader.read(cbuf);
                return read();
            }
        } else {
            return Constant.EOF;
        }
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
