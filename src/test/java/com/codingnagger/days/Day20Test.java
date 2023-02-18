package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codingnagger.days.Day20.PART_ONE_DECRYPTION_KEY;
import static com.codingnagger.days.Day20.PART_TWO_DECRYPTION_KEY;
import static org.assertj.core.api.Assertions.assertThat;

class Day20Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day20.txt");
    private static final Day DAY = new Day20();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("3");
    }

    @Test
    void exampleMixPartOne() {
        var gps = new Day20.GrovePositioningSystem(INPUT, PART_ONE_DECRYPTION_KEY);

        assertThat(gps.toString()).isEqualTo("1, 2, -3, 3, -2, 0, 4");

        gps.mix(0);
        assertThat(gps.toString()).isEqualTo("2, 1, -3, 3, -2, 0, 4");

        gps.mix(1);
        assertThat(gps.toString()).isEqualTo("1, -3, 2, 3, -2, 0, 4");

        gps.mix(2);
        assertThat(gps.toString()).isEqualTo("1, 2, 3, -2, -3, 0, 4");

        gps.mix(3);
        assertThat(gps.toString()).isEqualTo("1, 2, -2, -3, 0, 3, 4");

        gps.mix(4);
        assertThat(gps.toString()).isEqualTo("1, 2, -3, 0, 3, 4, -2");

        gps.mix(5);
        assertThat(gps.toString()).isEqualTo("1, 2, -3, 0, 3, 4, -2");

        gps.mix(6);
        assertThat(gps.toString()).isEqualTo("1, 2, -3, 4, 0, 3, -2");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("1623178306");
    }

    @Test
    void exampleMixPartTwo() {
        var gps = new Day20.GrovePositioningSystem(INPUT, PART_TWO_DECRYPTION_KEY);

        assertThat(gps.toString()).isEqualTo("811589153, 1623178306, -2434767459, 2434767459, -1623178306, 0, 3246356612");

        gps.mixing();
        assertThat(gps.toString()).isEqualTo("0, -2434767459, 3246356612, -1623178306, 2434767459, 1623178306, 811589153");
    }
}
