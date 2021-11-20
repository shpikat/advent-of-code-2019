package com.shpikat.adventofcode2019;

import com.shpikat.adventofcode2019.Day09.Intcode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Day11 {

    private static final Long BLACK = 0L;
    private static final Long WHITE = 1L;

    static class Part1 {
        static int solve(final String input) throws ExecutionException, InterruptedException {
            final Map<Coordinate, Long> hull = new HashMap<>();

            runProgram(input, hull, new Robot());

            return hull.size();
        }
    }

    static class Part2 {
        static String solve(final String input) throws ExecutionException, InterruptedException {
            final Map<Coordinate, Long> hull = new HashMap<>();
            final Robot robot = new Robot();
            robot.paint(hull, WHITE);

            runProgram(input, hull, robot);

            return toString(hull);
        }

        private static String toString(final Map<Coordinate, Long> hull) {
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

            final StringBuilder builder = new StringBuilder((maxY - minY + 1) * (maxX - minX));
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    final Long colour = hull.getOrDefault(new Coordinate(x, y), BLACK);
                    builder.append(colour.equals(WHITE) ? '$' : ' ');
                }
                builder.append('\n');
            }
            return builder.toString();
        }
    }

    private static void runProgram(final String input, final Map<Coordinate, Long> hull, final Robot robot)
            throws InterruptedException, ExecutionException {
        final Intcode computer = Intcode.fromInput(input, 32);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<Void> future = executor.submit(computer);

        do {
            final Long current = robot.getCurrent(hull);
            computer.input.put(current);

            final Long colour = computer.output.poll(100, TimeUnit.MILLISECONDS);
            if (colour == null) {
                // Probably the execution is over, hence no incoming data
                continue;
            }
            robot.paint(hull, colour);
            final Long rotation = computer.output.take();
            robot.move(rotation);
        } while (!future.isDone() || !computer.output.isEmpty());

        // check for normal termination
        future.get();
        executor.shutdown();
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

            private static final Direction[][] rotations = new Direction[][]{
                    {LEFT, UP, RIGHT, DOWN},
                    {RIGHT, DOWN, LEFT, UP},
            };

            Direction rotate(final int rotation) {
                return rotations[rotation][ordinal()];
            }

            abstract void move(Robot robot);
        }
    }

    private record Coordinate(int x, int y) {
    }
}
