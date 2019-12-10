package com.shpikat.adventofcode2019.day09;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(path, StandardCharsets.ISO_8859_1).trim().split(",");
        final long[] program = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Long.parseLong(strings[i]);
        }

        final BlockingQueue<Long> input = new ArrayBlockingQueue<>(100);
        final BlockingQueue<Long> output = new ArrayBlockingQueue<>(100);
        final Intcode computer = new Intcode(program, input, output);
        input.put(1L);
        computer.call();

        System.out.println(output);
    }
}
