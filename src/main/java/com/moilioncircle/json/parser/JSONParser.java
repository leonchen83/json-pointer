package com.moilioncircle.json.parser;

import java.io.*;
import java.math.BigDecimal;
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
 * @author leon on 15-11-11
 */
public class JSONParser {

    private char curr;

    private final StringBuilder builder = new StringBuilder();

    private final boolean isOrdered;

    private final BufferedReader reader;

    public JSONParser(InputStream stream, Charset encoding, boolean isOrdered) {
        this(new InputStreamReader(stream, encoding), isOrdered);
    }

    public JSONParser(Reader reader, boolean isOrdered) {
        this.reader = new BufferedReader(reader);
        this.isOrdered = isOrdered;
    }

    public JSONType parse() throws IOException, JSONParserException {
        JSONType object;
        curr = next();
        switch (curr) {
            case Constant.LBRACE:
                next();
                object = parseObject();
                break;
            case Constant.LBRACKET:
                next();
                object = parseArray();
                break;
            default:
                throw new JSONParserException("Expected '{','[' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
        }
        accept(Constant.EOF);
        return object;
    }

    public JSONObject parseObject() throws IOException, JSONParserException {
        JSONObject object = new JSONObject(isOrdered);
        switch (curr) {
            case Constant.RBRACE:
                next();
                return object;
            case Constant.QUOTE:
                do {
                    String key = parseString();
                    accept(Constant.COLON);
                    Object value = parseValue();
                    object.put(key, value);
                } while (nextIfAccept(Constant.COMMA));
                accept(Constant.RBRACE);
                return object;
            default:
                throw new JSONParserException("Expected '}','\"' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    public JSONArray parseArray() throws IOException, JSONParserException {
        JSONArray array = new JSONArray();
        switch (curr) {
            case Constant.RBRACKET:
                next();
                return array;
            default:
                do {
                    Object value = parseValue();
                    array.add(value);
                } while (nextIfAccept(Constant.COMMA));
                accept(Constant.RBRACKET);
                return array;
        }
    }

    private Object parseValue() throws IOException, JSONParserException {
        switch (curr) {
            case Constant.QUOTE:
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
            case Constant.LBRACE:
                next();
                return parseObject();
            case Constant.LBRACKET:
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
            default:
                throw new JSONParserException("Expected '{','[','t','f','n','\"','-','0'~'9' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    private Object parseNumber() throws IOException, JSONParserException {
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
                throw new JSONParserException("Expected '0'~'9' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
        }
        switch (curr) {
            case '.':
                builder.append('.');
                next();
                switch (curr) {
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
                        while (next() >= '0' && curr <= '9') {
                            builder.append(curr);
                        }
                        break;
                    default:
                        throw new JSONParserException("Expected '0'~'9' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));

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
                        next();
                        switch (curr) {
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
                                throw new JSONParserException("Expected '0'~'9' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));

                        }
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
                        throw new JSONParserException("Expected '+','-','0'~'9' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
                }
                while (next() >= '0' && curr <= '9') {
                    builder.append(curr);
                }
                break;
            default:
        }
        return new BigDecimal(builder.toString());
    }

    private String parseString() throws IOException, JSONParserException {
        builder.setLength(0);
        loop:
        while (true) {
            next0();
            switch (curr) {
                case Constant.QUOTE:
                    next();
                    return builder.toString();
                case '\\':
                    next0();
                    switch (curr) {
                        case Constant.QUOTE:
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
                            int s = Integer.valueOf(new String(new char[]{next0(), next0(), next0(), next0()}), 16);
                            builder.append((char) s);
                            continue loop;
                        default:
                            throw new JSONParserException("Expected '\\','b','f','F','n','r','t','/','u' but "+(curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
                    }
                case Constant.EOF:
                case '\n':
                case '\t':
                case '\r':
                case '\f':
                case '\b':
                    throw new JSONParserException("Un-closed String");
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
                case Constant.EOF:
                    curr = Constant.EOF;
                    break loop;
                default:
                    curr = c;
                    break loop;
            }
        }
        return curr;
    }

    private char next0() throws IOException {
        curr = (char) reader.read();
        return curr;
    }

    private void accept(char c) throws IOException, JSONParserException {
        if (c == curr) {
            next();
        } else {
            throw new JSONParserException("Expected '" + c + "' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
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

    public static void main(String[] args) throws IOException, JSONParserException {
        try (InputStream stream = new ByteArrayInputStream("[\"  \"]".getBytes())) {
            JSONParser parser = new JSONParser(stream, Charset.defaultCharset(), true);
            System.out.println(parser.parse());
        }

    }

}
