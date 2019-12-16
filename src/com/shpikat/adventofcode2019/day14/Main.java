package com.shpikat.adventofcode2019.day14;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern pattern = Pattern.compile("(\\d+) ([A-Z]+)");

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());

        final Map<String, Reaction> reactions = new HashMap<>();

        for (final String line : Files.readAllLines(path, StandardCharsets.ISO_8859_1)) {
            final Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                throw new IllegalStateException("No material in line " + line);
            }
            final Collection<ReactionPart> input = new ArrayList<>();
            ReactionPart part = getReactionPart(matcher);
            while (matcher.find(matcher.end())) {
                input.add(part);
                // the last one will be the output
                part = getReactionPart(matcher);
            }

            reactions.put(part.material, new Reaction(part.amount, input));
        }

        final Map<String, Integer> materialRequests = new HashMap<>();
        final Map<String, Integer> leftOvers = new HashMap<>();
        materialRequests.put("FUEL", 1);
        int amountOre = 0;
        while (!materialRequests.isEmpty()) {
            final Iterator<Map.Entry<String, Integer>> iterator = materialRequests.entrySet().iterator();
            final Map.Entry<String, Integer> request = iterator.next();
            iterator.remove();
            final String material = request.getKey();
            final int amountNeeded = request.getValue();
            final int currentLeftOver = leftOvers.getOrDefault(material, 0);
            if (amountNeeded <= currentLeftOver) {
                leftOvers.put(material, currentLeftOver - amountNeeded);
            } else {
                final int amountStillNeeded = amountNeeded - currentLeftOver;
                final Reaction reaction = reactions.get(material);
                final int nReactionsRequired = (int) Math.ceil((double) amountStillNeeded / reaction.outputAmount);
                leftOvers.put(material, nReactionsRequired * reaction.outputAmount - amountStillNeeded);
                for (final ReactionPart reactionPart : reaction.input) {
                    if (reactionPart.material.equals("ORE")) {
                        amountOre += nReactionsRequired * reactionPart.amount;
                    } else {
                        materialRequests.merge(reactionPart.material, nReactionsRequired * reactionPart.amount, Integer::sum);
                    }
                }
            }
        }

        System.out.println(amountOre);
    }

    private static ReactionPart getReactionPart(final Matcher matcher) {
        final int amount = Integer.parseInt(matcher.group(1));
        final String material = matcher.group(2);
        return new ReactionPart(amount, material);
    }

    private static class Reaction {
        final int outputAmount;
        final Collection<ReactionPart> input;

        private Reaction(final int outputAmount, final Collection<ReactionPart> input) {
            this.outputAmount = outputAmount;
            this.input = input;
        }
    }

    private static class ReactionPart {
        private final int amount;
        private final String material;

        private ReactionPart(final int amount, final String material) {
            this.amount = amount;
            this.material = material;
        }

        @Override
        public String toString() {
            return amount + " " + material;
        }
    }
}
