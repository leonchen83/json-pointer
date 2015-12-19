package com.moilioncircle.json.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

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
        new BenchMark().test();
    }

    public void test() throws IOException, JSONParserException {
        benchmark("canada.json", 800); //faster than jackson
        benchmark("citm_catalog.json", 1000); //slower than jackson
        benchmark("twitter.json", 1000); //slower than jackson
        System.out.println("-----------------------------------------------------");
        benchmark0("canada.json", 800); //faster than jackson
        benchmark0("citm_catalog.json", 1000); //faster than jackson
        benchmark0("twitter.json", 10000); //slower than jackson
        System.out.println("-----------------------------------------------------");
//        benchmark1("canada.json", 1000); //faster than jackson
//        benchmark1("citm_catalog.json", 4000); //slower than jackson
//        benchmark1("twitter.json", 10000); //slower than jackson
//        System.out.println("-----------------------------------------------------");
//        benchmark2("canada.json", 1000); //faster than jackson
//        benchmark2("citm_catalog.json", 4000); //slower than jackson
//        benchmark2("twitter.json", 10000); //slower than jackson
    }

    //reader
    private void benchmark(String fileName, int loopSize) throws IOException, JSONParserException {
        System.out.println(fileName);
        InputStream stream = BenchMark.class.getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = IOUtils.toByteArray(stream);

        IOUtils.closeQuietly(stream);
        String str = new String(bytes);
        StringReader[] streams = new StringReader[loopSize];
        for(int i=0;i<streams.length;i++){
            streams[i] = new StringReader(str);
        }
        ObjectMapper mapper = new ObjectMapper();

        long start = System.currentTimeMillis();
        //47389,36582,44350

        for (int i = 0; i < loopSize; i++) {
            mapper.readTree(streams[i]);

        }
        System.out.println("Jackson parser: " + (System.currentTimeMillis() - start) / (double) loopSize);

        for(int i=0;i<streams.length;i++){
            streams[i] = new StringReader(str);
        }
        start = System.currentTimeMillis();
        //73612,64926,68491
        for (int i = 0; i < loopSize; i++) {
            ParserFactory.readTree(streams[i],true);
        }
        System.out.println("MY JSON parser: " + (System.currentTimeMillis() - start) / (double) loopSize);
    }

    //str
    private void benchmark0(String fileName, int loopSize) throws IOException, JSONParserException {
        System.out.println(fileName);
        InputStream stream = BenchMark.class.getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = IOUtils.toByteArray(stream);

        IOUtils.closeQuietly(stream);
        String str = new String(bytes);
        ObjectMapper mapper = new ObjectMapper();

        long start = System.currentTimeMillis();
        //47389,36582,44350

        for (int i = 0; i < loopSize; i++) {
            mapper.readTree(str);
        }
        System.out.println("Jackson parser: " + (System.currentTimeMillis() - start) / (double) loopSize);
        start = System.currentTimeMillis();
        //73612,64926,68491
        for (int i = 0; i < loopSize; i++) {
            ParserFactory.readTree(str,true);
        }
        System.out.println("MY JSON parser: " + (System.currentTimeMillis() - start) / (double) loopSize);
    }

    //inputstream
    private void benchmark1(String fileName, int loopSize) throws IOException, JSONParserException {
        System.out.println(fileName);
        InputStream stream = BenchMark.class.getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = IOUtils.toByteArray(stream);

        IOUtils.closeQuietly(stream);
        String str = new String(bytes);
        InputStream[] streams = new InputStream[loopSize];
        for(int i=0;i<streams.length;i++){
            streams[i] = new ByteArrayInputStream(bytes);
        }
        ObjectMapper mapper = new ObjectMapper();

        long start = System.currentTimeMillis();
        //47389,36582,44350

        for (int i = 0; i < loopSize; i++) {
            mapper.readTree(streams[i]);
        }
        System.out.println("Jackson parser: " + (System.currentTimeMillis() - start) / (double) loopSize);
        for(int i=0;i<streams.length;i++){
            streams[i] = new ByteArrayInputStream(bytes);
        }
        start = System.currentTimeMillis();
        //73612,64926,68491
        for (int i = 0; i < loopSize; i++) {
            ParserFactory.readTree(streams[i]);
        }
        System.out.println("MY JSON parser: " + (System.currentTimeMillis() - start) / (double) loopSize);
    }

    //bytes
    private void benchmark2(String fileName, int loopSize) throws IOException, JSONParserException {
        System.out.println(fileName);
        InputStream stream = BenchMark.class.getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = IOUtils.toByteArray(stream);

        IOUtils.closeQuietly(stream);

        ObjectMapper mapper = new ObjectMapper();

        long start = System.currentTimeMillis();
        //47389,36582,44350

        for (int i = 0; i < loopSize; i++) {
            mapper.readTree(bytes);
        }
        System.out.println("Jackson parser: " + (System.currentTimeMillis() - start) / (double) loopSize);
        start = System.currentTimeMillis();
        //73612,64926,68491
        for (int i = 0; i < loopSize; i++) {
            ParserFactory.readTree(bytes);
        }
        System.out.println("MY JSON parser: " + (System.currentTimeMillis() - start) / (double) loopSize);
    }

}
