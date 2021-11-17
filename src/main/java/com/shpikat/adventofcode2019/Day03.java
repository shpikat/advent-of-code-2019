package com.shpikat.adventofcode2019;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.min;

public class Day03 {
    static class Part2 {
        static int solve(final String input) {
            final String[] allLines = input.split("\n");
            final String[] first = allLines[0].split(",");
            final String[] second = allLines[1].split(",");

            final int center = 2 << 13;

            final Map<Integer, Integer> grid = new HashMap<>();

            int x = 0;
            int y = 0;
            int step = 0;
            for (final String command : first) {
                final int distance = Integer.parseInt(command.substring(1));
                switch (command.charAt(0)) {
                    case 'U' -> {
                        for (int i = 1; i <= distance; i++) {
                            grid.putIfAbsent(squash(center + y + i, center + x), ++step);
                        }
                        y += distance;
                    }
                    case 'D' -> {
                        for (int i = 1; i <= distance; i++) {
                            grid.putIfAbsent(squash(center + y - i, center + x), ++step);
                        }
                        y -= distance;
                    }
                    case 'L' -> {
                        for (int i = 1; i <= distance; i++) {
                            grid.putIfAbsent(squash(center + y, center + x - i), ++step);
                        }
                        x -= distance;
                    }
                    case 'R' -> {
                        for (int i = 1; i <= distance; i++) {
                            grid.putIfAbsent(squash(center + y, center + x + i), ++step);
                        }
                        x += distance;
                    }
                }
            }

            grid.put(squash(center, center), 0);

            x = 0;
            y = 0;
            int step2 = 0;
            int fastest = Integer.MAX_VALUE;
            for (final String command : second) {
                final int distance = Integer.parseInt(command.substring(1));
                switch (command.charAt(0)) {
                    case 'U' -> {
                        for (int i = 1; i <= distance; i++) {
                            ++step2;
                            final Integer steps1 = grid.get(squash(center + y + i, center + x));
                            if (steps1 != null) {
                                fastest = min(fastest, steps1 + step2);
                            }
                        }
                        y += distance;
                    }
                    case 'D' -> {
                        for (int i = 1; i <= distance; i++) {
                            ++step2;
                            final Integer steps1 = grid.get(squash(center + y - i, center + x));
                            if (steps1 != null) {
                                fastest = min(fastest, steps1 + step2);
                            }
                        }
                        y -= distance;
                    }
                    case 'L' -> {
                        for (int i = 1; i <= distance; i++) {
                            ++step2;
                            final Integer steps1 = grid.get(squash(center + y, center + x - i));
                            if (steps1 != null) {
                                fastest = min(fastest, steps1 + step2);
                            }
                        }
                        x -= distance;
                    }
                    case 'R' -> {
                        for (int i = 1; i <= distance; i++) {
                            ++step2;
                            final Integer steps1 = grid.get(squash(center + y, center + x + i));
                            if (steps1 != null) {
                                fastest = min(fastest, steps1 + step2);
                            }
                        }
                        x += distance;
                    }
                }
            }

            return fastest;
        }

        private static int squash(final int y, final int x) {
            return y << 16 | x;
        }
    }
}
