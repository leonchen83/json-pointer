package com.moilioncircle.json.parser;

import com.moilioncircle.json.parser.input.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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
        private ParserInput input;
        private boolean isOrdered;

        public ParserBuilder reader(Reader reader) throws IOException {
            this.input = new ReaderParserInput(reader);
            return this;
        }

        public ParserBuilder stream(InputStream stream, Charset charset) throws IOException {
            this.input = new InputStreamParserInput(stream, charset);
            return this;
        }

        public ParserBuilder stream(InputStream stream) throws IOException {
            return stream(stream, Charset.defaultCharset());
        }

        public ParserBuilder bytes(byte[] bytes, Charset charset) throws IOException {
            this.input = new ByteArrayParserInput(bytes, charset);
            return this;
        }

        public ParserBuilder bytes(byte[] bytes) throws IOException {
            return bytes(bytes, Charset.defaultCharset());
        }

        public ParserBuilder string(String jsonStr) {
            this.input = new StringParserInput(jsonStr);
            return this;
        }

        public ParserBuilder chars(char[] chars) {
            this.input = new CharArrayParserInput(chars);
            return this;
        }

        public ParserBuilder order(boolean isOrdered) {
            this.isOrdered = isOrdered;
            return this;
        }

        public JSONParser build() {
            return new JSONParser(input, isOrdered);
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

    public static JSONType readTree(char[] chars) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().chars(chars).build()) {
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

    public static JSONType readTree(char[] chars, boolean isOrdered) throws IOException, JSONParserException {
        try (JSONParser parser = new ParserBuilder().chars(chars).order(isOrdered).build()) {
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

    public static String writeAsString(JSONType json){
        if(json instanceof JSONArray){
            return writeAsString((JSONArray) json);
        }else if(json instanceof JSONObject){
            return writeAsString((JSONObject) json);
        }
        throw new UnsupportedOperationException();
    }

    private static String writeAsString(JSONObject map){
        if(map == null || map.size() == 0){
            return "{}";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            Object value = map.get(key);
            builder.append("\""+escape(key)+"\":");
            if(value == null){
                builder.append("null");
            }else if(value instanceof Map){
                builder.append(writeAsString((JSONObject)value));
            }else if(value instanceof Collection){
                builder.append(writeAsString((JSONArray) value));
            }else if(value instanceof String){
                builder.append("\""+escape((String)value)+"\"");
            }else {
                builder.append(value);
            }
            if(it.hasNext()){
                builder.append(',');
            }
        }
        builder.append('}');
        return builder.toString();
    }

    private static String writeAsString(JSONArray list){
        if(list == null || list.size() == 0){
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        Iterator<Object> it = list.iterator();
        while(it.hasNext()){
            Object value = it.next();
            if(value == null){
                builder.append("null");
            }else if(value instanceof Map){
                builder.append(writeAsString((JSONObject)value));
            }else if(value instanceof Collection){
                builder.append(writeAsString((JSONArray) value));
            }else if(value instanceof String){
                builder.append("\""+escape((String)value)+"\"");
            }else {
                builder.append(value);
            }
            if(it.hasNext()){
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public static String escape(String str){
        StringBuilder builder = new StringBuilder();
        char[] ary = str.toCharArray();
        for(char c : ary){
            switch (c){
                case '"':
                    builder.append('\\');
                    builder.append('"');
                    break;
                case '\n':
                    builder.append('\\');
                    builder.append('n');
                    break;
                case '\\':
                    builder.append('\\');
                    builder.append('\\');
                    break;
                case '/':
                    builder.append('\\');
                    builder.append('/');
                    break;
                case '\b':
                    builder.append('\\');
                    builder.append('b');
                    break;
                case '\f':
                    builder.append('\\');
                    builder.append('f');
                    break;
                case '\r':
                    builder.append('\\');
                    builder.append('r');
                    break;
                case '\t':
                    builder.append('\\');
                    builder.append('t');
                    break;
                default:
                    builder.append(c);
            }
        }
        return builder.toString();
    }

}
