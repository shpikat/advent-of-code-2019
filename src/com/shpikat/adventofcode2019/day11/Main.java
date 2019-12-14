package com.shpikat.adventofcode2019.day11;

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

    private static final Long BLACK = 0L;
    private static final Long WHITE = 1L;

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(path, StandardCharsets.ISO_8859_1).trim().split(",");
        final long[] program = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Long.parseLong(strings[i]);
        }

        final BlockingQueue<Long> input = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Long> output = new ArrayBlockingQueue<>(10);
        final Intcode computer = new Intcode(program, input, output);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Void> future = executor.submit(computer);

        final Map<Coordinate, Long> hull = new HashMap<>();
        final Robot robot = new Robot();
        robot.paint(hull, WHITE);

        do {
            final Long current = robot.getCurrent(hull);
            input.put(current);

            final Long colour = output.poll(2, TimeUnit.SECONDS);
            if (colour == null && future.isDone()) {
                break;
            }
            robot.paint(hull, colour);
            final Long rotation = output.take();
            robot.move(rotation);
        } while (!future.isDone());

        // check for normal termination
        future.get();
        print(hull);
        executor.shutdown();
    }

    private static void print(final Map<Coordinate, Long> hull) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Coordinate coordinate : hull.keySet()) {
            minX = Math.min(minX, coordinate.x);
            maxX = Math.max(maxX, coordinate.x);
            minY = Math.min(minY, coordinate.y);
            maxY = Math.max(maxY, coordinate.y);
        }

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                final Long colour = hull.getOrDefault(new Coordinate(x, y), BLACK);
                System.out.print(colour.equals(WHITE) ? '■' : ' ');
            }

            System.out.println();
        }
    }


    private static class Robot {
        private int x;
        private int y;
        private Direction direction = Direction.UP;

        private Long getCurrent(final Map<Coordinate, Long> hull) {
            return hull.getOrDefault(new Coordinate(x, y), BLACK);
        }

        void paint(final Map<Coordinate, Long> hull, final Long colour) {
            hull.put(new Coordinate(x, y), colour);
        }

        void move(final Long rotation) {
            direction = direction.rotate(rotation.intValue());
            direction.move(this);
        }

        private enum Direction {
            UP {
                @Override
                void move(final Robot robot) {
                    robot.y--;
                }
            },
            RIGHT {
                @Override
                void move(final Robot robot) {
                    robot.x++;
                }
            },
            DOWN {
                @Override
                void move(final Robot robot) {
                    robot.y++;
                }
            },
            LEFT {
                @Override
                void move(final Robot robot) {
                    robot.x--;
                }
            };

            private static Direction[][] rotations = new Direction[][]{
                    {LEFT, UP, RIGHT, DOWN},
                    {RIGHT, DOWN, LEFT, UP},
            };

            Direction rotate(final int rotation) {
                return rotations[rotation][ordinal()];
            }

            abstract void move(Robot robot);
        }
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
