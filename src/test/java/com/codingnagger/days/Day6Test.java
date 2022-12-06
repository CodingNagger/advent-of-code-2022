package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day6Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day6.txt");
    private static final Day DAY = new Day6();

    @ParameterizedTest
    @CsvSource({
            "mjqjpqmgbljsphdztnvjfqwrcgsmlb, 7",
            "bvwbjplbgvbhsrlpgdmjqwftvncz, 5",
            "nppdvjthqldpwncqszvftbrmjlhg, 6",
            "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg , 10",
            "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw , 11"
    })
    void partOne(String input, int expectedResult) {
        String result = DAY.partOne(List.of(input));

        assertThat(result).isEqualTo(""+expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "mjqjpqmgbljsphdztnvjfqwrcgsmlb, 19",
            "bvwbjplbgvbhsrlpgdmjqwftvncz, 23",
            "nppdvjthqldpwncqszvftbrmjlhg, 23",
            "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg , 29",
            "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw , 26"
    })
    void partTwo(String input, int expectedResult) {
        String result = DAY.partTwo(List.of(input));

        assertThat(result).isEqualTo("" + expectedResult);
    }
}
