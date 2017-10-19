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
package com.moilioncircle.json.parser;

import com.moilioncircle.json.parser.input.ParserInput;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;

import static com.moilioncircle.json.parser.Constant.COLON;
import static com.moilioncircle.json.parser.Constant.COMMA;
import static com.moilioncircle.json.parser.Constant.EOF;
import static com.moilioncircle.json.parser.Constant.LBRACE;
import static com.moilioncircle.json.parser.Constant.LBRACKET;
import static com.moilioncircle.json.parser.Constant.QUOTE;
import static com.moilioncircle.json.parser.Constant.RBRACE;
import static com.moilioncircle.json.parser.Constant.RBRACKET;

/**
 * @author Leon Chen
 */
public class JSONParser implements Closeable {

    private char curr;

    private final boolean isOrdered;

    private final ParserInput input;

    private static final int CHAR_BUF_SIZE = 64;

    private char[] cbuf = new char[CHAR_BUF_SIZE];

    private int index;

    public JSONParser(ParserInput input, boolean isOrdered) {
        this.input = input;
        this.isOrdered = isOrdered;
    }

    public JSONType parse() throws IOException {
        JSONType object;
        curr = next();
        switch (curr) {
            case LBRACE:
                next();
                object = parseObject();
                break;
            case LBRACKET:
                next();
                object = parseArray();
                break;
            default:
                throw new JSONParserException("Expected '{','[' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));
        }
        accept(EOF);
        return object;
    }

    private JSONObject parseObject() throws IOException {
        JSONObject object = new JSONObject(isOrdered);
        switch (curr) {
            case QUOTE:
                do {
                    String key = parseString();
                    accept(COLON);
                    object.put(key, parseValue());
                } while (nextIfAccept(COMMA));
                accept(RBRACE);
                return object;
            case RBRACE:
                next();
                return object;
            default:
                throw new JSONParserException("Expected '}','\"' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    private JSONArray parseArray() throws IOException {
        JSONArray array = new JSONArray();
        switch (curr) {
            case RBRACKET:
                next();
                return array;
            default:
                do {
                    array.add(parseValue());
                } while (nextIfAccept(COMMA));
                accept(RBRACKET);
                return array;
        }
    }

    private Object parseValue() throws IOException {
        switch (curr) {
            case LBRACE:
                next();
                return parseObject();
            case LBRACKET:
                next();
                return parseArray();
            case QUOTE:
                return parseString();
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
            case 'n':
                next0();
                accept0('u');
                accept0('l');
                accept('l');
                return null;
            case 't':
                next0();
                accept0('r');
                accept0('u');
                accept('e');
                return true;
            case 'f':
                next0();
                accept0('a');
                accept0('l');
                accept0('s');
                accept('e');
                return false;
            default:
                throw new JSONParserException("Expected '{','[','t','f','n','\"','-','0'~'9' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    private Object parseNumber() throws IOException {
        index = 0;
        switch (curr) {
            case '-':
                append('-');
                next0();
                break;
            default:
        }
        switch (curr) {
            case '0':
                append('0');
                next0();
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
                append(curr);
                while (next0() >= '0' && curr <= '9') {
                    append(curr);
                }
                break;
            default:
                throw new JSONParserException("Expected '0'~'9' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));
        }
        switch (curr) {
            case '.':
                append('.');
                switch (next0()) {
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
                        append(curr);
                        while (next0() >= '0' && curr <= '9') {
                            append(curr);
                        }
                        break;
                    default:
                        throw new JSONParserException("Expected '0'~'9' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));

                }
                break;
            default:
        }
        switch (curr) {
            case 'e':
            case 'E':
                append(curr);
                switch (next0()) {
                    case '+':
                    case '-':
                        append(curr);
                        switch (next0()) {
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
                                append(curr);
                                break;
                            default:
                                throw new JSONParserException("Expected '0'~'9' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));

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
                        append(curr);
                        break;
                    default:
                        throw new JSONParserException("Expected '+','-','0'~'9' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));
                }
                while (next0() >= '0' && curr <= '9') {
                    append(curr);
                }
                break;
            default:
        }
        for (; ; ) {
            switch (curr) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    next0();
                    continue;
                default:
                    return new BigDecimal(new String(cbuf, 0, index));
            }
        }
    }

    private String parseString() throws IOException {
        index = 0;
        for (; ; ) {
            switch (next0()) {
                case Constant.QUOTE:
                    next();
                    return new String(cbuf, 0, index);
                case '\\':
                    switch (next0()) {
                        case Constant.QUOTE:
                            append('\"');
                            continue;
                        case '\\':
                            append('\\');
                            continue;
                        case 'n':
                            append('\n');
                            continue;
                        case 't':
                            append('\t');
                            continue;
                        case 'r':
                            append('\r');
                            continue;
                        case '/':
                            append('/');
                            continue;
                        case 'u':
                            int s = Integer.parseInt(new String(new char[]{next0(), next0(), next0(), next0()}), 16);
                            append((char) s);
                            continue;
                        case 'b':
                            append('\b');
                            continue;
                        case 'f':
                            append('\f');
                            continue;
                        default:
                            throw new JSONParserException("Expected '\\','b','f','n','r','t','/','u' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
                    }
                case Constant.EOF:
                case '\n':
                case '\t':
                case '\r':
                case '\f':
                case '\b':
                    throw new JSONParserException("Un-closed String");
                default:
                    append(curr);
                    continue;
            }
        }
    }

    private char next() throws IOException {
        for (; ; ) {
            switch (next0()) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    continue;
                default:
                    return curr;
            }
        }
    }

    private char next0() throws IOException {
        return curr = input.read();
    }

    private void accept(char c) throws IOException {
        if (c == curr) {
            next();
        } else {
            throw new JSONParserException("Expected '" + c + "' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    private void accept0(char c) throws IOException {
        if (c == curr) {
            next0();
        } else {
            throw new JSONParserException("Expected '" + c + "' but " + (curr == EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    private boolean nextIfAccept(char c) throws IOException {
        if (c == curr) {
            next();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        if (input != null) {
            input.close();
        }
    }

    public void append(char c) {
        if (index == cbuf.length) {
            char[] newbuf = new char[cbuf.length * 2];
            System.arraycopy(cbuf, 0, newbuf, 0, cbuf.length);
            cbuf = newbuf;
        }
        cbuf[index++] = c;
    }
}
