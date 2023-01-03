package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day17Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day17.txt");
    private static final Day DAY = new Day17();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("3068");
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "2, 4",
            "3, 6",
            "4, 7",
            "5, 9",
            "6, 10",
            "7, 13",
            "8, 15",
            "9, 17",
            "10, 17",
            "11, 18",
            "2022, 3068",
    })
    void letRocksFallAndReturnTowerHeight(int rockCount, int expectedResult) {
        assertThat(new Day17.RockRotation(INPUT.get(0)).letRocksFallAndReturnTowerHeight(rockCount))
                .isEqualTo(expectedResult);
    }

    @Test
    void letRocksFallAndReturnTowerHeightDebug() {
        new Day17.RockRotation(INPUT.get(0)).letRocksFallAndReturnTowerHeight(200);
        assertThat(true).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "5, 13, .....1., >>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<>",
            "6, 13, .00001., >>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<>", // slip to the right
            "5, 11, .1....., <<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>><",
            "6, 11, .10000., <<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>><" // slip to the left
    })
    void edgeCaseSlipToSide(int rockCount, int expectedResult, String line1, String gases) {
        var rotation = new Day17.RockRotation(gases);

        assertThat(rotation.letRocksFallAndReturnTowerHeight(rockCount)).isEqualTo(expectedResult);
        assertThat(rotation.prettyPrintLine(1L)).isEqualTo(line1);
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("1514285714288");
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "2, 4",
            "3, 6",
            "4, 7",
            "5, 9",
            "6, 10",
            "7, 13",
            "8, 15",
            "9, 17",
            "10, 17",
            "11, 18",
            "2022, 3068",
    })
    void letRocksFallAndReturnTowerHeightForLargeNumber(int rockCount, int expectedResult) {
        assertThat(new Day17.RockRotation(INPUT.get(0)).letRocksFallAndReturnTowerHeightForLargeNumber(rockCount))
                .isEqualTo(expectedResult);
    }
}
