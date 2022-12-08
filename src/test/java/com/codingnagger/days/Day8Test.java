package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day8Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day8.txt");
    private static final Day DAY = new Day8();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("21");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("8");
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2, 4",
    })
    void partTwo_scenicScores(int x, int y, int expectedScenicScore) {
        var day = new Day8();

        var result = day.scenicScore(x, y, day.createMap(INPUT));

        assertThat(result).isEqualTo(expectedScenicScore);
    }
}
