package com.codingnagger.days;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Day1 implements Day {

    @Override
    public String partOne(List<String> input) {
        var elvesCals = getElvesCals(input);

        return "" + elvesCals[0];
    }

    @Override
    public String partTwo(List<String> input) {
        var elvesCals = getElvesCals(input);

        return "" + (elvesCals[0] + elvesCals[1] + elvesCals[2]);
    }

    private Integer[] getElvesCals(List<String> input) {
        var elfCals = 0;
        var allCals = new TreeSet<Integer>(Comparator.reverseOrder());

        for (String line : input) {
            if (line.isBlank()) {
                allCals.add(elfCals);
                elfCals = 0;
            } else
                elfCals += Integer.parseInt(line);
        }

        allCals.add(elfCals);

        return allCals.toArray(allCals.toArray(new Integer[0]));
    }
}
