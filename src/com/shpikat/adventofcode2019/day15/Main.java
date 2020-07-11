package com.shpikat.adventofcode2019.day15;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    private static final Long WALL = -1L;
    private static final Long EMPTY = 0L;
    private static final Long TARGET = Long.MAX_VALUE;

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        final Path inputPath = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(inputPath, StandardCharsets.ISO_8859_1).trim().split(",");
        final long[] program = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Long.parseLong(strings[i]);
        }

        final BlockingQueue<Long> input = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Long> output = new ArrayBlockingQueue<>(10);
        final Intcode computer = new Intcode(program, input, output, t -> {
        });

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Void> future = executor.submit(computer);

        final PrettySolver solver = new PrettySolver(input, output, future);

        JFrame f = new JFrame();
        f.add(solver);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(1000, 800);
        f.setVisible(true);


        solver.explore(new Coordinate(0, 0));
        System.out.println("All paths covered");
        final int steps = solver.findShortest();

        System.out.println("Shortest path: " + steps);

        // check for normal termination
//        future.get();
        executor.shutdown();
    }


    private static class PrettySolver extends JPanel {
        private final Map<Coordinate, Long> grid = new ConcurrentHashMap<>();
        private final BlockingQueue<Long> input;
        private final BlockingQueue<Long> output;
        private final Future<Void> future;

        private PrettySolver(final BlockingQueue<Long> input, final BlockingQueue<Long> output, final Future<Void> future) {
            this.input = input;
            this.output = output;
            this.future = future;
            grid.put(new Coordinate(0, 0), EMPTY);
        }

        void explore(final Coordinate current) throws InterruptedException {
            if (future.isDone()) {
                System.out.println("End of story");
                return;
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
                        grid.put(next, TARGET);
                        System.out.println("Found! " + next);
                        explore(next);
                        input.put(Direction.reverse(direction).value);
                        if (output.take() != 1) {
                            throw new IllegalStateException("Failed to reverse!");
                        }
                    }
                    repaint();
                }
            }
        }

        public int findShortest() {
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
                    repaint();
                } else if (value.equals(TARGET)) {
                    return current.step.intValue() - 1;
                }
            }
            throw new IllegalStateException("Target not found");
        }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            final int x = 500;
            final int y = 400;
            final int size = 8;
            grid.forEach((coordinate, value) -> {
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
            });
            g.setColor(Color.GREEN);
            g.fillRect(x, y, size, size);
            g.setColor(Color.BLACK);
        }

        private static class Step {
            private final Coordinate coordinate;
            private final Long step;

            private Step(final Coordinate coordinate, final Long step) {
                this.coordinate = coordinate;
                this.step = step;
            }
        }
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

    static class Coordinate {
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

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

}
