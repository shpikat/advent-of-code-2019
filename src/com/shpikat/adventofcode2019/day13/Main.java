package com.shpikat.adventofcode2019.day13;

import com.shpikat.adventofcode2019.day09.Intcode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(path, StandardCharsets.ISO_8859_1).trim().split(",");
        final long[] program = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Long.parseLong(strings[i]);
        }

        final BlockingQueue<Long> input = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Long> output = new ArrayBlockingQueue<>(10);
        final Intcode arcade = new Intcode(program, input, output);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Void> future = executor.submit(arcade);

        final Map<Coordinate, Long> grid = new HashMap<>();

        do {
            final Long x = output.poll(2, TimeUnit.SECONDS);
            if (x == null && future.isDone()) {
                break;
            }
            final Long y = output.take();
            final Long tileId = output.take();

            grid.put(new Coordinate(x.intValue(), y.intValue()), tileId);
        } while (!future.isDone());

        // check for normal termination
        future.get();
        executor.shutdown();

        final long count = grid.values().stream()
                .filter(tile -> tile == 2)
                .count();
        System.out.println(count);
    }

    private static class Coordinate {
        private final int x;
        private final int y;

        private Coordinate(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
