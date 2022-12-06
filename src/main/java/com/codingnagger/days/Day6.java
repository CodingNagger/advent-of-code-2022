package com.codingnagger.days;

import java.util.HashMap;
import java.util.List;

public class Day6 implements Day {
    @Override
    public String partOne(List<String> input) {
        return "" + findLastIndexOfMarker(input.get(0), 4);
    }

    @Override
    public String partTwo(List<String> input) {
        return ""+ findLastIndexOfMarker(input.get(0), 14);
    }

    private static int findLastIndexOfMarker(String signal, int markerSize) {
        var chars = signal.toCharArray();

        for (var i = 0; i < chars.length - markerSize; i++) {
            var charsSoFar = new HashMap<Character, Boolean>();
            for (var j = 0; j < markerSize; j++) {
                charsSoFar.put(chars[i+j], true);
            }
            if (charsSoFar.size() == markerSize) {
                return i + markerSize;
            }
        }
        return -1;
    }
}
