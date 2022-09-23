package com.shpikat.adventofcode2019;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class Day21 {

    private static final Long EOL = 10L;

    static class Part1 {

        private static final String RUN_COMMAND = "WALK";

        static int solve(final String input) throws InterruptedException, ExecutionException {
            final Intcode computer = Intcode.fromInput(input, 32);

            final SpringscriptInterpreter interpreter = new SpringscriptInterpreter(computer);
            final String prompt = interpreter.init();
            System.out.println(prompt);

            return interpreter
                    .not(Register.A, Register.J)
                    .not(Register.B, Register.T)
                    .or(Register.T, Register.J)
                    .not(Register.C, Register.T)
                    .or(Register.T, Register.J)
                    .and(Register.D, Register.J)
                    .execute(RUN_COMMAND);
        }
    }

    static class Part2 {

        private static final String RUN_COMMAND = "RUN";

        static int solve(final String input) throws InterruptedException, ExecutionException {
            final Intcode computer = Intcode.fromInput(input, 32);

            final SpringscriptInterpreter interpreter = new SpringscriptInterpreter(computer);
            final String prompt = interpreter.init();
            System.out.println(prompt);

            // Use the program for part1, but now ensure there's a place to move or jump next
            return interpreter
                    .not(Register.A, Register.J)
                    .not(Register.B, Register.T)
                    .or(Register.T, Register.J)
                    .not(Register.C, Register.T)
                    .or(Register.T, Register.J)
                    .and(Register.D, Register.J)
                    .not(Register.E, Register.T)
                    .not(Register.T, Register.T)
                    .or(Register.H, Register.T)
                    .and(Register.T, Register.J)
                    .execute(RUN_COMMAND);
        }
    }

    private enum Register {
        T, J, A, B, C, D, E, F, G, H, I
    }


    private static class SpringscriptInterpreter {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Intcode computer;
        private Future<Void> future;

        SpringscriptInterpreter(final Intcode computer) {
            this.computer = computer;
        }

        String init() throws InterruptedException {
            future = executor.submit(computer);
            final StringBuilder sb = new StringBuilder();
            Long value;
            do {
                value = computer.output.poll(100, TimeUnit.MILLISECONDS);
                if (value != null) {
                    if (value <= 0x7F) {
                        sb.append((char) value.intValue());
                    } else {
                        System.err.println(value);
                    }
                }
            } while (!future.isDone() && !EOL.equals(value));
            return sb.toString();
        }

        SpringscriptInterpreter not(final Register arg1, final Register arg2) throws InterruptedException {
            input(Command.NOT.apply(arg1, arg2));
            return this;
        }

        SpringscriptInterpreter and(final Register arg1, final Register arg2) throws InterruptedException {
            input(Command.AND.apply(arg1, arg2));
            return this;
        }

        SpringscriptInterpreter or(final Register arg1, final Register arg2) throws InterruptedException {
            input(Command.OR.apply(arg1, arg2));
            return this;
        }

        int execute(final String command) throws ExecutionException, InterruptedException {
            input(command);

            final StringBuilder sb = new StringBuilder();
            Long value;
            do {
                value = computer.output.poll(100, TimeUnit.MILLISECONDS);
                if (value != null) {
                    if (value <= 0x7F) {
                        if (value.equals(EOL)) {
                            System.out.println(sb);
                            sb.setLength(0);
                        } else {
                            sb.append((char) value.intValue());
                        }
                    } else {
                        break;
                    }
                }
            } while (!future.isDone() || !computer.output.isEmpty());

            future.get();
            executor.shutdown();
            return Objects.requireNonNullElse(value, 0).intValue();
        }

        private void input(final String line) throws InterruptedException {
            System.out.println(line);

            for (int i = 0; i < line.length(); i++) {
                computer.input.put((long) line.charAt(i));
            }
            computer.input.put(EOL);
        }

        private enum Command implements BiFunction<Register, Register, String> {
            AND,
            OR,
            NOT;

            @Override
            public String apply(final Register r1, final Register r2) {
                if (r2 != Register.T && r2 != Register.J) {
                    throw new IllegalArgumentException("Register is not writable: " + r2);
                }
                return String.format("%s %s %s", name(), r1.name(), r2.name());
            }
        }
    }
}
