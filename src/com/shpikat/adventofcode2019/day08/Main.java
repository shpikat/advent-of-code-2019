package com.shpikat.adventofcode2019.day08;

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

        final char[] data = Files.readString(path, StandardCharsets.ISO_8859_1).trim().toCharArray();

        final int width = 25;
        final int height = 6;

        final int[] image = new int[width * height];
        Arrays.fill(image, 2);
        for (int layer = 0; layer < data.length; layer += width * height) {
            for (int i = 0; i < width * height; i++) {
                if (image[i] == 2) {
                    image[i] = data[layer + i] - '0';
                }
            }
        }

        for (int i = 0; i < width * height; i += width) {
            for (int j = 0; j < width; j++) {
                System.out.print(image[i + j] != 0 ? '@' : ' ');
            }
            System.out.println();
        }
    }
}
