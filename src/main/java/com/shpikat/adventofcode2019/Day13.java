package com.shpikat.adventofcode2019;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.shpikat.adventofcode2019.Utils.readInput;

public class Day13 {

    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int BLOCK = 2;
    private static final int PADDLE = 3;
    private static final int BALL = 4;

    private static final int OUTPUT_CAPACITY = 128;

    static class Part1 {

        static long solve(final String input) throws InterruptedException, ExecutionException {
            final Day09.Intcode arcade = Day09.Intcode.fromInput(input, 32);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Void> future = executor.submit(arcade);

            final Map<Coordinate, Long> grid = new HashMap<>();

            do {
                final Long x = arcade.output.poll(100, TimeUnit.MILLISECONDS);
                if (x == null) {
                    // Probably the execution is over, hence no incoming data
                    continue;
                }
                final Long y = arcade.output.take();
                final Long tileId = arcade.output.take();

                grid.put(new Coordinate(x.intValue(), y.intValue()), tileId);
            } while (!future.isDone() || !arcade.output.isEmpty());

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
            return solve(
                    input,
                    new HashMap<>(),
                    value -> {
                    },
                    () -> {
                    });
        }

        private static long solve(final String input, final Map<Coordinate, Long> grid, final Consumer<Long> scoreUpdated, final Runnable updated) throws InterruptedException, ExecutionException {
            final Consumer<long[]> gameWizard = memory -> memory[0] = 2;
            final Day13.Intcode arcade = Day13.Intcode.fromInput(input, gameWizard);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Void> future = executor.submit(arcade);

            long paddle = 0;
            long score = 0;
            final Collection<Long> buffer = new ArrayList<>(OUTPUT_CAPACITY);
            do {
                final Long first = arcade.output.poll(100, TimeUnit.MILLISECONDS);
                if (first == null) {
                    continue;
                }
                buffer.add(first);
                final int maxElements = (buffer.size() + arcade.output.size()) / 3 * 3 - buffer.size();
                arcade.output.drainTo(buffer, maxElements);
                if (buffer.size() < 3) {
                    continue;
                }

                final Iterator<Long> iterator = buffer.iterator();
                while (iterator.hasNext()) {
                    final int x = iterator.next().intValue();
                    final int y = iterator.next().intValue();
                    final Long value = iterator.next();

                    if (x == -1 && y == 0) {
                        score = value;
                        scoreUpdated.accept(score);
                    } else {
                        if (value == PADDLE) {
                            paddle = x;
                        } else if (value == BALL) {
                            final long tilt = Long.compare(x, paddle);
                            arcade.input.put(tilt);
                        }

                        grid.put(new Coordinate(x, y), value);
                    }
                }
                updated.run();
                buffer.clear();
            } while (!future.isDone() || !arcade.output.isEmpty());

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
            return new Intcode(readProgram(input), createInput(16), createOutput(OUTPUT_CAPACITY), runtimePatcher);
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

    private static class DemoPanel extends JPanel {
        private final Map<Coordinate, Long> grid = new ConcurrentHashMap<>();
        private final AtomicLong score = new AtomicLong(0);
        private final AtomicBoolean done = new AtomicBoolean(false);

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            final int baseX = 100;
            final int baseY = 100;
            final int size = 16;
            g.drawString("Score: " + score.get(), 40, 40);
            for (Map.Entry<Coordinate, Long> entry : grid.entrySet()) {
                Coordinate coordinate = entry.getKey();
                final int tileId = entry.getValue().intValue();
                final int x = baseX + coordinate.x * size;
                final int y = baseY + coordinate.y * size;
                switch (tileId) {
                    case WALL -> {
                        g.setColor(Color.GRAY);
                        g.fillRect(x, y, size, size);
                        g.setColor(Color.BLACK);
                    }
                    case BLOCK -> {
                        g.drawRect(x, y, size, size);
                        g.drawOval(x + size / 4, y + size / 4, size / 2, size / 2);
                    }
                    case PADDLE -> g.fillRect(x, y, size, size);
                    case BALL -> g.fillOval(x, y, size, size);
                }
            }

            if (done.get()) {
                g.drawString("All done. You can close the window now.", 400, 500);
            }
        }

        private void scoreUpdated(final Long newScore) {
            score.set(newScore);
        }

        private void updated() {
            repaint();
            try {
                // looks like 60fps
                Thread.sleep(17);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void done() {
            done.lazySet(true);
            repaint();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final DemoPanel panel = new DemoPanel();
        final JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(1000, 600);
        f.setVisible(true);

        final String input = readInput("day13_input.txt");
        Part2.solve(input, panel.grid, panel::scoreUpdated, panel::updated);
        panel.done();
    }
}
