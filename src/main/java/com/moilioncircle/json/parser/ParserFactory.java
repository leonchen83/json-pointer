package com.moilioncircle.json.parser;

import java.io.*;
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
 * @author leon on 15-12-18
 */
public class ParserFactory {
    private ParserFactory() {

    }

    private static class ParserBuilder {
        private Reader reader;
        private boolean isOrdered;

        public ParserBuilder reader(Reader reader) {
            this.reader = reader;
            return this;
        }

        public ParserBuilder stream(InputStream stream, Charset charset) {
            this.reader = new InputStreamReader(stream, charset);
            return this;
        }

        public ParserBuilder stream(InputStream stream) {
            return stream(stream, Charset.defaultCharset());
        }

        public ParserBuilder bytes(byte[] bytes, Charset charset) {
            return stream(new ByteArrayInputStream(bytes), charset);
        }

        public ParserBuilder bytes(byte[] bytes) {
            return stream(new ByteArrayInputStream(bytes));
        }

        public ParserBuilder string(String jsonStr) {
            return reader(new StringReader(jsonStr));
        }

        public ParserBuilder order(boolean isOrdered) {
            this.isOrdered = isOrdered;
            return this;
        }

        public JSONParser build() {
            return new JSONParser(reader, isOrdered);
        }
    }

    public static JSONType readTree(String jsonStr) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().string(jsonStr).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(Reader reader) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().reader(reader).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(InputStream stream) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().stream(stream).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(InputStream stream, String charset) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().stream(stream, Charset.forName(charset)).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(byte[] bytes, String charset) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().bytes(bytes, Charset.forName(charset)).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(byte[] bytes) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().bytes(bytes).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(String jsonStr, boolean isOrdered) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().string(jsonStr).order(isOrdered).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(Reader reader, boolean isOrdered) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().reader(reader).order(isOrdered).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(InputStream stream, boolean isOrdered) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().stream(stream).order(isOrdered).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(InputStream stream, String charset, boolean isOrdered) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().stream(stream, Charset.forName(charset)).order(isOrdered).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(byte[] bytes, String charset, boolean isOrdered) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().bytes(bytes, Charset.forName(charset)).order(isOrdered).build()) {
            return parser.parse();
        }
    }

    public static JSONType readTree(byte[] bytes, boolean isOrdered) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().bytes(bytes).order(isOrdered).build()) {
            return parser.parse();
        }
    }

}
