package com.moilioncircle.jsonpath;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * @author leon on 15-11-13
 */
public class JSONPointer {
    private final JSONType type;


    public JSONPointer(JSONType type) {
        this.type = type;
    }

    public <T> T read(String path) {
        List<String> paths = parsePath(path);
        Object temp = type;
        if (paths != null && paths.size() == 1 && paths.get(0).equals("")) {
            return (T) temp;
        }
        for (String subPath : paths) {
            temp = apply(temp, subPath);
        }
        return (T) temp;
    }

    private Object apply(Object temp, String path) {
        if (temp == null) {
            throw new NotFoundException("path:'" + path + "'");
        } else if (temp instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) temp;

            int index = parseIndex(path);
            return jsonArray.get(index);
        } else if (temp instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) temp;
            return jsonObject.get(path);
        } else {
            throw new NotFoundException("path:'" + path + "'");
        }
    }

    private List<String> parsePath(String path) {

        List<String> list = new ArrayList<>();
        char[] chs = path.toCharArray();
        if (path.equals("")) {
            return Arrays.asList("");
        } else if (!path.startsWith("/")) {
            throw new NotFoundException("should start with '/'");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < chs.length; i++) {
            switch (chs[i]) {
                case '/':
                    list.add(builder.toString());
                    builder.setLength(0);
                    break;
                default:
                    builder.append(chs[i]);
            }
        }
        list.add(builder.toString());
        List<String> rList = new ArrayList<>();
        for (String str : list) {
            rList.add(parseString(str));
        }
        return rList;
    }

    private int parseIndex(String path) {
        char[] chs = path.toCharArray();
        if (chs.length == 0 || (chs.length > 1 && chs[0] == '0')) {
            throw new NotFoundException("path:'" + path + "'");
        } else {
            try {
                return Integer.parseInt(path);
            } catch (NumberFormatException e) {
                throw new NotFoundException("path:'" + path + "'");
            }
        }
    }

    public String parseString(String str) {
        StringBuilder builder = new StringBuilder();
        char[] chs = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            switch (chs[i]) {
                case '~':
                    int j = i;
                    j++;
                    if (j < chs.length) {
                        switch (chs[j]) {
                            case '0':
                                builder.append('~');
                                break;
                            case '1':
                                builder.append('/');
                                break;
                            default:
                                builder.append('~');
                                builder.append(chs[j]);
                        }
                        i++;
                    } else {
                        builder.append('~');
                    }
                    break;
                default:
                    builder.append(chs[i]);
            }
        }
        return builder.toString();
    }

    public static String quote(String str) {
        StringBuilder builder = new StringBuilder();
        char[] chs = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            switch (chs[i]) {
                case '~':
                    builder.append('~');
                    builder.append('0');
                    break;
                case '/':
                    builder.append('~');
                    builder.append('1');
                    break;
                default:
                    builder.append(chs[i]);
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) throws IOException {
        try (StringReader reader = new StringReader("[{\"http://test.com/测试\":true},[\"测试中文\"],[null], [ 0 , 1.23, 4,{\"abc\":\"bcd\" , \"123\":345} ]]")) {
            JSONParser parser = new JSONParser(reader, true);
            JSONPointer pointer = new JSONPointer(parser.parse());
            Boolean bool = pointer.read("/0/" + quote("http://test.com/测试"));
            System.out.println(bool);
            bool = pointer.read("/0/http:~1~1test.com~1测试");
            System.out.println(bool);
        }

    }
}
