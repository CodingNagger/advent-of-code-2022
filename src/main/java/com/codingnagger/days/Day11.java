package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;

public class Day11 implements Day {
    @Override
    public String partOne(List<String> input) {
        return new MonkeyRing(input).executeRounds(20, true).monkeyBusiness() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new MonkeyRing(input).executeRounds(10000, false).monkeyBusiness() + "";
    }

    private interface WorryAggravator {
        Item aggravate(Item item);
    }

    private interface MonkeyWire {
        void sendToMonkey(int address, Item item);
    }

    static class SumAggravator implements WorryAggravator {
        private final int value;

        public SumAggravator(int value) {
            this.value = value;
        }

        @Override
        public Item aggravate(Item item) {
            return new Item(value + item.worryLevel);
        }
    }

    static class ProductAggravator implements WorryAggravator {
        private final int value;

        public ProductAggravator(int value) {
            this.value = value;
        }

        @Override
        public Item aggravate(Item item) {
            return new Item(value * item.worryLevel);
        }
    }

    static class SquareAggravator implements WorryAggravator {
        @Override
        public Item aggravate(Item item) {
            return new Item(item.worryLevel * item.worryLevel);
        }
    }

    static class MonkeyRing implements MonkeyWire {
        List<Monkey> monkeys;

        public MonkeyRing(List<String> input) {
            monkeys = new ArrayList<>();
            Monkey currentMonkey = null;

            for (var line : input) {
                if (line.matches("^Monkey [0-9]+:$")) {
                    if (currentMonkey != null) {
                        monkeys.add(currentMonkey);
                    }

                    currentMonkey = new Monkey(this);
                } else if (line.contains("Starting items: ")) {
                    currentMonkey.setItems(
                            Arrays.stream(line.substring(18).split(","))
                                    .map(String::trim)
                                    .mapToLong(Long::parseLong)
                                    .toArray());
                } else if (line.contains("Operation: ")) {
                    if (line.contains("new = old * old")) {
                        currentMonkey.setAggravator(new SquareAggravator());
                    } else if (line.contains("new = old * ")) {
                        currentMonkey.setAggravator(new ProductAggravator(Integer.parseInt(line.substring(25))));
                    } else if (line.contains("new = old + ")) {
                        currentMonkey.setAggravator(new SumAggravator(Integer.parseInt(line.substring(25))));
                    }
                } else if (line.contains("Test: ")) {
                    currentMonkey.setTestDivisor(Integer.parseInt(line.substring(21)));
                } else if (line.contains("If true: ")) {
                    currentMonkey.setTrueTargetMonkey(Integer.parseInt(line.substring(29)));
                } else if (line.contains("If false: ")) {
                    currentMonkey.setFalseTargetMonkey(Integer.parseInt(line.substring(30)));
                }
            }

            monkeys.add(currentMonkey);
        }

        public MonkeyRing executeRounds(int count, boolean chill) {
            var commonDivisor = monkeys.stream().map(monkey -> monkey.testDivisor).reduce(1L, (a, b) -> a * b);

            for (var i = 0; i < count; i++) {
                for (var monkey : monkeys) {
                    while (monkey.holdsSomething()) {
                        monkey.inspectNextItem(chill, commonDivisor);
                    }
                }
            }

            return this;
        }

        public long monkeyBusiness() {
            monkeys.sort(Comparator.comparing(m -> -m.inspectedItemsCount));

            return monkeys.get(0).inspectedItemsCount * monkeys.get(1).inspectedItemsCount;
        }

        @Override
        public void sendToMonkey(int address, Item item) {
            this.monkeys.get(address).addItem(item);
        }
    }

    private static class Monkey {
        private final MonkeyWire monkeyWire;
        private long testDivisor;
        private Deque<Item> items;
        private int trueMonkey;
        private int falseMonkey;
        private WorryAggravator aggravator;
        private Long inspectedItemsCount;

        public Monkey(MonkeyWire monkeyWire) {
            this.monkeyWire = monkeyWire;
            this.inspectedItemsCount = 0L;
        }

        public void setItems(long[] toArray) {
            this.items = Arrays.stream(toArray).mapToObj(Item::new).collect(Collectors.toCollection(ArrayDeque::new));
        }

        public void setTestDivisor(long testDivisor) {
            this.testDivisor = testDivisor;
        }

        public void setTrueTargetMonkey(int trueMonkey) {
            this.trueMonkey = trueMonkey;
        }

        public void setFalseTargetMonkey(int falseMonkey) {
            this.falseMonkey = falseMonkey;
        }

        public void setAggravator(WorryAggravator aggravator) {
            this.aggravator = aggravator;
        }

        public boolean holdsSomething() {
            return !items.isEmpty();
        }

        public void inspectNextItem(boolean chill, Long commonDivisor) {
            var item = aggravator.aggravate(items.removeFirst());
            var refreshedItem = new Item(item.worryLevel % commonDivisor);

            if (chill) {
                refreshedItem = refreshedItem.cooldown();
            }

            if (refreshedItem.worryLevel % testDivisor == 0) {
                monkeyWire.sendToMonkey(trueMonkey, refreshedItem);
            } else {
                monkeyWire.sendToMonkey(falseMonkey, refreshedItem);
            }

            inspectedItemsCount++;
        }

        public void addItem(Item item) {
            items.addLast(item);
        }
    }

    private static class Item {
        private final long worryLevel;

        public Item(long initialWorryLevel) {
            this.worryLevel = initialWorryLevel;
        }

        public Item cooldown() {
            return new Item(worryLevel / 3);
        }
    }
}
