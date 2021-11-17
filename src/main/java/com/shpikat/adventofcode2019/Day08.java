package com.shpikat.adventofcode2019;

import java.util.Arrays;

public class Day08 {
    static class Part2 {
        static String solve(final String input, final int width, final int height) {
            final char[] data = input.toCharArray();

            final int[] image = new int[width * height];
            Arrays.fill(image, 2);
            for (int layer = 0; layer < data.length; layer += width * height) {
                for (int i = 0; i < width * height; i++) {
                    if (image[i] == 2) {
                        image[i] = data[layer + i] - '0';
                    }
                }
            }

            final StringBuilder builder = new StringBuilder((width + 1) * height);
            for (int i = 0; i < width * height; i += width) {
                for (int j = i; j < i + width; j++) {
                    builder.append(image[j] != 0 ? '$' : ' ');
                }
                builder.append('\n');
            }

            return builder.toString();
        }
    }
}
