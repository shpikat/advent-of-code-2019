package com.shpikat.adventofcode2019;

public class Day01 {

    static class Part2 {
        static int solve(final String input) {
            int sum = 0;
            for (String line : input.split("\n")) {
                int mass = Integer.parseInt(line);
                sum += calculateFuel(mass);
            }

            return sum;
        }

        static int calculateFuel(final int mass) {
            int sum = 0;
            int fuel = mass / 3 - 2;
            while (fuel > 0) {
                sum += fuel;
                fuel = fuel / 3 - 2;
            }
            return sum;
        }
    }
}
