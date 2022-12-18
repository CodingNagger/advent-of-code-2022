package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day15Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day15.txt");
    private static final Day DAY = new Day15(true);

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("26");
    }

    @Test
    void maybeLocationRange() {
        var sensor = new Day15.Sensor(2, 2, new Day15.Beacon(3, 3));

        var possibleLocationRange = sensor.maybeLocationRange(2);

        assertThat(possibleLocationRange)
                .isPresent()
                .get()
                .isEqualTo(new Day15.LocationRange(2, 0, 4));

        possibleLocationRange = sensor.maybeLocationRange(4);

        assertThat(possibleLocationRange)
                .isPresent()
                .get()
                .isEqualTo(new Day15.LocationRange(4, 2, 2));

        possibleLocationRange = sensor.maybeLocationRange(-1);

        assertThat(possibleLocationRange)
                .isEmpty();

        possibleLocationRange = sensor.maybeLocationRange(5);

        assertThat(possibleLocationRange)
                .isEmpty();
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("56000011");
    }
}
