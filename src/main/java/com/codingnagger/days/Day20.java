package com.codingnagger.days;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day20 implements Day {
    public static final BigInteger PART_ONE_DECRYPTION_KEY = BigInteger.ONE;
    public static final BigInteger PART_TWO_DECRYPTION_KEY = BigInteger.valueOf(811589153L);

    @Override
    public String partOne(List<String> input) {
        return new GrovePositioningSystem(input, PART_ONE_DECRYPTION_KEY).mixing().groveCoordinates() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new GrovePositioningSystem(input, PART_TWO_DECRYPTION_KEY).mixingTenTimes().groveCoordinates() + "";
    }

    static class GrovePositioningSystem {
        private final List<GPSNumber> numbers;

        GrovePositioningSystem(List<String> input, BigInteger decryptionKey) {
            numbers = IntStream.range(0, input.size())
                    .mapToObj(position -> new GPSNumber(input.get(position), position, decryptionKey))
                    .collect(Collectors.toList());
        }

        void mix(int position) {
            var numberToMove = getNumberAtPosition(position);
            var existingIndex = numbers.indexOf(numberToMove);

            numbers.remove(numberToMove);

            var newIndex = calculateInsertPosition(numberToMove.value, existingIndex);

            numbers.add(newIndex, numberToMove.clone());
        }

        private GPSNumber getNumberAtPosition(int position) {
            return numbers.stream()
                    .filter(number -> number.originalPosition == position)
                    .findFirst()
                    .orElseThrow();
        }

        GrovePositioningSystem mixingTenTimes() {
            for (var i = 0; i < 10; i++) {
                mixing();
            }
            return this;
        }

        GrovePositioningSystem mixing() {
            for (var position = 0; position < Objects.requireNonNull(numbers).size(); position++) {
                mix(position);
            }
            return this;
        }

        BigInteger groveCoordinates() {
            var zero = numbers.stream().filter(n -> n.value.equals(BigInteger.ZERO)).findFirst().orElseThrow();
            var indexOfZero = numbers.indexOf(zero);
            return valueAt(indexOfZero + 1000).add(valueAt(indexOfZero + 2000)).add(valueAt(indexOfZero + 3000));
        }

        private BigInteger valueAt(int i) {
            return numbers.get(position(i)).value;
        }

        private int calculateInsertPosition(BigInteger value, int existingIndex) {
            if (value.equals(BigInteger.ZERO)) {
                return existingIndex;
            }

            var absolutePosition = value.add(BigInteger.valueOf(existingIndex));
            int scaledDownPosition = getFittedPosition((int) absolutePosition.mod(BigInteger.valueOf(numbers.size())).longValueExact());
            var fittedPosition = getFittedPosition(scaledDownPosition);

            return fittedPosition == 0 ? numbers.size() : fittedPosition;
        }

        private int getFittedPosition(int possiblyNegativePosition) {
            return possiblyNegativePosition < 0 ? (numbers.size() + possiblyNegativePosition) : possiblyNegativePosition;
        }

        private int position(int i) {
            return i % numbers.size();
        }

        @Override
        public String toString() {
            return numbers.stream().map(n -> n.value + "").collect(Collectors.joining(", "));
        }
    }

    static class GPSNumber implements Cloneable {
        private final BigInteger value;
        private final int originalPosition;

        public GPSNumber(String value, int originalPosition, BigInteger decryptionKey) {
            this(new BigInteger(value).multiply(decryptionKey), originalPosition);
        }

        public GPSNumber(BigInteger value, int originalPosition) {
            this.value = value;
            this.originalPosition = originalPosition;
        }

        @Override
        public String toString() {
            return "" + value;
        }

        @Override
        public GPSNumber clone() {
            return new GPSNumber(value, originalPosition);
        }
    }
}
