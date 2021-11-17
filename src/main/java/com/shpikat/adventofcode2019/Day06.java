package com.shpikat.adventofcode2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day06 {
    static class Part2 {
        static int solve(final String input) {
            final Map<String, String> orbits = Arrays
                    .stream(input.split("\n"))
                    .map(line -> line.split("\\)"))
                    .collect(Collectors.toMap(tuple -> tuple[1], tuple -> tuple[0]));

            final Object[] pathFromYou = getPathToCom(orbits, "YOU");
            final Object[] pathFromSan = getPathToCom(orbits, "SAN");

            final int commonObject = Arrays.mismatch(pathFromYou, pathFromSan) + 1;

            return pathFromYou.length - commonObject + pathFromSan.length - commonObject;
        }

        private static Object[] getPathToCom(final Map<String, String> orbits, final String start) {
            final List<String> path = new ArrayList<>();
            String localOrbit = start;
            do {
                path.add(localOrbit);
                localOrbit = orbits.get(localOrbit);
            } while (!localOrbit.equals("COM"));
            Collections.reverse(path);
            // We know it is actually an array of strings, but we just don't care, and this method is tiny tad faster
            return path.toArray();
        }
    }
}
