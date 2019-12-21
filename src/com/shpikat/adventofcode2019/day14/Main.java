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

    private static final long AMOUNT_ORE = 1_000_000_000_000L;

    // calculated at part 1, could have been added here, too, but, eh...
    private static final long MIN_ORE_PER_FUEL = 220019L;

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

        final Map<String, Long> materialRequests = new HashMap<>();
        final Map<String, Long> leftOvers = new HashMap<>();
        long amountOre = AMOUNT_ORE;
        long amountFuel = 0L;
        while (amountOre > 0) {
            if (materialRequests.isEmpty()) {
                final long moreFuel = Math.max(amountOre / MIN_ORE_PER_FUEL, 1);
                materialRequests.put("FUEL", moreFuel);
                amountFuel += moreFuel;
            }
            final Iterator<Map.Entry<String, Long>> iterator = materialRequests.entrySet().iterator();
            final Map.Entry<String, Long> request = iterator.next();
            iterator.remove();
            final String material = request.getKey();
            final long amountNeeded = request.getValue();
            final long currentLeftOver = leftOvers.getOrDefault(material, 0L);
            if (amountNeeded <= currentLeftOver) {
                leftOvers.put(material, currentLeftOver - amountNeeded);
            } else {
                final long amountStillNeeded = amountNeeded - currentLeftOver;
                final Reaction reaction = reactions.get(material);
                final long nReactionsRequired = (long) Math.ceil((double) amountStillNeeded / reaction.outputAmount);
                leftOvers.put(material, nReactionsRequired * reaction.outputAmount - amountStillNeeded);
                for (final ReactionPart reactionPart : reaction.input) {
                    if (reactionPart.material.equals("ORE")) {
                        amountOre -= nReactionsRequired * reactionPart.amount;
                    } else {
                        materialRequests.merge(reactionPart.material, nReactionsRequired * reactionPart.amount, Long::sum);
                    }
                }
            }
        }

        System.out.println(amountOre == 0 ? amountFuel : amountFuel - 1);
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
