package com.shpikat.adventofcode2019.day12;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final int N = 3;

    private static final Pattern pattern = Pattern.compile("^<x=([^,]+), y=([^,]+), z=([^>]+)>$");

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());

        final List<Moon> moons = new ArrayList<>();
        for (final String line : Files.readAllLines(path, StandardCharsets.ISO_8859_1)) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                final int[] position = new int[N];
                for (int i = 0; i < position.length; i++) {
                    position[i] = Integer.parseInt(matcher.group(i + 1));
                }
                moons.add(new Moon(position));
            }
        }

        final int nSteps = 1000;

        for (int step = 0; step < nSteps; step++) {
            for (int i = 0; i < moons.size() - 1; i++) {
                for (int j = i + 1; j < moons.size(); j++) {
                    Moon.applyGravity(moons.get(i), moons.get(j));
                }
            }

            for (final Moon moon : moons) {
                moon.applyVelocity();
            }
        }

        final int totalEnergy = moons.stream()
                .mapToInt(Moon::getTotalEnergy)
                .sum();
        System.out.println(totalEnergy);

    }

    private static class Moon {
        private final int[] position;
        private final int[] velocity = new int[N];

        Moon(final int[] position) {
            assert position.length == N;
            this.position = position;
        }

        void applyVelocity() {
            for (int i = 0; i < N; i++) {
                position[i] += velocity[i];
            }
        }

        int getTotalEnergy() {
            int potential = 0;
            int kinetic = 0;
            for (int i = 0; i < N; i++) {
                potential += Math.abs(position[i]);
                kinetic += Math.abs(velocity[i]);
            }
            return potential * kinetic;
        }

        static void applyGravity(final Moon moon1, final Moon moon2) {
            for (int i = 0; i < N; i++) {
                final int deltaX = Integer.compare(moon1.position[i], moon2.position[i]);
                moon1.velocity[i] -= deltaX;
                moon2.velocity[i] += deltaX;
            }
        }
    }
}
