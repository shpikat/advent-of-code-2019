package com.shpikat.adventofcode2019.day10;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.List;

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

            max = Math.max(max, mapCopy.cardinality());
            if (candidate == Integer.MAX_VALUE) {
                break; // or (candidate+1) would overflow
            }

        }
        System.out.println(max);
    }

    private static int getGcd(final int n1, final int n2) {
        return n2 == 0 ? n1 : getGcd(n2, n1 % n2);
    }
}
