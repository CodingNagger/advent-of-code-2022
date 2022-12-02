package com.codingnagger.days;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2 implements Day {

    @Override
    public String partOne(List<String> input) {
        return input.stream().map(this::roundScore).reduce(Integer::sum).get() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return input.stream().map(this::roundScorePartTwo).reduce(Integer::sum).get() + "";
    }

    int roundScore(String line) {
        var res = Arrays.stream(line.split(" ")).collect(Collectors.toUnmodifiableList());

        return calculateScore(res);
    }

    int roundScorePartTwo(String line) {
        var res = Arrays.stream(line.split(" ")).collect(Collectors.toUnmodifiableList());

        return calculateScorePartTwo(res);
    }

    private int calculateScore(List<String> res) {
        var opponentPlay = calculateOpponentPlay(res.get(0));
        var myPlay = calculateMyPlay(res.get(1));

        return myPlay + outcome(opponentPlay, myPlay);
    }

    private int calculateScorePartTwo(List<String> res) {
        var opponentPlay = calculateOpponentPlay(res.get(0));
        var myPlay = calculateMyPlayPartTwo(opponentPlay, res.get(1));

        return myPlay + outcome(opponentPlay, myPlay);
    }

    private int calculateMyPlayPartTwo(int opponentPlay, String myInput) {
        switch (myInput) {
            case "X":
                return losingPlay(opponentPlay);
            case "Y":
                return opponentPlay;
            case "Z":
                return winningPlay(opponentPlay);
            default:
                return 0;
        }
    }

    private int winningPlay(int opponentPlay) {
        switch (opponentPlay) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 1;
            default:
                return 0;
        }
    }

    private int losingPlay(int opponentPlay) {
        switch (opponentPlay) {
            case 1:
                return 3;
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                return 0;
        }
    }

    private int outcome(Integer opponentPlay, Integer myPlay) {
        if (opponentPlay == 3 && myPlay == 1) return 6;
        if (myPlay == 3 && opponentPlay == 1) return 0;

        return 3 * (myPlay.compareTo(opponentPlay) + 1);
    }

    private int calculateOpponentPlay(String play) {
        switch (play) {
            case "A":
                return 1;
            case "B":
                return 2;
            case "C":
                return 3;
            default:
                return 0;
        }
    }

    private int calculateMyPlay(String play) {
        switch (play) {
            case "X":
                return 1;
            case "Y":
                return 2;
            case "Z":
                return 3;
            default:
                return 0;
        }
    }
}
