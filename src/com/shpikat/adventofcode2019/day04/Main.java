package com.shpikat.adventofcode2019.day04;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] input = Files.readString(path, StandardCharsets.ISO_8859_1).split("-");
        final char[] start = input[0].toCharArray();
        final char[] end = input[1].toCharArray();

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

        System.out.println(count);
    }

    private static boolean isPasswordGood(final char[] password) {
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
