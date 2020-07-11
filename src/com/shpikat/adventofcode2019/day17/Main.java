package com.shpikat.adventofcode2019.day17;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        final Path inputPath = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(inputPath, StandardCharsets.ISO_8859_1).trim().split(",");
        final long[] program = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Long.parseLong(strings[i]);
        }

        final BlockingQueue<Long> input = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Long> output = new ArrayBlockingQueue<>(1000);
        final Intcode computer = new Intcode(program, input, output, t -> {
        });

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Void> future = executor.submit(computer);

        final List<Long> buffer = new ArrayList<>(1000);
        do {
            if (output.drainTo(buffer) < 100) {
                Thread.sleep(50);
            }
        } while (!future.isDone());
        output.drainTo(buffer);

        final StringBuilder viewBiulder = new StringBuilder(buffer.size());
        for (final Long symbol : buffer) {
            viewBiulder.append((char) symbol.intValue());
        }

//        System.out.println(viewBiulder);

        int sum = 0;
        final String[] lines = viewBiulder.toString().split("\n");
        for (int i = 1; i < lines.length - 1; i++) {
            final String line = lines[i];
            for (int j = 1; j < line.length() - 1; j++) {
                if (line.charAt(j - 1) == '#'
                        && line.charAt(j + 1) == '#'
                        && lines[i - 1].charAt(j) == '#'
                        && lines[i + 1].charAt(j) == '#') {
                    sum += i * j;
                }
            }
        }
        System.out.println(sum);

        executor.shutdown();
    }
}
