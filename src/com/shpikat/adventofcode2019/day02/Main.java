package com.shpikat.adventofcode2019.day02;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(path, StandardCharsets.ISO_8859_1).trim().split(",");
        final int[] source = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            source[i] = Integer.parseInt(strings[i]);
        }

        out:
        for (int noun = 0; noun <= 99; noun++) {
            for (int verb = 0; verb <= 99; verb++) {
                final int[] program = source.clone();
                program[1] = noun;
                program[2] = verb;
                for (int ip = 0; program[ip] != 99; ip += 4) {
                    final int a = program[program[ip + 1]];
                    final int b = program[program[ip + 2]];
                    final int c;
                    switch (program[ip]) {
                        case 1:
                            c = a + b;
                            break;
                        case 2:
                            c = a * b;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + program[ip]);
                    }
                    program[program[ip + 3]] = c;

                }
                if (program[0] == 19690720) {
                    System.out.println(100 * noun + verb);
                    break out;
                }
            }
        }
    }
}
