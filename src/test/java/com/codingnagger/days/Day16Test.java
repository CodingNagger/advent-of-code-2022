package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day16Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day16.txt");
    private static final Day DAY = new Day16();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("1651");
    }

    @ParameterizedTest
    @CsvSource({
            "30, 1651",
            "3, 20"
    })
    void maxPressure(long duration, long expectedResult) {
        assertThat(new Day16.Volcano(INPUT).maxPressure(duration)).isEqualTo(expectedResult);
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("1707");
    }

    @ParameterizedTest
    @CsvSource({
            "26, 1707",
            "25, 1626",
            "24, 1545",
            "3, 33",
            "4, 66",
            "5, 104",
            "8, 260",
            "9, 336",
    })
    void maxPressureTeamUp(long duration, long expectedResult) {
        assertThat(new Day16.Volcano(INPUT).maxPressureTeamUp(duration)).isEqualTo(expectedResult);
    }
}
