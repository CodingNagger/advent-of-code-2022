package com.codingnagger.days;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day10 implements Day {
    @Override
    public String partOne(List<String> input) {
        return new CommunicationDevice().execute(input).partOneAnswer() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new CommunicationDevice().execute(input).partTwoAnswer();
    }

    static class CommunicationDevice {
        int x;
        int xSum;
        int latestCycle;

        Map<Integer, Integer> registerCycleSum;

        public CommunicationDevice execute(List<String> input) {
            xSum = 0;
            latestCycle = 0;
            registerCycleSum = new HashMap<>();

            updateRegisterX(1);

            for (var instruction : input) {
                var commandAndParams = instruction.split(" ");

                switch (commandAndParams[0]) {
                    case "noop":
                        incrementCycle();
                        break;
                    case "addx":
                        incrementCycle();
                        incrementCycle();
                        updateRegisterX(Integer.parseInt(commandAndParams[1]));

                }
            }
            return this;
        }

        private void updateRegisterX(int value) {
            x = value;
            xSum += value;
        }

        private void incrementCycle() {
            latestCycle++;
            registerCycleSum.put(latestCycle, xSum);
        }

        public int signalStrengthForCycle(int cycle) {
            return registerCycleSum.get(cycle) * cycle;
        }

        public int partOneAnswer() {
            var answer = 0;

            for (var i = 20; i <= latestCycle; i += 40) {
                answer += signalStrengthForCycle(i);
//                System.out.printf("Adding %d from cycle %d - result %d%n", signalStrengthForCycle(i), i, answer);
            }

            return answer;
        }

        public String partTwoAnswer() {
            var width = 40;
            var height = latestCycle / width;

            var screen = new char[height][];

            for (var row = 0; row < height; row++) {
                screen[row] = new char[width];

                for (var column = 0; column < width; column++) {
                    screen[row][column] = '.';
                }
            }

            for (var cycle = 1; cycle <= latestCycle; cycle++) {
                var spriteMiddle = registerCycleSum.get(cycle);
                var pixel = cycle - 1;
                var pixelHorizontalPosition = pixel % width;
                var pixelVerticalPosition = pixel / width;

                if (pixelHorizontalPosition > spriteMiddle - 2 && pixelHorizontalPosition < spriteMiddle + 2) {
                    screen[pixelVerticalPosition][pixelHorizontalPosition] = '#';
                }
            }

            var sb = new StringBuilder();

            for (var row = 0; row < height; row++) {
                for (var column = 0; column < width; column++) {
                    sb.append(screen[row][column]);
                }

                sb.append("\n");
            }

            return sb.toString();
        }
    }
}
