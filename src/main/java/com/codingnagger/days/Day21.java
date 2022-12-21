package com.codingnagger.days;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day21 implements Day {
    @Override
    public String partOne(List<String> input) {
        var monkeys = input.stream().map(s -> s.split(": ")).collect(Collectors.toUnmodifiableMap(s -> s[0], s -> s[1]));

        return computeValueFor("root", monkeys).toString();
    }

    @Override
    public String partTwo(List<String> input) {
        var monkeys = input.stream().map(s -> s.split(": ")).collect(Collectors.toUnmodifiableMap(s -> s[0], s -> s[1]));

        var expression = monkeys.get("root").split(" ");

        var left = expression[0];
        var right = expression[2];

        String humnBranch, comparedBranch;

        var humnPath = computeHumnPath(monkeys);

        if (humnPath.contains(right)) {
            humnBranch = right;
            comparedBranch = left;
        } else {
            humnBranch = left;
            comparedBranch = right;
        }

        return findHumnValueFor(humnBranch, computeValueFor(comparedBranch, monkeys), humnPath, monkeys).toString();
    }

    private BigInteger findHumnValueFor(String humnBranch, BigInteger comparedValue, List<String> humnPath, Map<String, String> monkeys) {
        var expression = monkeys.get(humnBranch).split(" ");

        var left = humnPath.contains(expression[0]) ? expression[0] : expression[2];
        var right = humnPath.contains(expression[0]) ? expression[2] : expression[0];

        var rightValue = computeValueFor(right, monkeys);
        BigInteger foundValue;

        System.out.printf("Searching for humn from branch '%s: %s' - left: %s ", humnBranch, monkeys.get(humnBranch), left);

        switch (expression[1]) {
            case "+":
                foundValue = comparedValue.subtract(rightValue);
                System.out.printf("%s subtract %s = %s%n", comparedValue, rightValue, foundValue);
                break;
            case "-":
                foundValue = expression[0].equals(left) ? comparedValue.add(rightValue) : rightValue.subtract(comparedValue);
                System.out.printf("%s add %s = %s%n", comparedValue, rightValue, foundValue);
                break;
            case "/":
                foundValue = expression[0].equals(left) ? comparedValue.multiply(rightValue) : rightValue.divide(comparedValue);
                System.out.printf("%s multiply %s = %s%n", comparedValue, rightValue, foundValue);
                break;
            case "*":
                foundValue = comparedValue.divide(rightValue);
                System.out.printf("%s divide %s = %s%n", comparedValue, rightValue, foundValue);
                break;
            default:
                throw new IllegalStateException("Should not be here");
        }

        if (left.equals("humn")) {
            System.out.printf("Found humn %s%n", foundValue);
            return foundValue;
        }

        System.out.printf("Continue searching for humn with %s == %s%n", left, foundValue);
        return findHumnValueFor(left, foundValue, humnPath, monkeys);
    }

    private List<String> computeHumnPath(Map<String, String> monkeys) {

        var cursor = "humn";
        var humnPath = new ArrayList<String>();

        while (!cursor.equals("root")) {
            humnPath.add(cursor);
            final var valueToFind = cursor;
            var possibleCursor = monkeys.entrySet().stream().filter(e -> e.getValue().contains(valueToFind)).map(Map.Entry::getKey).findFirst();

            if (possibleCursor.isPresent()) {
                cursor = possibleCursor.get();
            } else {
                return List.of();
            }
        }

        return humnPath;
    }

    private BigInteger computeValueFor(String start, Map<String, String> monkeys) {
        var current = monkeys.get(start);
        BigInteger result = null;

        if (current.matches("-?\\d+")) {
            result = new BigInteger(current);
        } else {
            var expression = current.split(" ");

            var left = computeValueFor(expression[0], monkeys);
            var right = computeValueFor(expression[2], monkeys);

            switch (expression[1]) {
                case "+":
                    result = left.add(right);
                    break;
                case "-":
                    result = left.subtract(right);
                    break;
                case "/":
                    result = left.divide(right);
                    break;
                case "*":
                    result = left.multiply(right);
                    break;
            }
        }

        if (result == null) {
            throw new RuntimeException("Should have a value");
        }

        return result;
    }

}
