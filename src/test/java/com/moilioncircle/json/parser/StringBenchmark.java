package com.moilioncircle.json.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.InputStream;

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
 * @author leon on 15-12-19
 */
public class StringBenchmark {
    public static String twitter;
    public static String citm_catalog;
    public static String canada;
    public static String twitter_trim;
    public static String pass1;
    public static String pass2;
    public static String pass3;
    public static String pass3_trim;

    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        try {
            InputStream stream = StringBenchmark.class.getClassLoader().getResourceAsStream("twitter.json");
            byte[] bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            twitter = new String(bytes);

            stream = StringBenchmark.class.getClassLoader().getResourceAsStream("citm_catalog.json");
            bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            citm_catalog = new String(bytes);

            stream = StringBenchmark.class.getClassLoader().getResourceAsStream("canada.json");
            bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            canada = new String(bytes);

            stream = StringBenchmark.class.getClassLoader().getResourceAsStream("twitter_trim.json");
            bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            twitter_trim = new String(bytes);

            stream = StringBenchmark.class.getClassLoader().getResourceAsStream("pass1.json");
            bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            pass1 = new String(bytes);

            stream = StringBenchmark.class.getClassLoader().getResourceAsStream("pass2.json");
            bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            pass2 = new String(bytes);

            stream = StringBenchmark.class.getClassLoader().getResourceAsStream("pass3.json");
            bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            pass3 = new String(bytes);

            stream = StringBenchmark.class.getClassLoader().getResourceAsStream("pass3_trim.json");
            bytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
            pass3_trim = new String(bytes);
        } catch (Exception e) {

        }
    }

    @Benchmark
    public void testTwitterTrimMyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(twitter_trim);
    }

    @Benchmark
    public void testTwitterTrimJacksonParser() throws IOException, JSONParserException {
        mapper.readTree(twitter_trim);
    }

    @Benchmark
    public void testTwitterMyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(twitter);
    }

    @Benchmark
    public void testTwitterJacksonParser() throws IOException, JSONParserException {
        mapper.readTree(twitter);
    }

    @Benchmark
    public void testCanadaMyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(canada);
    }

    @Benchmark
    public void testCanadaJacksonParser() throws IOException, JSONParserException {
        mapper.readTree(canada);
    }

    @Benchmark
    public void testCitmCatalogMyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(citm_catalog);
    }

    @Benchmark
    public void testCitmCatalogJacksonParser() throws IOException, JSONParserException {
        mapper.readTree(citm_catalog);
    }

    @Benchmark
    public void testPass1MyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(pass1);
    }

    @Benchmark
    public void testPass1JacksonParser() throws IOException, JSONParserException {
        mapper.readTree(pass1);
    }

    @Benchmark
    public void testPass3MyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(pass3);
    }

    @Benchmark
    public void testPass3JacksonParser() throws IOException, JSONParserException {
        mapper.readTree(pass3);
    }

    @Benchmark
    public void testPass2MyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(pass2);
    }

    @Benchmark
    public void testPass2JacksonParser() throws IOException, JSONParserException {
        mapper.readTree(pass2);
    }

    @Benchmark
    public void testPass3TrimMyJSONParser() throws IOException, JSONParserException {
        ParserFactory.readTree(pass3_trim);
    }

    @Benchmark
    public void testPass3TrimJacksonParser() throws IOException, JSONParserException {
        mapper.readTree(pass3_trim);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBenchmark.class.getSimpleName())
                .warmupIterations(20)
                .measurementIterations(20)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
