package com.shpikat.adventofcode2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day12 {

    private static final int N_COORDINATES = 3;

    private static final Pattern pattern = Pattern.compile("^<x=([^,]+), y=([^,]+), z=([^>]+)>$");

    static class Part1 {

        static long solve(final String input, final int steps) {
            final List<Moon> moons = new ArrayList<>();
            for (final String line : input.split("\n")) {
                final Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    final int[] position = new int[N_COORDINATES];
                    for (int i = 0; i < position.length; i++) {
                        position[i] = Integer.parseInt(matcher.group(i + 1));
                    }
                    moons.add(new Moon(position));
                }
            }

            for (int step = 0; step < steps; step++) {
                for (int i = 0; i < moons.size() - 1; i++) {
                    for (int j = i + 1; j < moons.size(); j++) {
                        Moon.applyGravity(moons.get(i), moons.get(j));
                    }
                }

                for (final Moon moon : moons) {
                    moon.applyVelocity();
                }
            }

            return moons.stream()
                    .mapToInt(Moon::getTotalEnergy)
                    .sum();
        }

        private static class Moon {
            private final int[] position;
            private final int[] velocity = new int[N_COORDINATES];

            Moon(final int[] position) {
                assert position.length == N_COORDINATES;
                this.position = position;
            }

            void applyVelocity() {
                for (int i = 0; i < N_COORDINATES; i++) {
                    position[i] += velocity[i];
                }
            }

            int getTotalEnergy() {
                int potential = 0;
                int kinetic = 0;
                for (int i = 0; i < N_COORDINATES; i++) {
                    potential += Math.abs(position[i]);
                    kinetic += Math.abs(velocity[i]);
                }
                return potential * kinetic;
            }

            static void applyGravity(final Moon moon1, final Moon moon2) {
                for (int i = 0; i < N_COORDINATES; i++) {
                    final int deltaX = Integer.compare(moon1.position[i], moon2.position[i]);
                    moon1.velocity[i] -= deltaX;
                    moon2.velocity[i] += deltaX;
                }
            }
        }
    }

    static class Part2 {
        static long solve(final String input) {
            final String[] allLines = input.split("\n");
            final int nMoons = allLines.length;
            final int[][] coordinates = new int[N_COORDINATES][nMoons];

            for (int i = 0; i < allLines.length; i++) {
                final Matcher matcher = pattern.matcher(allLines[i]);
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

            return getLcm(steps);
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
}
