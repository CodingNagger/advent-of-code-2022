package com.codingnagger.days;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day3 implements Day {
    @Override
    public String partOne(List<String> input) {
        return input.stream().map(this::createCompartments).map(this::omnipresentItemPriority).reduce(Integer::sum).get().toString();
    }

    private int omnipresentItemPriority(String[] compartments) {
        var omnipresentItem = findOmnipresentItem(compartments);
        return priority(omnipresentItem);
    }

    private char findOmnipresentItem(String[] compartments) {
        var itemCounts = new HashMap<Character, Integer>();

        for (var item = 'a'; item <= 'z'; item++) {
            for (var compartment : compartments) {
                if (compartment.contains(item + "")) {
                    itemCounts.put(item, (itemCounts.getOrDefault(item, 0)) + 1);
                }
            }
        }

        for (var item = 'A'; item <= 'Z'; item++) {
            for (var compartment : compartments) {
                if (compartment.contains(item + "")) {
                    itemCounts.put(item, (itemCounts.getOrDefault(item, 0)) + 1);
                }
            }
        }

        return itemCounts.entrySet().stream().filter((entry) -> entry.getValue() == compartments.length).map(Map.Entry::getKey).findFirst().get();
    }

    private String[] createCompartments(String line) {
        return new String[]{
                line.substring(0, (line.length() / 2)),
                line.substring(line.length() / 2)
        };
    }

    @Override
    public String partTwo(List<String> input) {
        var prioritySum = 0;

        for (int i = 0; i < input.size(); i += 3) {
            var omnipresentItem = findOmnipresentItem(new String[]{
                    input.get(i), input.get(i + 1), input.get(i + 2)
            });
            prioritySum += priority(omnipresentItem);
        }
        return prioritySum + "";
    }
    

    private int priority(char c) {
        return c - (isLowercase(c) ? 96 : 38);
    }

    private boolean isLowercase(char c) {
        return c >= 'a' && c <= 'z';
    }
}