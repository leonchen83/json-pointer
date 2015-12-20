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
        } catch (Exception e) {

        }
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

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBenchmark.class.getSimpleName())
                .warmupIterations(30)
                .measurementIterations(30)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
