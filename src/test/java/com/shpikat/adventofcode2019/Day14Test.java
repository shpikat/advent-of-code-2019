package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Day 14")
public class Day14Test {

    private static final Named<String> sample13312 = named("13312 ORE-per-FUEL example", """
            157 ORE => 5 NZVS
            165 ORE => 6 DCFZ
            44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
            12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
            179 ORE => 7 PSHF
            177 ORE => 5 HKGWZ
            7 DCFZ, 7 PSHF => 2 XJWVT
            165 ORE => 2 GPVTF
            3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT
            """);
    private static final Named<String> sample180697 = named("180697 ORE-per-FUEL example", """
            2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG
            17 NVRVD, 3 JNWZP => 8 VPVL
            53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
            22 VJHF, 37 MNCFX => 5 FWMGM
            139 ORE => 4 NVRVD
            144 ORE => 7 JNWZP
            5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC
            5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV
            145 ORE => 6 MNCFX
            1 NVRVD => 8 CXFTF
            1 VJHF, 6 MNCFX => 4 RFSQX
            176 ORE => 6 VJHF
            """);
    private static final Named<String> sample2210736 = named("2210736 ORE-per-FUEL example", """
            171 ORE => 8 CNZTR
            7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
            114 ORE => 4 BHXH
            14 VRPVC => 6 BMBT
            6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
            6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
            15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
            13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
            5 BMBT => 4 WPTQ
            189 ORE => 9 KTJDG
            1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
            12 VRPVC, 27 CNZTR => 2 XDBXC
            15 KTJDG, 12 BHXH => 5 XCVML
            3 BHXH, 2 VRPVC => 7 MZWV
            121 ORE => 7 VRPVC
            7 XCVML => 6 RJRHP
            5 BHXH, 4 VRPVC => 5 LTCX
            """);

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the minimum amount of ORE required to produce exactly 1 FUEL")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, long answer) {
            assertEquals(answer, Day14.Part1.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", """
                            10 ORE => 10 A
                            1 ORE => 1 B
                            7 A, 1 B => 1 C
                            7 A, 1 C => 1 D
                            7 A, 1 D => 1 E
                            7 A, 1 E => 1 FUEL
                            """), 31),
                    arguments(named("sample 2", """
                            9 ORE => 2 A
                            8 ORE => 3 B
                            7 ORE => 5 C
                            3 A, 4 B => 1 AB
                            5 B, 7 C => 1 BC
                            4 C, 1 A => 1 CA
                            2 AB, 3 BC, 4 CA => 1 FUEL
                            """), 165),
                    arguments(sample13312, 13312),
                    arguments(sample180697, 180697),
                    arguments(sample2210736, 2210736),
                    arguments(named("puzzle input", readInput("day14_input.txt")), 220019)
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the maximum amount of FUEL you can produce from 1 trillion units of ORE")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, long answer) {
            assertEquals(answer, Day14.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(sample13312, 82892753),
                    arguments(sample180697, 5586022),
                    arguments(sample2210736, 460664),
                    arguments(named("puzzle input", readInput("day14_input.txt")), 5650230)
            );
        }
    }
}
