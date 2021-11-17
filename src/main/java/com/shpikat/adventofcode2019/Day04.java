package com.shpikat.adventofcode2019;

import java.util.Arrays;

public class Day04 {
    static class Part2 {
        static int solve(final String input) {
            final String[] range = input.split("-");
            final char[] start = range[0].toCharArray();
            final char[] end = range[1].toCharArray();

            final char[] password = start.clone();

            for (int i = 1; i < password.length; i++) {
                if (password[i] < password[i - 1]) {
                    Arrays.fill(password, i, password.length, password[i - 1]);
                    break;
                }
            }

            int i = password.length - 1;
            int count = 0;
            while (Arrays.compare(password, end) <= 0 && i >= 0) {
                if (isPasswordGood(password)) {
                    ++count;
                }
                final int last = i;
                do {
                    password[i]++;
                } while (password[i] > '9' && --i >= 0);
                if (last != i && i >= 0) {
                    Arrays.fill(password, i + 1, password.length, password[i]);
                }
                i = password.length - 1;
            }

            return count;
        }

        static boolean isPasswordGood(final char[] password) {
            boolean hasTwoSameAdjacentDigits = false;
            boolean isIncreasing = true;
            for (int i = 1; i < password.length && isIncreasing; i++) {
                hasTwoSameAdjacentDigits |= password[i - 1] == password[i]
                        && (i == 1 || password[i - 2] != password[i])
                        && (i == password.length - 1 || password[i + 1] != password[i]);
                isIncreasing = password[i - 1] <= password[i];
            }
            return hasTwoSameAdjacentDigits && isIncreasing;
        }
    }
}
