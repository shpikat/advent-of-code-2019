package com.shpikat.adventofcode2019;

import java.util.BitSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.Math.PI;

public class Day10 {
    static class Part1 {
        static int solve(final String input) {
            final AsteroidMap map = AsteroidMap.fromString(input);

            final FindMaximum findMaximum = new FindMaximum();
            map.detectAsteroids(findMaximum);
            return findMaximum.max;
        }

        private static class FindMaximum implements Accumulator {
            private int max = 0;

            @Override
            public void accept(final int count, final int candidate) {
                max = Math.max(max, count);
            }
        }
    }

    static class Part2 {
        static int solve(final String input) {
            final AsteroidMap map = AsteroidMap.fromString(input);

            final FindBestLocation findBest = new FindBestLocation();
            map.detectAsteroids(findBest);
            return map.vaporize(findBest.location, 200);
        }

        private static class FindBestLocation implements Accumulator {
            private int max = 0;
            private int location = 0;

            @Override
            public void accept(final int count, final int candidate) {
                if (count > max) {
                    max = count;
                    location = candidate;
                }
            }
        }
    }

    private static class AsteroidMap {
        private final BitSet map;
        private final int width;
        private final int height;

        private AsteroidMap(final BitSet map, final int width, final int height) {
            this.map = map;
            this.width = width;
            this.height = height;
        }

        public static AsteroidMap fromString(final String input) {
            final String[] allLines = input.split("\n");
            final int width = allLines[0].length();
            final int height = allLines.length;
            final BitSet sourceMap = new BitSet(allLines.length * width);

            int offset = 0;
            for (final String line : allLines) {
                for (int i = line.indexOf('#'); i >= 0; i = line.indexOf('#', i + 1)) {
                    sourceMap.set(offset + i);
                }
                offset += width;
            }
            return new AsteroidMap(sourceMap, width, height);
        }

        public void detectAsteroids(final Accumulator accumulator) {
            for (int candidate = map.nextSetBit(0); candidate >= 0; candidate = map.nextSetBit(candidate + 1)) {
                final int x1 = candidate % width;
                final int y1 = candidate / width;

                final BitSet mapCopy = (BitSet) map.clone();
                mapCopy.clear(candidate);

                for (int asteroid = mapCopy.nextSetBit(0); asteroid >= 0; asteroid = mapCopy.nextSetBit(asteroid + 1)) {
                    final int x2 = asteroid % width;
                    final int y2 = asteroid / width;

                    if (x1 != x2) {
                        final int deltaY = y2 - y1;
                        final int deltaX = x2 - x1;
                        final int gcd = Math.abs(getGcd(deltaX, deltaY));
                        final int stepY = deltaY / gcd;
                        final int stepX = deltaX / gcd;

                        for (int x = x2 + stepX, y = y2 + stepY; x >= 0 && x < width && y >= 0 && y < height; x += stepX, y += stepY) {
                            mapCopy.clear(y * width + x);
                        }
                    } else if (y2 < y1) {
                        // clear the column from 0 to y2
                        for (int bitIndex = x1; bitIndex < asteroid; bitIndex += width) {
                            mapCopy.clear(bitIndex);
                        }
                    } else {
                        // clear the column from y2 to the end
                        for (int bitIndex = asteroid + width; bitIndex < mapCopy.size(); bitIndex += width) {
                            mapCopy.clear(bitIndex);
                        }
                    }

                    if (asteroid == Integer.MAX_VALUE) {
                        break; // or (asteroid+1) would overflow
                    }
                }

                accumulator.accept(mapCopy.cardinality(), candidate);

                if (candidate == Integer.MAX_VALUE) {
                    break; // or (candidate+1) would overflow
                }
            }

        }

        public int vaporize(final int location, final int limit) {
            final BitSet mapCopy = (BitSet) map.clone();
            mapCopy.clear(location);
            final int centerX = location % width;
            final int centerY = location / width;
            final SortedMap<Double, Queue<Asteroid>> thetas = new TreeMap<>();
            for (int asteroid = mapCopy.nextSetBit(0); asteroid >= 0; asteroid = mapCopy.nextSetBit(asteroid + 1)) {
                final int x = asteroid % width;
                final int y = asteroid / width;

                final int deltaX = centerX - x;
                final int deltaY = centerY - y;

                final double theta = adjustStartingPosition(Math.atan2(deltaY, deltaX));

                thetas.computeIfAbsent(theta, key -> new PriorityQueue<>(Asteroid.byDistance))
                        .add(new Asteroid(x, y, deltaX * deltaX + deltaY * deltaY));
            }

            int count = 0;
            do {
                for (final Queue<Asteroid> asteroids : thetas.values()) {
                    final Asteroid asteroid = asteroids.poll();
                    if (asteroid != null && ++count == limit) {
                        return asteroid.x * 100 + asteroid.y;
                    }
                }
            } while (count < limit);

            throw new IllegalStateException("No 200th asteroid to vaporize");
        }

        private static int getGcd(final int n1, final int n2) {
            return n2 == 0 ? n1 : getGcd(n2, n1 % n2);
        }

        private static double adjustStartingPosition(final double angle) {
            return angle >= 0.5 * PI ? angle - 2 * PI : angle;
        }

        private record Asteroid(int x, int y, int distanceSquared) {
            private static final Comparator<Asteroid> byDistance = Comparator.comparing(Asteroid::distanceSquared);
        }
    }

    @FunctionalInterface
    private interface Accumulator {
        void accept(int count, int candidate);
    }
}
