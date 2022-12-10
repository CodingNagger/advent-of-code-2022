package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day10Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day10.txt");
    private static final Day DAY = new Day10();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("13140");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo(
                "##..##..##..##..##..##..##..##..##..##..\n" +
                        "###...###...###...###...###...###...###.\n" +
                        "####....####....####....####....####....\n" +
                        "#####.....#####.....#####.....#####.....\n" +
                        "######......######......######......####\n" +
                        "#######.......#######.......#######.....\n");
    }
}
