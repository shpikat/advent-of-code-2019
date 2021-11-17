package com.shpikat.adventofcode2019;

public class Day16 {

    public static final int[] BASE_PATTERN = new int[]{0, 1, 0, -1};

    static class Part1 {
        static String solve(final String input, final int nPhases) {
            int[] data = new int[input.length()];
            for (int i = 0; i < input.length(); i++) {
                data[i] = input.charAt(i) - '0';
            }

            for (int phase = 0; phase < nPhases; phase++) {
                final int[] newData = new int[data.length];
                for (int i = 0; i < data.length; i++) {
                    applyPattern(data, newData, i);
                }
                data = newData;
            }

            return toStringOf8(data, 0);
        }
    }

    static class Part2 {
        static String solve(final String input) {
            final int offset = Integer.parseInt(input.substring(0, 7));
            int replicationFactor = 10_000;
            int[] data = new int[input.length() * replicationFactor];
            for (int i = 0; i < input.length(); i++) {
                data[i] = input.charAt(i) - '0';
            }
            for (int i = 1; i < replicationFactor; i++) {
                System.arraycopy(data, 0, data, input.length() * i, input.length());
            }
            final int nPhases = 100;
            for (int phase = 0; phase < nPhases; phase++) {
                final int[] newData = new int[data.length];
                final int middle = data.length / 2 + 1;
                for (int i = offset; i < middle; i++) {
                    applyPattern(data, newData, i);
                }

                int sum = 0;
                for (int i = middle - 1; i < data.length; i++) {
                    sum += data[i];
                }

                for (int i = middle; i < data.length; i++) {
                    sum -= data[i - 1];
                    newData[i] = sum % 10;
                }
                data = newData;
            }

            return toStringOf8(data, offset);
        }
    }

    private static void applyPattern(final int[] data, final int[] newData, final int position) {
        final int patternRepeat = position + 1;
        final int patternPeriod = BASE_PATTERN.length * patternRepeat;

        final int plusOne = calculateValue(data, patternRepeat - 1, patternRepeat, patternPeriod);
        final int minusOne = calculateValue(data, (patternRepeat * 3) - 1, patternRepeat, patternPeriod);

        newData[position] = Math.abs(plusOne - minusOne) % 10;
    }

    private static int calculateValue(final int[] data, final int offset, final int repeat, final int period) {
        int sum = 0;
        for (int i = offset; i < data.length; i += period) {
            for (int j = i; j < i + repeat && j < data.length; j++) {
                sum += data[j];
            }
        }
        return sum;
    }

    private static String toStringOf8(final int[] data, final int offset) {
        final StringBuilder builder = new StringBuilder(8);
        for (int i = offset; i < offset + 8; i++) {
            final int digit = data[i];
            builder.append(digit);
        }
        return builder.toString();
    }
}
