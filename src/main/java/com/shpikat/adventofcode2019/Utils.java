package com.shpikat.adventofcode2019;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

class Utils {
    static String readInput(final String filename) throws IOException {
        return new String(
                requireNonNull(Utils.class.getResourceAsStream("/" + filename)).readAllBytes(),
                StandardCharsets.ISO_8859_1
        ).trim();
    }
}
