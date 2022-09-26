package com.shpikat.adventofcode2019;

import java.math.BigInteger;

public class Day22 {

    private static final String DEAL_INTO_NEW_STACK = "deal into new stack";
    private static final String PREFIX_CUT = "cut ";
    private static final String PREFIX_DEAL_WITH_INCREMENT = "deal with increment ";
    private static final int LENGTH_PREFIX_CUT = PREFIX_CUT.length();
    private static final int LENGTH_PREFIX_DEAL_WITH_INCREMENT = PREFIX_DEAL_WITH_INCREMENT.length();

    static class Part1 {

        private static final int DECK_SIZE = 10_007;

        static int solve(final String input) {

            // Simple commonsense solution, no modular arithmetics is known
            int card = 2019;
            for (final String line : input.split("\n")) {
                if (line.equals(DEAL_INTO_NEW_STACK)) {
                    card = DECK_SIZE - card - 1;
                } else if (line.startsWith(PREFIX_CUT)) {
                    final int number = Integer.parseInt(line.substring(LENGTH_PREFIX_CUT));
                    card = (card - number + DECK_SIZE) % DECK_SIZE;
                } else if (line.startsWith(PREFIX_DEAL_WITH_INCREMENT)) {
                    final int increment = Integer.parseInt(line.substring(LENGTH_PREFIX_DEAL_WITH_INCREMENT));
                    card = card * increment % DECK_SIZE;
                }
            }

            return card;
        }
    }

    static class Part2 {

        private static final long DECK_SIZE = 119_315_717_514_047L;
        private static final long REPEAT_TIMES = 101_741_582_076_661L;

        static long solve(final String input) {
            final BigInteger deckSize = toBigIntegerCheckingIsPrime(DECK_SIZE);
            final BigInteger n = toBigIntegerCheckingIsPrime(REPEAT_TIMES);

            // Here go modular arithmetics, represent card as (a*card + b) mod DECK_SIZE
            long a = 1;
            long b = 0;
            for (final String line : input.split("\n")) {
                if (line.equals(DEAL_INTO_NEW_STACK)) {
                    a = -a % DECK_SIZE;
                    b = (DECK_SIZE - b - 1) % DECK_SIZE;
                } else if (line.startsWith(PREFIX_CUT)) {
                    final int number = Integer.parseInt(line.substring(LENGTH_PREFIX_CUT));
                    b = (b - number) % DECK_SIZE;
                } else if (line.startsWith(PREFIX_DEAL_WITH_INCREMENT)) {
                    final int increment = Integer.parseInt(line.substring(LENGTH_PREFIX_DEAL_WITH_INCREMENT));
                    // Explicitly guard against large numbers multiplication overflows silently
                    a = Math.multiplyExact(a, increment) % DECK_SIZE;
                    b = Math.multiplyExact(b, increment) % DECK_SIZE;
                }
            }

            // Repeating n times gives: position === (a^n * card + (a^(n-1) + a^(n-2) + ... + 1) * b) mod DECK_SIZE
            //
            // The right part can be replaced with the geometric sum: b * (1 - a^n) / (1-a) (also we know n != 1)
            //
            // position === (a^n * card + ((1 - a^n) / (1-a)) * b) mod DECK_SIZE
            //
            // card === (a^-n * (position - (1-a)^-1 * b) + (1-a)^-1 * b) mod DECK_SIZE
            //
            // To calculate the card from the position, modular multiplicative inverse of a modulo m must be found.
            // This is usually done using extended Euclidean algorithm. Although as the modulo is prime in our case,
            // the Euler's theorem may be used, where phi(m)=m-1. That should be even faster.
            //
            // See https://en.wikipedia.org/wiki/Modular_multiplicative_inverse#Using_Euler's_theorem for more details
            // and formulas.
            //
            // a^-1===a^(phi(m)-1) mod m => a^-1===a^(m-2) mod m and a^-n===a^(n*(m-2)) mod m

            final BigInteger phiMinusOne = BigInteger.valueOf(DECK_SIZE - 2);
            final long rightPart = BigInteger.valueOf(1 - a).modPow(phiMinusOne, deckSize)
                    .multiply(BigInteger.valueOf(b)).mod(deckSize)
                    .longValueExact();
            return (BigInteger.valueOf(a).modPow(n.multiply(phiMinusOne), deckSize)
                    .multiply(BigInteger.valueOf(2020 - rightPart))
                    .mod(deckSize)
                    .longValueExact()
                    + rightPart) % DECK_SIZE;
        }

        private static BigInteger toBigIntegerCheckingIsPrime(final long val) {
            final BigInteger big = BigInteger.valueOf(val);

            // In fact will be way fewer rounds
            if (!big.isProbablePrime(Integer.MAX_VALUE)) {
                throw new IllegalArgumentException("Must be prime: " + val);
            }

            return big;
        }
    }
}
