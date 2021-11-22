package com.shpikat.adventofcode2019;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day17 {

    static class Part1 {
        static int solve(final String input) throws InterruptedException, ExecutionException {
            // Capacity must be big enough
            final Intcode computer = Intcode.fromInput(input, 16 * 1024);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Void> future = executor.submit(computer);
            // No interaction needed, just let it work
            future.get();
            executor.shutdown();

            final StringBuilder viewBuilder = new StringBuilder(computer.output.size());
            for (final Long symbol : computer.output) {
                viewBuilder.append((char) symbol.intValue());
            }
            final String[] scaffolds = viewBuilder.toString().split("\n");

            int sum = 0;
            for (int i = 1; i < scaffolds.length - 1; i++) {
                final String line = scaffolds[i];
                for (int j = 1; j < line.length() - 1; j++) {
                    if (line.charAt(j - 1) == '#'
                            && line.charAt(j + 1) == '#'
                            && scaffolds[i - 1].charAt(j) == '#'
                            && scaffolds[i + 1].charAt(j) == '#') {
                        sum += i * j;
                    }
                }
            }

            return sum;
        }
    }

    static class Part2 {

        private static final List<String> MOVEMENT_FUNCTION_NAMES = List.of("A", "B", "C");
        private static final Long NEWLINE = 10L;

        static int solve(final String input) throws InterruptedException, ExecutionException {
            final Intcode computer = Intcode.fromInput(input, 128, memory -> memory[0] = 2);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Void> future = executor.submit(computer);

            final List<String> scaffolds = readView(computer.output);
            final String path = getPath(scaffolds);
            final List<String> movementFunctions = getMovementFunctions(path);

            for (final String line : movementFunctions) {
                final String prompt = readLine(computer.output);
                System.out.printf("%s %s%n", prompt, line);

                for (int i = 0; i < line.length(); i++) {
                    computer.input.put((long) line.charAt(i));
                }
                computer.input.put(NEWLINE);
            }
            final char videoFeed = 'n';  // no video feed, please
            final String prompt = readLine(computer.output);
            System.out.printf("%s %c%n", prompt, videoFeed);

            computer.input.put((long) videoFeed);
            computer.input.put(NEWLINE);

            // end of prompts
            readLine(computer.output);
            // still produces a single frame of the video feed
            readView(computer.output);

            future.get();
            executor.shutdown();

            return computer.output.take().intValue();
        }

        private static List<String> readView(final BlockingQueue<Long> output) throws InterruptedException {
            final List<String> view = new ArrayList<>();
            while (true) {
                final String line = readLine(output);
                if (line.length() == 0) {
                    break;
                }
                view.add(line);
            }
            return view;
        }

        private static String readLine(final BlockingQueue<Long> output) throws InterruptedException {
            final StringBuilder buffer = new StringBuilder(128);
            while (true) {
                final Long value = output.take();
                if (value.equals(NEWLINE)) {
                    break;
                }
                buffer.append((char) value.intValue());
            }
            return buffer.toString();
        }

        private static String getPath(final List<String> scaffolds) {
            for (int i = 0; i < scaffolds.size(); i++) {
                final String line = scaffolds.get(i);
                // adding three to compensate minus three for not found indices
                final int index = line.indexOf('<')
                        + line.indexOf('^')
                        + line.indexOf('>')
                        + line.indexOf('v')
                        + 3;
                if (index >= 0) {
                    Coordinate current = new Coordinate(index, i);
                    Direction direction = switch (current.get(scaffolds)) {
                        case '^' -> Direction.UP;
                        case '>' -> Direction.RIGHT;
                        case 'v' -> Direction.DOWN;
                        case '<' -> Direction.LEFT;
                        default -> throw new IllegalStateException("Unexpected direction: " + current.get(scaffolds));
                    };

                    final List<String> path = new ArrayList<>();
                    while (true) {
                        int forward = 0;
                        while (true) {
                            final Coordinate next = direction.next(current, scaffolds);
                            if (next != null && next.get(scaffolds) == '#') {
                                ++forward;
                                current = next;
                            } else {
                                break;
                            }
                        }
                        if (forward != 0) {
                            path.add(String.valueOf(forward));
                        }
                        final Direction left = Direction.turnLeft(direction);
                        final Coordinate nextOnTheLeft = left.next(current, scaffolds);
                        if (nextOnTheLeft != null && nextOnTheLeft.get(scaffolds) == '#') {
                            path.add("L");
                            direction = left;
                        } else {
                            final Direction right = Direction.turnRight(direction);
                            final Coordinate nextOnTheRight = right.next(current, scaffolds);
                            if (nextOnTheRight != null && nextOnTheRight.get(scaffolds) == '#') {
                                path.add("R");
                                direction = right;
                            } else {
                                return String.join(",", path);
                            }
                        }
                    }
                }
            }

            throw new IllegalStateException("Robot not found in the view");
        }

        private static List<String> getMovementFunctions(final String path) {
            String mainRoutine = path;
            final Matcher matcher = Pattern
                    .compile("^(?<A>[LR0-9,]{1,20}),(\\k<A>,)*(?<B>[LR0-9,]{1,20}),((\\k<A>|\\k<B>),)*(?<C>[LR0-9,]{1,20})(,(\\k<A>|\\k<B>|\\k<C>))*$")
                    .matcher(mainRoutine);
            if (matcher.matches()) {
                final List<String> movementFunctions = new ArrayList<>(MOVEMENT_FUNCTION_NAMES.size() + 1);
                movementFunctions.add(null); // reserve a place for the main routine
                for (final String functionName : MOVEMENT_FUNCTION_NAMES) {
                    final String group = matcher.group(functionName);
                    movementFunctions.add(group);
                    mainRoutine = mainRoutine.replace(group, functionName);
                }
                movementFunctions.set(0, mainRoutine);
                return movementFunctions;
            } else {
                throw new IllegalStateException("Unable to break into functions: " + mainRoutine);
            }
        }

        private enum Direction {
            UP {
                @Override
                Coordinate next(final Coordinate coordinate, final List<String> scaffolds) {
                    return coordinate.y == 0
                            ? null
                            : new Coordinate(coordinate.x, coordinate.y - 1);
                }
            },
            RIGHT {
                @Override
                Coordinate next(final Coordinate coordinate, final List<String> scaffolds) {
                    return coordinate.x == scaffolds.get(0).length() - 1
                            ? null
                            : new Coordinate(coordinate.x + 1, coordinate.y);
                }
            },
            DOWN {
                @Override
                Coordinate next(final Coordinate coordinate, final List<String> scaffolds) {
                    return coordinate.y == scaffolds.size() - 1
                            ? null
                            : new Coordinate(coordinate.x, coordinate.y + 1);
                }
            },
            LEFT {
                @Override
                Coordinate next(final Coordinate coordinate, final List<String> scaffolds) {
                    return coordinate.x == 0
                            ? null
                            : new Coordinate(coordinate.x - 1, coordinate.y);
                }
            },
            ;

            private static final Direction[] toTheLeft = {LEFT, UP, RIGHT, DOWN};
            private static final Direction[] toTheRight = {RIGHT, DOWN, LEFT, UP};

            static Direction turnLeft(final Direction current) {
                return toTheLeft[current.ordinal()];
            }

            static Direction turnRight(final Direction current) {
                return toTheRight[current.ordinal()];
            }

            abstract Coordinate next(Coordinate coordinate, final List<String> scaffolds);
        }

        record Coordinate(int x, int y) {
            char get(final List<String> scaffolds) {
                return scaffolds.get(y).charAt(x);
            }
        }
    }
}
