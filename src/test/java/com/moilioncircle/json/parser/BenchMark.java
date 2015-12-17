package com.moilioncircle.json.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
 * @author leon on 15-12-17
 */
public class BenchMark {
    public static void main(String[] args) throws IOException, JSONParserException {
        InputStream stream = BenchMark.class.getClassLoader().getResourceAsStream("canada.json");
        byte[] bytes = IOUtils.toByteArray(stream);
        IOUtils.closeQuietly(stream);
        String str = new String(bytes);
        ObjectMapper mapper = new ObjectMapper();

        long start = System.currentTimeMillis();
        //47389
//        for (int i = 0; i < 1000; i++) {
//            try (StringReader reader = new StringReader(str)) {
//                JSONParser parser = new JSONParser(reader, false);
//                parser.parse();
//            }
//        }
//        System.out.println(System.currentTimeMillis() - start);

        //73612
        for (int i = 0; i < 1000; i++) {
            try (StringReader reader = new StringReader(str)) {
                mapper.readTree(reader);
            }
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    public void testCanada() throws Exception {
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("canada.json")) {
            parse(stream);
        } catch (Exception e) {
        }
    }

    public void testCatalog() throws Exception {
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("citm_catalog.json")) {
            parse(stream);
        } catch (Exception e) {
        }
    }

    public void testTwitter() throws Exception {
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("twitter.json")) {
            parse(stream);
        } catch (Exception e) {
        }
    }

    private void parse(InputStream stream) throws IOException, JSONParserException {
        JSONParser parser = new JSONParser(stream, Charset.defaultCharset(), true);
        parser.parse();
    }
}
