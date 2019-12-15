package com.shpikat.adventofcode2019.day12;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final int N_COORDINATES = 3;

    private static final Pattern pattern = Pattern.compile("^<x=([^,]+), y=([^,]+), z=([^>]+)>$");

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());

        final List<String> allLines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
        final int nMoons = allLines.size();
        final int[][] coordinates = new int[N_COORDINATES][nMoons];

        for (int i = 0; i < allLines.size(); i++) {
            final Matcher matcher = pattern.matcher(allLines.get(i));
            if (matcher.matches()) {
                for (int j = 0; j < N_COORDINATES; j++) {
                    coordinates[j][i] = Integer.parseInt(matcher.group(j + 1));
                }
            }
        }

        final long[] steps = new long[N_COORDINATES];
        for (int coordinate = 0; coordinate < steps.length; coordinate++) {
            final int[] original = coordinates[coordinate].clone();
            final int[] position = original.clone();
            final int[] velocity = new int[position.length];
            final int[] empty = new int[velocity.length];

            do {
                steps[coordinate]++;
                for (int i = 0; i < position.length - 1; i++) {
                    for (int j = i + 1; j < position.length; j++) {
                        final int deltaX = Integer.compare(position[i], position[j]);
                        velocity[i] -= deltaX;
                        velocity[j] += deltaX;
                    }
                }

                for (int i = 0; i < position.length; i++) {
                    position[i] += velocity[i];
                }
            } while (!(Arrays.equals(velocity, empty) && Arrays.equals(position, original)));
        }

        System.out.println(getLcm(steps));
    }


    private static long getLcm(final long[] n) {
        long lcm = n[0];
        for (int i = 1; i < n.length; i++) {
            final long step = n[i];
            //noinspection SuspiciousIntegerDivAssignment
            lcm *= step / getGcd(lcm, step);
        }
        return lcm;
    }

    private static long getGcd(final long n1, final long n2) {
        return n2 == 0 ? n1 : getGcd(n2, n1 % n2);
    }
}
