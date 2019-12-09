package com.shpikat.adventofcode2019.day06;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());

        final Map<String, String> orbits = Files.readAllLines(path, StandardCharsets.ISO_8859_1)
                .stream()
                .map(line -> line.split("\\)"))
                .collect(Collectors.toMap(tuple -> tuple[1], tuple -> tuple[0]));

        final Object[] pathFromYou = getPathToCom(orbits, "YOU");
        final Object[] pathFromSan = getPathToCom(orbits, "SAN");

        final int commonObject = Arrays.mismatch(pathFromYou, pathFromSan) + 1;

        System.out.println(pathFromYou.length - commonObject + pathFromSan.length - commonObject);
    }

    private static Object[] getPathToCom(final Map<String, String> orbits, final String start) {
        final List<String> path = new ArrayList<>();
        String localOrbit = start;
        do {
            path.add(localOrbit);
            localOrbit = orbits.get(localOrbit);
        } while (!localOrbit.equals("COM"));
        Collections.reverse(path);
        return path.toArray();
    }
}
