package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Day13Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day13.txt");
    private static final Day DAY = new Day13();

    private static Stream<Arguments> partOneWithSinglePairsInput() {
        return Stream.of(
                Arguments.of("[5,6,7]", "[5,6,0]", "0"),
                Arguments.of("[4,[5,6,7]]", "[4,[5,6,0]]", "0"),
                Arguments.of("[]", "[3]", "1"),
                Arguments.of("[[0,2,10,2,8]]", "[[0,2,10,2]]", "0"),
                Arguments.of("[[0,2,10,2,8]]", "[[0,2,10,2,8]]", "1"),
                Arguments.of("[[],[],[]]", "[[],[]]", "0")
        );
    }

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("13");
    }

    @ParameterizedTest
    @MethodSource("partOneWithSinglePairsInput")
    void partOneWithSinglePairs(String pair1, String pair2, String expectedResult) {
        String result = DAY.partOne(List.of(pair1, pair2));

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("140");
    }
}
