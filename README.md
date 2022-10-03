## Advent of Code 2019

[![All solutions](https://github.com/shpikat/advent-of-code-2019/actions/workflows/check-solutions.yml/badge.svg)](https://github.com/shpikat/advent-of-code-2019/actions/workflows/check-solutions.yml)

### Introduction

> _Advent of Code_ is an Advent calendar of small programming puzzles for a variety of skill sets and skill levels that
> can be solved in any programming language you like.

See more at https://adventofcode.com/2019/about

A friend of mine mentioned he had done it the year before that, and he was going to do it again this year. So I thought
to myself: _This must be fun! :tada:_ So, here we go: this is mostly about **FUN**.

### My experience

The challenge for me is to come algorithm first using the standard library only. The intention is to learn and feel the
language in itself. I mean, there is a reason for the language to have exactly what is has in SDK.

Having all that in mind, my concern is to create the properly maintainable code, which is nice to read, understand and
hopefully learn from it.

### Repository details

After completing Advent of Code 2020 (see [shpikat/advent-of-code-2020](https://github.com/shpikat/advent-of-code-2020)
by the way), I realized I should've organized that a tad differently. Hence, The Great Refactor of 2021, where the
solution verification is done through JUnit test suites.

Although there are days [13](src/main/java/com/shpikat/adventofcode2019/Day13.java),
[15](src/main/java/com/shpikat/adventofcode2019/Day15.java) and
[25](src/main/java/com/shpikat/adventofcode2019/Day25.java) where some user interaction is provided. Those can be run
through their `main` entrypoint.

Also, both parts of the solution are important as they are. Unfortunately I realized I should keep records of all my
steps a little too late, so a few days in the beginning of the journey are missing their part 1.

Important to note Day 23 Part 2 tests are a bit flaky. My Intcode was designed to be run concurrently with the main
procedure. Which played nicely until a great number of concurrent executions were required with no explicit execution
control. 
