/*
 * Copyright 2015-2018 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.json.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon Chen
 * @see <a href="https://tools.ietf.org/html/rfc6901">RFC6901</a>
 */
public class JSONPath {

    @SuppressWarnings("unchecked")
    public static <T> T readPath(String path, JSONType json) {
        if (path != null && path.length() == 0) {
            return (T) json;
        }
        return readPath(path, parse(path), json, 0);
    }

    @SuppressWarnings("unchecked")
    private static <T> T readPath(String raw, List<String> path, Object json, int index) {
        int len = path.size();
        if (index == len) {
            return (T) json;
        }
        if (index > len) {
            throw new JSONPathException("unmatched path ['" + raw + "']");
        }
        if (json == null || !(json instanceof JSONType)) {
            throw new JSONPathException("unmatched path ['" + raw + "']");
        }
        String p = path.get(index);
        if (json instanceof JSONArray) {
            JSONArray ary = (JSONArray) json;
            try {
                int idx = Integer.parseInt(p);
                return readPath(raw, path, ary.get(idx), ++index);
            } catch (Exception e) {
                throw new JSONPathException("unmatched path ['" + raw + "'] at ['" + p + "'], expect a valid number.", e);
            }
        } else if (json instanceof JSONObject) {
            JSONObject obj = (JSONObject) json;
            if (!obj.containsKey(p)) {
                throw new JSONPathException("not found path ['" + raw + "'] at ['" + p + "']");
            }
            return readPath(raw, path, obj.get(p), ++index);
        } else {
            throw new JSONPathException("unmatched path ['" + raw + "'] at ['" + p + "']");
        }
    }

    private static List<String> parse(String path) {
        if (path == null || path.charAt(0) != '/') {
            throw new JSONPathException("invalid path ['" + path + "']");
        }
        char[] ary = path.toCharArray();
        List<String> r = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        char prev = '/';
        for (int i = 1; i < ary.length; i++) {
            char c = ary[i];
            switch (c) {
                case '~':
                    break;
                case '/':
                    if (prev == '~') {
                        builder.append('~');
                    }
                    r.add(builder.toString());
                    builder.setLength(0);
                    break;
                case '0':
                    if (prev == '~') {
                        builder.append("~");
                    } else {
                        builder.append(c);
                    }
                    break;
                case '1':
                    if (prev == '~') {
                        builder.append("/");
                    } else {
                        builder.append(c);
                    }
                    break;
                default:
                    if (prev == '~') {
                        builder.append('~');
                    }
                    builder.append(c);
                    break;
            }
            prev = c;
        }
        if (prev == '~') {
            builder.append("~");
        }
        r.add(builder.toString());
        return r;
    }
}
