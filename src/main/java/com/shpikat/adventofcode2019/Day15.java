package com.shpikat.adventofcode2019;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.ToIntFunction;

import static com.shpikat.adventofcode2019.Utils.readInput;

public class Day15 {

    private static final Long WALL = -1L;
    private static final Long EMPTY = 0L;
    private static final Long TARGET = Long.MAX_VALUE;

    static class Part1 {
        static int solve(final String input) throws InterruptedException, ExecutionException {
            return solve(input, new HashMap<>(), () -> {
            });
        }

        private static int solve(final String input, final Map<Coordinate, Long> grid, final Runnable updated) throws InterruptedException {
            return Day15.solve(input, grid, updated, Tracker::findShortest);
        }
    }

    static class Part2 {
        static int solve(final String input) throws InterruptedException, ExecutionException {
            return solve(input, new HashMap<>(), () -> {
            });
        }

        private static int solve(final String input, final Map<Coordinate, Long> grid, final Runnable updated) throws InterruptedException {
            return Day15.solve(input, grid, updated, Tracker::fillAllFromTarget);
        }
    }

    private static int solve(final String input, final Map<Coordinate, Long> grid, final Runnable updated, final ToIntFunction<Tracker> getAnswer) throws InterruptedException {
        final Day09.Intcode computer = Day09.Intcode.fromInput(input, 32);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Void> future = executor.submit(computer);

        final Tracker tracker = new Tracker(grid, computer.input, computer.output, future, updated);
        tracker.explore(new Coordinate(0, 0));
        final int answer = getAnswer.applyAsInt(tracker);

        executor.shutdown();

        return answer;
    }

    private static class Tracker {
        private final Map<Coordinate, Long> grid;
        private final BlockingQueue<Long> input;
        private final BlockingQueue<Long> output;
        private final Future<Void> future;
        private final Runnable updated;

        // For part2 only, but let it live
        private Coordinate target;

        private Tracker(final Map<Coordinate, Long> grid, final BlockingQueue<Long> input, final BlockingQueue<Long> output, final Future<Void> future, final Runnable updated) {
            this.grid = grid;
            this.input = input;
            this.output = output;
            this.future = future;
            this.updated = updated;
            this.grid.put(new Coordinate(0, 0), EMPTY);
        }

        private void explore(final Coordinate current) throws InterruptedException {
            if (future.isDone()) {
                throw new IllegalStateException("Execution ended unexpectedly");
            }
            for (final Direction direction : Direction.values()) {
                final Coordinate next = direction.next(current);
                if (grid.get(next) == null) {
                    input.put(direction.value);
                    final long status = output.take();
                    if (status == 0) {
                        grid.put(next, WALL);
                    } else if (status == 1) {
                        grid.put(next, EMPTY);
                        explore(next);
                        input.put(Direction.reverse(direction).value);
                        if (output.take() != 1) {
                            throw new IllegalStateException("Failed to reverse!");
                        }
                    } else if (status == 2) {
                        target = next;

                        grid.put(next, TARGET);
                        explore(next);
                        input.put(Direction.reverse(direction).value);
                        if (output.take() != 1) {
                            throw new IllegalStateException("Failed to reverse!");
                        }
                    }
                    updated.run();
                }
            }
        }

        private int findShortest() {
            final Queue<Step> queue = new ArrayDeque<>();
            queue.add(new Step(new Coordinate(0, 0), 1L));
            while (!queue.isEmpty()) {
                final Step current = queue.remove();
                final Long value = grid.get(current.coordinate);
                if (value == 0L) {
                    grid.put(current.coordinate, current.step);
                    for (final Direction direction : Direction.values()) {
                        queue.add(new Step(direction.next(current.coordinate), current.step + 1));
                    }
                    updated.run();
                } else if (value.equals(TARGET)) {
                    return current.step.intValue() - 1;
                }
            }
            throw new IllegalStateException("Target not found");
        }

