package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day2Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day2.txt");
    private static final Day DAY = new Day2();

    @Test
    void partOne_shouldReturnIncreaseCount() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("15");
    }

    @Test
    void partOne_shouldReturnIncreaseCount_case1() {
        String result = DAY.partOne(List.of("A Y", "B X", "C Z", "A Y"));

        assertThat(result).isEqualTo("23");
    }

    @Test
    void partOne_shouldReturnIncreaseCount_case2() {
        String result = DAY.partOne(List.of("C X"));

        assertThat(result).isEqualTo("7");
    }

    @Test
    void partTwo_shouldReturnSlidingIncreaseCount() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("12");
    }
}
