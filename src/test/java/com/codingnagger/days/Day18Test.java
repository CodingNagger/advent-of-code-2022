package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day18Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day18.txt");
    private static final Day DAY = new Day18();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("64");
    }

    @Test
    void partOne_smallInput() {
        String result = DAY.partOne(List.of("1,1,1", "2,1,1"));

        assertThat(result).isEqualTo("10");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("58");
    }

    @Test
    void partOne_simpleExampleWithTwoHorizontalNeighboursTrappedBubbles() {
        var result = DAY.partOne(buildTwoHorizontalNeighboursNeighboursTrappedBubblesSampleInput());

        assertThat(result).isEqualTo("76");
    }

    @Test
    void partOne_simpleExampleWithTwoSingleTrappedBubbles() {
        var result = DAY.partOne(buildTwoSingleTrappedBubblesSampleInput());

        assertThat(result).isEqualTo("90");
    }

    @Test
    void partOne_simpleExampleWithThreeHorizontalNeighboursTrappedBubbles() {
        var result = DAY.partOne(buildThreeHorizontalNeighboursNeighboursTrappedBubblesSampleInput());

        assertThat(result).isEqualTo("92");
    }

    private List<String> buildTwoHorizontalNeighboursNeighboursTrappedBubblesSampleInput() {
        var input = new ArrayList<String>();

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (y == 1 && z == 1 && (x == 1 || x == 2)) {
                        continue;
                    }

                    input.add(String.format("%d,%d,%d", x, y, z));
                }
            }
        }

        return input;
    }

    private List<String> buildTwoSingleTrappedBubblesSampleInput() {
        var input = new ArrayList<String>();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (y == 1 && z == 1 && (x == 1 || x == 3)) {
                        continue;
                    }

                    input.add(String.format("%d,%d,%d", x, y, z));
                }
            }
        }

        return input;
    }

    private List<String> buildThreeHorizontalNeighboursNeighboursTrappedBubblesSampleInput() {
        var input = new ArrayList<String>();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (y == 1 && z == 1 && (x == 1 || x == 2 || x == 3)) {
                        continue;
                    }

                    input.add(String.format("%d,%d,%d", x, y, z));
                }
            }
        }

        return input;
    }

    @Test
    void partTwo_simpleExampleWithTwoHorizontalNeighboursTrappedBubbles() {
        String result = DAY.partTwo(buildTwoHorizontalNeighboursNeighboursTrappedBubblesSampleInput());

        assertThat(result).isEqualTo("66");
    }

    @Test
    void partTwo_simpleExampleWithTwoSingleTrappedBubbles() {
        String result = DAY.partTwo(buildTwoSingleTrappedBubblesSampleInput());

        assertThat(result).isEqualTo("78");
    }

    @Test
    void partTwo_simpleExampleWithThreeHorizontalNeighboursTrappedBubbles() {
        String result = DAY.partTwo(buildThreeHorizontalNeighboursNeighboursTrappedBubblesSampleInput());

        assertThat(result).isEqualTo("78");
    }
}
