package com.shpikat.adventofcode2019.day03;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.Math.min;

public class Main {


    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final List<String> allLines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
        final String[] first = allLines.get(0).split(",");
        final String[] second = allLines.get(1).split(",");

        final int row = 2 << 14;
        final int xCentral = row / 2;
        final int yCentral = row / 2;

        final short[][] grid = new short[row][row];

        int x = 0;
        int y = 0;
        short step = 0;
        for (final String command : first) {
            final int distance = Integer.parseInt(command.substring(1));
            switch (command.charAt(0)) {
                case 'U':
                    for (int i = 1; i <= distance; i++) {
                        setIfEmpty(grid, yCentral + y + i, xCentral + x, ++step);
                    }
                    y += distance;
                    break;
                case 'D':
                    for (int i = 1; i <= distance; i++) {
                        setIfEmpty(grid, yCentral + y - i, xCentral + x, ++step);
                    }
                    y -= distance;
                    break;
                case 'L':
                    for (int i = 1; i <= distance; i++) {
                        setIfEmpty(grid, yCentral + y, xCentral + x - i, ++step);
                    }
                    x -= distance;
                    break;
                case 'R':
                    for (int i = 1; i <= distance; i++) {
                        setIfEmpty(grid, yCentral + y, xCentral + x + i, ++step);
                    }
                    x += distance;
                    break;
            }
        }

        grid[yCentral][xCentral] = 0;

        x = 0;
        y = 0;
        int step2 = 0;
        int fastest = Short.MAX_VALUE;
        for (final String command : second) {
            final int distance = Integer.parseInt(command.substring(1));
            switch (command.charAt(0)) {
                case 'U':
                    for (int i = 1; i <= distance; i++) {
                        final short steps1 = grid[yCentral + y + i][xCentral + x];
                        ++step2;
                        if (steps1 != 0) {
                            fastest = min(fastest, steps1 + step2);
                        }
                    }
                    y += distance;
                    break;
                case 'D':
                    for (int i = 1; i <= distance; i++) {
                        final short steps1 = grid[yCentral + y - i][xCentral + x];
                        ++step2;
                        if (steps1 != 0) {
                            fastest = min(fastest, steps1 + step2);
                        }
                    }
                    y -= distance;
                    break;
                case 'L':
                    for (int i = 1; i <= distance; i++) {
                        final short steps1 = grid[yCentral + y][xCentral + x - i];
                        ++step2;
                        if (steps1 != 0) {
                            fastest = min(fastest, steps1 + step2);
                        }
                    }
                    x -= distance;
                    break;
                case 'R':
                    for (int i = 1; i <= distance; i++) {
                        final short steps1 = grid[yCentral + y][xCentral + x + i];
                        ++step2;
                        if (steps1 != 0) {
                            fastest = min(fastest, steps1 + step2);
                        }
                    }
                    x += distance;
                    break;
            }
        }

        System.out.println(fastest);
    }

    private static void setIfEmpty(final short[][] grid, final int y, final int x, short step) {
        grid[y][x] = grid[y][x] == 0 ? step : grid[y][x];
    }
}
