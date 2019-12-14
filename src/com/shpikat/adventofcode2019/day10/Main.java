package com.shpikat.adventofcode2019.day10;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.Math.PI;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());

        final List<String> allLines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
        final int width = allLines.get(0).length();
        final int height = allLines.size();
        final BitSet sourceMap = new BitSet(allLines.size() * width);

        int offset = 0;
        for (final String line : allLines) {
            for (int i = line.indexOf('#'); i >= 0; i = line.indexOf('#', i + 1)) {
                sourceMap.set(offset + i);
            }
            offset += width;
        }

        int max = 0;
        int bestX = 0;
        int bestY = 0;
        for (int candidate = sourceMap.nextSetBit(0); candidate >= 0; candidate = sourceMap.nextSetBit(candidate + 1)) {
            final int x1 = candidate % width;
            final int y1 = candidate / width;

            final BitSet mapCopy = (BitSet) sourceMap.clone();
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

            final int current = mapCopy.cardinality();
            if (current > max) {
                max = current;
                bestX = x1;
                bestY = y1;
            }
            if (candidate == Integer.MAX_VALUE) {
                break; // or (candidate+1) would overflow
            }
        }

        // Prepare the laser!
        sourceMap.clear(bestY * width + bestX);
        final SortedMap<Double, Queue<Asteroid>> thetas = new TreeMap<>();
        for (int asteroid = sourceMap.nextSetBit(0); asteroid >= 0; asteroid = sourceMap.nextSetBit(asteroid + 1)) {
            final int x = asteroid % width;
            final int y = asteroid / width;

            final int deltaX = bestX - x;
            final int deltaY = bestY - y;

            final double theta = adjustStartingPosition(Math.atan2(deltaY, deltaX));

            thetas.computeIfAbsent(theta, key -> new PriorityQueue<>(Asteroid.byDistance))
                    .add(new Asteroid(x, y, deltaX * deltaX + deltaY * deltaY));
        }

        int count = 0;
        final int limit = 200;
        do {
            for (final Queue<Asteroid> asteroids : thetas.values()) {
                final Asteroid asteroid = asteroids.poll();
                if (asteroid != null && ++count == limit) {
                    System.out.println(asteroid.x * 100 + asteroid.y);
                    break;
                }

            }
        } while (count < limit);

    }

    private static int getGcd(final int n1, final int n2) {
        return n2 == 0 ? n1 : getGcd(n2, n1 % n2);
    }

    private static double adjustStartingPosition(final double angle) {
        return angle >= 0.5 * PI ? angle - 2 * PI : angle;
    }

    private static class Asteroid {
        private static final Comparator<Asteroid> byDistance = Comparator.comparing(Asteroid::getDistanceSquared);

        private final int x;
        private final int y;
        private final int distanceSquared;

        private Asteroid(final int x, final int y, final int distanceSquared) {
            this.x = x;
            this.y = y;
            this.distanceSquared = distanceSquared;
        }

        private int getDistanceSquared() {
            return distanceSquared;
        }
    }
}
