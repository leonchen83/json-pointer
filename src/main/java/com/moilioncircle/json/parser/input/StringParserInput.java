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

/**
 * @author Leon Chen
 */
public class StringParserInput implements ParserInput {

    private final String jsonStr;

    private final int length;

    private int index;


    public StringParserInput(String jsonStr) {
        this.jsonStr = jsonStr;
        this.length = jsonStr.length();
    }

    @Override
    public char read() {
        if (index < length) {
            return jsonStr.charAt(index++);
        }
        return Constant.EOF;
    }

    @Override
    public void close() throws IOException {

    }
}
