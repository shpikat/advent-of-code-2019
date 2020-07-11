package com.shpikat.adventofcode2019.day16;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path inputPath = Paths.get(Main.class.getResource("input.txt").toURI());
        final String input = Files.readString(inputPath, StandardCharsets.ISO_8859_1).trim();
        int[] data = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            data[i] = input.charAt(i) - '0';
        }
        final int[] basePattern = {0, 1, 0, -1};
        final int basePatternLength = basePattern.length;
        final int nPhases = 100;

        for (int phase = 0; phase < nPhases; phase++) {
            final int[] newData = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                final int patternRepeat = i + 1;
                final int patternPeriod = basePatternLength * patternRepeat;

                final int plusOne = applyPattern(data, patternRepeat - 1, patternRepeat, patternPeriod);
                final int minusOne = applyPattern(data, (patternRepeat * 3) - 1, patternRepeat, patternPeriod);

                newData[i] = Math.abs(plusOne - minusOne) % 10;
            }
            data = newData;
        }

        print(data);
    }

    private static int applyPattern(final int[] data, final int offset, final int repeat, final int period) {
        int sum = 0;
        for (int i = offset; i < data.length; i += period) {
            for (int j = i; j < i + repeat && j < data.length; j++) {
                sum += data[j];
            }
        }
        return sum;
    }

    private static void print(final int[] data) {
        for (int i = 0; i < 8; i++) {
            final int digit = data[i];
            System.out.print(digit);
        }
        System.out.println();
    }
}
