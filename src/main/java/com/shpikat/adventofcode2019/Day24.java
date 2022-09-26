package com.shpikat.adventofcode2019;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day24 {

    private static final int SIZE = 5;
    private static final int[] masks = createMasks();

    static class Part1 {

        static int solve(final String input) {
            int layout = readLayout(input);

            final Set<Integer> history = new HashSet<>();
            while (history.add(layout)) {
                int next = layout;
                for (int i = 0; i < masks.length; i++) {
                    final int count = Integer.bitCount(layout & masks[i]);
                    final int bit = 1 << i;
                    final boolean isEmpty = (layout & bit) == 0;
                    if (isEmpty) {
                        if (count == 1 || count == 2) {
                            next |= bit;
                        }
                    } else if (count != 1) {
                        next &= ~bit;
                    }
                }
                layout = next;
            }

            return layout;
        }
    }


    static class Part2 {

        private static final int CENTER = SIZE * SIZE / 2;

        /* Split representation in groups of five for better comprehension. */
        private static final int maskHigherNorth = 0b00000_00000_00000_00000_11111;
        private static final int maskHigherWest = 0b00001_00001_00001_00001_00001;
        private static final int maskHigherEast = 0b10000_10000_10000_10000_10000;
        private static final int maskHigherSouth = 0b11111_00000_00000_00000_00000;
        private static final int maskLowerNorth = 0b00100_00000;
        private static final int maskLowerWest = 0b00010_00000_00000;
        private static final int maskLowerEast = 0b01000_00000_00000;
        private static final int maskLowerSouth = 0b00100_00000_00000_00000;

        static int solve(final int minutes, final String input) {
            int layout = readLayout(input);

            final Map<Integer, Integer> space = new HashMap<>();
            space.put(0, layout);

            Integer lowestLevel = 0;
            Integer highestLevel = 0;
            for (int minute = 0; minute < minutes; minute++) {
                final Map<Integer, Integer> snapshot = Map.copyOf(space);
                snapshot.forEach((level, current) -> {
                    int next = current;
                    for (int i = 0; i < masks.length; i++) {
                        if (i != CENTER) {
                            int count = Integer.bitCount(current & masks[i]);
                            final int higherLevel = snapshot.getOrDefault(level + 1, 0);
                            if (higherLevel != 0) {
                                count += getCountFromHigherLevel(i, higherLevel);
                            }
                            final int lowerLevel = snapshot.getOrDefault(level - 1, 0);
                            if (lowerLevel != 0) {
                                count += getCountFromLowerLevel(i, lowerLevel);
                            }

                            final int bit = 1 << i;
                            final boolean isEmpty = (current & bit) == 0;
                            if (isEmpty) {
                                if (count == 1 || count == 2) {
                                    next |= bit;
                                }
                            } else if (count != 1) {
                                next &= ~bit;
                            }
                        }
                    }
                    space.put(level, next);
                });

                int next = 0;
                final int highestLevelLayout = snapshot.get(highestLevel);
                for (int i = 0; i < masks.length; i++) {
                    if (i != CENTER) {
                        int count = getCountFromLowerLevel(i, highestLevelLayout);
                        if (count == 1 || count == 2) {
                            next |= 1 << i;
                        }
                    }
                }
                if (next != 0) {
                    highestLevel++;
                    space.put(highestLevel, next);
                }

                next = 0;
                final int lowestLevelLayout = snapshot.get(lowestLevel);
                for (int i = 0; i < masks.length; i++) {
                    if (i != CENTER) {
                        final int count = getCountFromHigherLevel(i, lowestLevelLayout);
                        if (count == 1 || count == 2) {
                            next |= 1 << i;
                        }
                    }
                }
                if (next != 0) {
                    lowestLevel--;
                    space.put(lowestLevel, next);
                }
            }

            return space.values().stream().mapToInt(Integer::bitCount).sum();
        }

        private static int getCountFromHigherLevel(final int index, final int layout) {
            return switch (index) {
                case CENTER - SIZE -> Integer.bitCount(layout & maskHigherNorth);
                case CENTER - 1 -> Integer.bitCount(layout & maskHigherWest);
                case CENTER + 1 -> Integer.bitCount(layout & maskHigherEast);
                case CENTER + SIZE -> Integer.bitCount(layout & maskHigherSouth);
                default -> 0;
            };
        }

        private static int getCountFromLowerLevel(final int index, final int layout) {
            int count = 0;
            if (index < SIZE) {
                count += Integer.bitCount(layout & maskLowerNorth);
            } else if (index >= SIZE * (SIZE - 1)) {
                count += Integer.bitCount(layout & maskLowerSouth);
            }
            final int indexInRow = index % SIZE;
            if (indexInRow == 0) {
                count += Integer.bitCount(layout & maskLowerWest);
            } else if (indexInRow == SIZE - 1) {
                count += Integer.bitCount(layout & maskLowerEast);
            }
            return count;
        }
    }

    private static int[] createMasks() {
        final int[] masks = new int[SIZE * SIZE];
        for (int i = 0; i < SIZE; i++) {
            int center = i * SIZE;
            masks[center] = 1 << center - SIZE | 1 << center + SIZE | 1 << center + 1;
            for (int j = 1; j < SIZE - 1; j++) {
                center++;
                masks[center] = 1 << center - SIZE | 1 << center + SIZE | 1 << center - 1 | 1 << center + 1;
            }
            center++;
            masks[center] = 1 << center - SIZE | 1 << center + SIZE | 1 << center - 1;
        }
        return masks;
    }

    private static int readLayout(final String input) {
        final String scan = String.join("", input.split("\n"));

        int layout = 0;
        for (int i = 0; i < scan.length(); i++) {
            if (scan.charAt(i) == '#') {
                layout |= 1 << i;
            }
        }
        return layout;
    }
}
