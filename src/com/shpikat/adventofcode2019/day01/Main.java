package com.shpikat.adventofcode2019.day01;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        int sum = 0;
        for (String line : Files.readAllLines(path, StandardCharsets.ISO_8859_1)) {
            int mass = Integer.parseInt(line);
            int fuel = mass / 3 - 2;
            while (fuel > 0) {
                sum += fuel;
                fuel = fuel / 3 - 2;
            }
        }

        System.out.println(sum);
    }
}