        private int fillAllFromTarget() {
            final Queue<Step> queue = new ArrayDeque<>();
            queue.add(new Step(target, 1L));
            long max = 0;
            while (!queue.isEmpty()) {
                final Step current = queue.remove();
                final Long value = grid.get(current.coordinate);
                if (value == 0L || value.equals(TARGET)) {
                    grid.put(current.coordinate, current.step);
                    max = Math.max(max, current.step);
                    for (final Direction direction : Direction.values()) {
                        queue.add(new Step(direction.next(current.coordinate), current.step + 1));
                    }
                    updated.run();
                }
            }
            return (int) (max - 1);
        }

        private enum Direction {
            NORTH(1) {
                @Override
                Coordinate next(final Coordinate coordinate) {
                    return new Coordinate(coordinate.x, coordinate.y - 1);
                }
            },
            EAST(4) {
                @Override
                Coordinate next(final Coordinate coordinate) {
                    return new Coordinate(coordinate.x + 1, coordinate.y);
                }
            },
            SOUTH(2) {
                @Override
                Coordinate next(final Coordinate coordinate) {
                    return new Coordinate(coordinate.x, coordinate.y + 1);
                }
            },
            WEST(3) {
                @Override
                Coordinate next(final Coordinate coordinate) {
                    return new Coordinate(coordinate.x - 1, coordinate.y);
                }
            },
            ;

            private static final Map<Direction, Direction> reversed = createReversed();

            private final Long value;

            Direction(final long value) {
                this.value = value;
            }

            static Direction reverse(Direction direction) {
                return reversed.get(direction);
            }

            abstract Coordinate next(Coordinate coordinate);

            private static Map<Direction, Direction> createReversed() {
                final Map<Direction, Direction> reversed = new EnumMap<>(Direction.class);
                reversed.put(NORTH, SOUTH);
                reversed.put(EAST, WEST);
                reversed.put(SOUTH, NORTH);
                reversed.put(WEST, EAST);
                return reversed;
            }
        }

        private record Step(Coordinate coordinate, Long step) {
        }
    }

    private record Coordinate(int x, int y) {
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    private static class DemoPanel extends JPanel {
        private final List<Map<Coordinate, Long>> grids = new CopyOnWriteArrayList<>();
        private final AtomicBoolean done = new AtomicBoolean(false);

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            final int y = 400;
            final int size = 8;
            int x = 300;
            int part = 1;
            for (final Map<Coordinate, Long> grid : grids) {
                g.drawString("Part " + part++, x, 100);
                for (Map.Entry<Coordinate, Long> entry : grid.entrySet()) {
                    Coordinate coordinate = entry.getKey();
                    Long value = entry.getValue();
                    if (value.equals(WALL)) {
                        g.fillRect(x + coordinate.x * size, y + coordinate.y * size, size, size);
                    } else if (value.equals(TARGET)) {
                        g.setColor(Color.RED);
                        g.fillRect(x + coordinate.x * size, y + coordinate.y * size, size, size);
                        g.setColor(Color.BLACK);
                    } else {
                        final int color = value.intValue() / 2;
                        g.setColor(new Color(color, color, color));
                        g.fillOval(x + coordinate.x * size + size / 4, y + coordinate.y * size + size / 4, size / 2, size / 2);
                        g.setColor(Color.BLACK);
                    }
                }
                g.setColor(Color.GREEN);
                g.fillRect(x, y, size, size);
                g.setColor(Color.BLACK);

                x += 500;
            }

            if (done.get()) {
                g.drawString("All done. You can close the window now.", 500, 700);
            }
        }

        private Map<Coordinate, Long> newGrid() {
            final Map<Coordinate, Long> grid = new ConcurrentHashMap<>();
            grids.add(grid);
            return grid;
        }

        private void updated() {
            repaint();
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void done() {
            done.lazySet(true);
            repaint();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final DemoPanel panel = new DemoPanel();
        final JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(1200, 800);
        f.setVisible(true);

        final String input = readInput("day15_input.txt");
        Part1.solve(input, panel.newGrid(), panel::updated);
        Part2.solve(input, panel.newGrid(), panel::updated);
        panel.done();
    }
}
