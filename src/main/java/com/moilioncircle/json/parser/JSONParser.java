package com.moilioncircle.json.parser;

import com.moilioncircle.json.parser.input.ParserInput;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;

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
public class JSONParser implements Closeable {
    private char curr;

    private final StringBuilder builder = new StringBuilder(40);

    private final boolean isOrdered;

    //??
    private final ParserInput input;

    public JSONParser(ParserInput input, boolean isOrdered) {
        this.input = input;
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
            case Constant.QUOTE:
                do {
                    String key = parseString();
                    accept(Constant.COLON);
                    object.put(key, parseValue());
                } while (nextIfAccept(Constant.COMMA));
                accept(Constant.RBRACE);
                return object;
            case Constant.RBRACE:
                next();
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
                    array.add(parseValue());
                } while (nextIfAccept(Constant.COMMA));
                accept(Constant.RBRACKET);
                return array;
        }
    }

    private Object parseValue() throws IOException, JSONParserException {
        switch (curr) {
            case Constant.LBRACE:
                next();
                return parseObject();
            case Constant.LBRACKET:
                next();
                return parseArray();
            case Constant.QUOTE:
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
                throw new JSONParserException("Expected '{','[','t','f','n','\"','-','0'~'9' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    private Object parseNumber() throws IOException, JSONParserException {
        builder.setLength(0);
        switch (curr) {
            case '-':
                builder.append('-');
                next0();
                break;
            default:
        }
        switch (curr) {
            case '0':
                builder.append('0');
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
                builder.append(curr);
                while (next0() >= '0' && curr <= '9') {
                    builder.append(curr);
                }
                break;
            default:
                throw new JSONParserException("Expected '0'~'9' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
        }
        switch (curr) {
            case '.':
                builder.append('.');
                next0();
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
                        while (next0() >= '0' && curr <= '9') {
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
                next0();
                switch (curr) {
                    case '+':
                    case '-':
                        builder.append(curr);
                        next0();
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
                while (next0() >= '0' && curr <= '9') {
                    builder.append(curr);
                }
                break;
            default:
        }
        while (true) {
            if (((1L << curr) & ((curr - 64) >> 31) & 0x100002600L) != 0L) {
                curr = input.read();
                continue;
            } else {
                return new BigDecimal(builder.toString());
            }
        }
    }

    private String parseString() throws IOException, JSONParserException {
        builder.setLength(0);
        loop:
        while (true) {
            next0();
            if (((1L << curr) & ((31 - curr) >> 31) & 0x7ffffffbefffffffL) != 0L) {
                builder.append(curr);
                continue loop;
            }
            switch (curr) {
                //bloom filter?
                //low cache hint
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
                            int s = Integer.parseInt(new String(new char[]{next0(), next0(), next0(), next0()}), 16);
                            builder.append((char) s);
                            continue loop;
                        default:
                            throw new JSONParserException("Expected '\\','b','f','F','n','r','t','/','u' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
                    }
                case Constant.EOF:
                case '\n':
                case '\t':
                case '\r':
                case '\f':
                case '\b':
                    throw new JSONParserException("Un-closed String");
//                default:
//                    builder.append(curr);
//                    continue loop;
            }
        }
    }

    private char next() throws IOException {
        while (true) {
            curr = input.read();
            if (((1L << curr) & ((curr - 64) >> 31) & 0x100002600L) == 0L) {
                return curr;
            }
        }
    }

    private char next0() throws IOException {
        return curr = input.read();
    }

    private void accept(char c) throws IOException, JSONParserException {
        if (c == curr) {
            next();
        } else {
            throw new JSONParserException("Expected '" + c + "' but " + (curr == Constant.EOF ? "EOF" : "'" + curr + "'"));
        }
    }

    private void accept0(char c) throws IOException, JSONParserException {
        if (c == curr) {
            next0();
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

    @Override
    public void close() throws IOException {
        if (input != null) {
            input.close();
        }
    }
}
