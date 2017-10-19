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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author Leon Chen
 */
public class InputStreamParserInput implements ParserInput {

    private final ReaderParserInput input;

    public InputStreamParserInput(InputStream stream, Charset charset) throws IOException {
        this.input = new ReaderParserInput(new InputStreamReader(stream, charset));
    }

    @Override
    public char read() throws IOException {
        return input.read();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
