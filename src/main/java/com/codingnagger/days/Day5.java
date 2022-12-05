package com.codingnagger.days;

import java.util.*;

public class Day5 implements Day {
    @Override
    public String partOne(List<String> input) {
        var crane = new Crane(input);
        return crane.topView();
    }

    @Override
    public String partTwo(List<String> input) {
        var crane = new Crane9001(input);
        return crane.topView();
    }

    private class Crane9001 extends Crane {

        public Crane9001(List<String> input) {
            super(input);
        }

        @Override
        protected void applyInstruction(int cratesToMove, int source, int destination) {
            var temporaryStack = new Stack<Character>();

            for (var c = 0; c < cratesToMove; c++) {
                temporaryStack.push(crates.get(source).pop());
            }

            for (var c = 0; c < cratesToMove; c++) {
                crates.get(destination).push(temporaryStack.pop());
            }
        }
    }
    private class Crane {
        Map<Integer, Stack<Character>> crates;

        public Crane(List<String> input) {
            var parsingCrates = true;

            var stackCount = countStacks(input);
            crates = new HashMap<>();
            var flippingStacks = new HashMap<Integer, Stack<Character>>();

            for (var line : input) {
                if (parsingCrates) {
                    if (line.startsWith(" 1")) {
                        for (var entry : flippingStacks.entrySet()) {
                            var stack = new Stack<Character>();
                            var flippingStack = entry.getValue();

                            while (!flippingStack.isEmpty()) {
                                stack.push(flippingStack.pop());
                            }

                            crates.put(entry.getKey(), stack);
                        }

                        parsingCrates = false;
                        continue;
                    }

                    parseCrates(stackCount, line).forEach((stack, crate) ->{
                        Stack<Character> s = flippingStacks.getOrDefault(stack, new Stack<>());
                        s.push(crate);
                        flippingStacks.put(stack, s);
                    });
                } else if (line.startsWith("move")) {
                    parseAndApplyInstruction(line);
                }
            }
        }

        private void parseAndApplyInstruction(String line) {
            var halves = line.split(" from ");
            var cratesToMove = Integer.parseInt(halves[0].split(" ")[1]);
            var sourceAndDestination = halves[1].split(" to ");
            var source = Integer.parseInt(sourceAndDestination[0]);
            var destination = Integer.parseInt(sourceAndDestination[1]);

            applyInstruction(cratesToMove, source, destination);
        }

        protected void applyInstruction(int cratesToMove, int source, int destination) {
            for (var c = 0; c < cratesToMove; c++) {
                crates.get(destination).push(crates.get(source).pop());
            }
        }

        private Map<Integer, Character> parseCrates(long stackCount, String line) {
            var result = new HashMap<Integer, Character>();

            for (var s = 0; s < stackCount; s++) {
                var index = (s*4)+1;
                if (index >= line.length()) break;

                var crate = line.charAt(index);
                if (crate != ' ') {
                    result.put(s+1, crate);
                }
            }

            return result;
        }

        private long countStacks(List<String> input) {
           return input.stream().map(line -> line.chars().mapToObj(c -> (char)c).filter(c -> c == '[').count()).max(Comparator.naturalOrder()).get();
        }

        public String topView() {
            StringBuilder view = new StringBuilder();
            for (var i = 1; i <= crates.size(); i++) {
                view.append(crates.get(i).peek().toString());
            }
            return view.toString();
        }
    }
}