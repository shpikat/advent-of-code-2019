package com.shpikat.adventofcode2019;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.shpikat.adventofcode2019.Utils.readInput;

public class Day13 {

    private static final int PADDLE = 3;
    private static final int BALL = 4;

    static class Part1 {

        static long solve(final String input) throws InterruptedException, ExecutionException {
            final Day09.Intcode arcade = Day09.Intcode.fromInput(input, 32);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Void> future = executor.submit(arcade);

            final Map<Coordinate, Long> grid = new HashMap<>();

            do {
                final Long x = arcade.output.poll(2, TimeUnit.SECONDS);
                if (x == null && future.isDone()) {
                    break;
                }
                final Long y = arcade.output.take();
                final Long tileId = arcade.output.take();

                grid.put(new Coordinate(x.intValue(), y.intValue()), tileId);
            } while (!future.isDone());

            // check for normal termination
            future.get();
            executor.shutdown();

            return grid.values().stream()
                    .filter(tile -> tile == 2)
                    .count();
        }
    }

    static class Part2 {
        static long solve(final String input) throws InterruptedException, ExecutionException {
            final Consumer<long[]> gameWizard = memory -> memory[0] = 2;
            final Day13.Intcode arcade = Day13.Intcode.fromInput(input, gameWizard);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Void> future = executor.submit(arcade);

            final Map<Coordinate, Long> grid = new HashMap<>();

            long paddle = 0;
            long score = 0;
            do {
                final Long x = arcade.output.poll(2, TimeUnit.SECONDS);
                if (x == null) {
                    if (!future.isDone()) {
                        System.err.println("Time-out");
                        future.cancel(true);
                    }
                    break;
                }
                final Long y = arcade.output.take();
                final Long value = arcade.output.take();
                if (x == -1 && y == 0) {
                    score = value;
                } else {
                    if (value == PADDLE) {
                        paddle = x;
                    } else if (value == BALL) {
                        final long tilt = Long.compare(x, paddle);
                        arcade.input.put(tilt);
                    }

                    grid.put(new Coordinate(x.intValue(), y.intValue()), value);
                }
            } while (!future.isDone());

            // check for normal termination
            future.get();
            executor.shutdown();

            if (grid.values().stream().anyMatch(tile -> tile == 2)) {
                throw new IllegalStateException("Not all blocks are cleared");
            }

            return score;
        }
    }

    private record Coordinate(int x, int y) {
    }

    private static class Intcode extends Day09.Intcode {
        private final Consumer<long[]> runtimePatcher;

        private Intcode(final long[] program,
                        final BlockingQueue<Long> input,
                        final BlockingQueue<Long> output,
                        final Consumer<long[]> runtimePatcher) {
            super(program, input, output);
            this.runtimePatcher = runtimePatcher;
        }

        public static Intcode fromInput(final String input, final Consumer<long[]> runtimePatcher) {
            final int capacity = 128;
            return new Intcode(readProgram(input), createInput(capacity), createOutput(capacity), runtimePatcher);
        }

        @Override
        public Void call() throws InterruptedException {
            final Runtime runtime = new Runtime(program, input, output, runtimePatcher);
            runtime.execute();
            return null;
        }

        private static class Runtime extends Day09.Intcode.Runtime {
            private final Consumer<long[]> patcher;

            Runtime(final long[] program,
                    final BlockingQueue<Long> input,
                    final BlockingQueue<Long> output,
                    final Consumer<long[]> patcher) {
                super(program, input, output);
                this.patcher = patcher;
            }

            @Override
            void execute() throws InterruptedException {
                patcher.accept(memory);
                super.execute();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final String input = readInput("day13_input.txt");
        //TODO implement interactive mode for part 2
        System.out.println(Part2.solve(input));
    }
}
