package com.moilioncircle.json.parser.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Copyright leon
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
 *
 * @author leon on 15-12-19
 */
public class InputStreamParserInput implements ParserInput {

    private final InputStream stream;
    private final Charset charset;

    public InputStreamParserInput(InputStream stream, Charset charset) {
        this.stream = stream;
        this.charset = charset;
    }

    @Override
    public char read() {
        return 0;
    }

    @Override
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }
}
