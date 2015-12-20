package com.moilioncircle.json.parser;

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
public class Constant {

    public static final char LBRACE = '{';

    public static final char RBRACE = '}';

    public static final char COLON = ':';

    public static final char COMMA = ',';

    public static final char LBRACKET = '[';

    public static final char RBRACKET = ']';

    public static final char QUOTE = '"';

    public static final char EOF = '\uFFFF';

    public static final int[] ARY = new int[65536];

    static {
        ARY[34] = 1;
        ARY[92] = 2;
        ARY[65535] = 3;
        ARY[10] = 4;
        ARY[9] = 5;
        ARY[13] = 6;
        ARY[12] = 7;
        ARY[8] = 8;
    }
}
