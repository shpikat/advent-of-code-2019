package com.shpikat.adventofcode2019.day13;

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
import java.util.function.Consumer;

public class Main {

    private static final int PADDLE = 3;
    private static final int BALL = 4;

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(path, StandardCharsets.ISO_8859_1).trim().split(",");
        final long[] program = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Long.parseLong(strings[i]);
        }

        final BlockingQueue<Long> input = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Long> output = new ArrayBlockingQueue<>(10);
        final Consumer<long[]> gameWizard = memory -> memory[0] = 2;
        final Intcode arcade = new Intcode(program, input, output, gameWizard);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Void> future = executor.submit(arcade);

        final Map<Coordinate, Long> grid = new HashMap<>();

        long paddle = 0;
        long score = 0;
        do {
            final Long x = output.poll(2, TimeUnit.SECONDS);
            if (x == null) {
                if (!future.isDone()) {
                    System.err.println("Time-out");
                    future.cancel(true);
                }
                break;
            }
            final Long y = output.take();
            final Long value = output.take();
            if (x == -1 && y == 0) {
                score = value;
            } else {
                if (value == PADDLE) {
                    paddle = x;
                } else if (value == BALL) {
                    final long tilt = Long.compare(x, paddle);
                    input.put(tilt);
                }

                grid.put(new Coordinate(x.intValue(), y.intValue()), value);
            }
        } while (!future.isDone());

        // check for normal termination
        future.get();
        executor.shutdown();

        if (grid.values().stream().anyMatch(tile -> tile == 2)) {
            System.err.println("Not all blocks are cleared");
        }

        System.out.println(score);
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
