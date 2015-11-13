package com.moilioncircle.jsonpath;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import static com.moilioncircle.jsonpath.Constant.*;

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
 * @author leon on 15-11-11
 */
public class JSONParser {
    private char curr;
    private final StringBuilder builder = new StringBuilder();
    private final boolean isOrdered;
    private final BufferedReader reader;

    public JSONParser(InputStream stream, Charset encoding, boolean isOrdered) throws IOException {
        this(new InputStreamReader(stream, encoding), isOrdered);
    }

    public JSONParser(Reader reader, boolean isOrdered) throws IOException {
        this.reader = new BufferedReader(reader);
        this.isOrdered = isOrdered;
        curr = next();
    }

    public JSONType parse() throws IOException {
        JSONType object;
        switch (curr) {
            case LBRACE:
                accept(LBRACE);
                object = parseObject();
                accept(EOF);
                break;
            case LBRACKET:
                accept(LBRACKET);
                object = parseArray();
                accept(EOF);
                break;
            default:
                throw new RuntimeException();
        }
        return object;
    }

    public JSONObject parseObject() throws IOException {
        JSONObject object = new JSONObject(isOrdered);
        switch (curr) {
            case RBRACE:
                accept(RBRACE);
                return object;
            case QUOTE:
                do {
                    String key = parseString();
                    accept(COLON);
                    Object value = parseValue();
                    object.put(key, value);
                } while (nextIfAccept(COMMA));
                accept(RBRACE);
                return object;
            default:
                throw new RuntimeException();
        }
    }

    public JSONArray parseArray() throws IOException {
        JSONArray array = new JSONArray();
        switch (curr) {
            case RBRACKET:
                accept(RBRACKET);
                return array;
            default:
                do {
                    Object value = parseValue();
                    array.add(value);
                } while (nextIfAccept(COMMA));
                accept(RBRACKET);
                return array;
        }
    }

    private Object parseValue() throws IOException {
        switch (curr) {
            case QUOTE:
                return parseString();
            case 't':
                accept('t');
                accept('r');
                accept('u');
                accept('e');
                return true;
            case 'f':
                accept('f');
                accept('a');
                accept('l');
                accept('s');
                accept('e');
                return false;
            case 'n':
                accept('n');
                accept('u');
                accept('l');
                accept('l');
                return null;
            case LBRACE:
                next();
                return parseObject();
            case LBRACKET:
                next();
                return parseArray();
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
                return parseNumber();
        }
        throw new RuntimeException();
    }

    private Object parseNumber() throws IOException {
        builder.setLength(0);
        switch (curr) {
            case '-':
                builder.append('-');
                next();
                break;
            default:
        }
        switch (curr) {
            case '0':
                builder.append('0');
                next();
                break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                builder.append(curr);
                while (next() >= '0' && curr <= '9') {
                    builder.append(curr);
                }
                break;
            default:
        }
        switch (curr) {
            case '.':
                builder.append('.');
                if (next() >= '0' && curr <= '9') {
                    builder.append(curr);
                } else {
                    throw new RuntimeException();
                }
                while (next() >= '0' && curr <= '9') {
                    builder.append(curr);
                }
                break;
            default:

        }
        switch (curr) {
            case 'e':
            case 'E':
                builder.append(curr);
                next();
                switch (curr) {
                    case '+':
                    case '-':
                        builder.append(curr);
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        builder.append(curr);
                        break;
                    default:
                        throw new RuntimeException();
                }
                while (next() >= '0' && curr <= '9') {
                    builder.append(curr);
                }
                break;
            default:
        }
        return new BigDecimal(builder.toString());
    }

    private String parseString() throws IOException {
        builder.setLength(0);
        loop:
        while (true) {
            next();
            switch (curr) {
                case QUOTE:
                    next();
                    return builder.toString();
                case '\\':
                    next();
                    switch (curr) {
                        case QUOTE:
                            builder.append('\"');
                            continue loop;
                        case '\\':
                            builder.append('\\');
                            continue loop;
                        case 'b':
                            builder.append('\b');
                            continue loop;
                        case 'f':
                        case 'F':
                            builder.append('\f');
                            continue loop;
                        case 'n':
                            builder.append('\n');
                            continue loop;
                        case 'r':
                            builder.append('\r');
                            continue loop;
                        case 't':
                            builder.append('\t');
                            continue loop;
                        case '/':
                            builder.append('/');
                            continue loop;
                        case 'u':
                            int s = Integer.valueOf(new String(new char[]{next(), next(), next(), next()}), 16);
                            builder.append((char) s);
                            continue loop;
                        default:
                            builder.append('\\');
                            builder.append(curr);
                            continue loop;
                    }
                case EOF:
                    throw new RuntimeException();
                default:
                    builder.append(curr);
                    continue loop;

            }
        }
    }

    private char next() throws IOException {
        loop:
        while (true) {
            char c = (char) reader.read();
            switch (c) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    continue loop;
                case EOF:
                    curr = EOF;
                    break loop;
                default:
                    curr = c;
                    break loop;
            }
        }
        return curr;
    }

    private void accept(char c) throws IOException {
        if (c == curr) {
            next();
        } else {
            throw new RuntimeException();
        }
    }

    private boolean nextIfAccept(char c) throws IOException {
        if (c == curr) {
            next();
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        try (InputStream stream = new ByteArrayInputStream("[true,[\"测试中文\"],[null], [ 0 , 1.23, 4,{\"abc\":\"bcd\" , \"123\":345} ]]".getBytes())) {
            JSONParser parser = new JSONParser(stream, Charset.defaultCharset(), true);
            System.out.println(parser.parse());
        }

    }

}
