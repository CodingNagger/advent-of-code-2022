package com.codingnagger.days;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Day13 implements Day {
    @Override
    public String partOne(List<String> input) {
        return new DistressSignal(input).sumOfIndexesForPairsInRightOrder() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new DistressSignal(input).decoderKey() + "";
    }

    interface Packet {
        int compareTo(Packet packet);
    }

    static class DistressSignal {
        List<PacketPair> pairs;

        public DistressSignal(List<String> input) {
            pairs = new ArrayList<>();
            var currentPair = new PacketPair();

            for (var line : input) {
                if (line.isEmpty()) {
                    pairs.add(currentPair);
                    currentPair = new PacketPair();
                } else if (currentPair.left == null) {
                    currentPair.left = new GroupPacket(line);
                } else if (currentPair.right == null) {
                    currentPair.right = new GroupPacket(line);
                } else {
                    throw new RuntimeException("Should not be here");
                }
            }

            pairs.add(currentPair);
        }

        public int sumOfIndexesForPairsInRightOrder() {
            var result = 0;

            for (var i = 0; i < pairs.size(); i++) {
                if (pairs.get(i).hasCorrectOrder()) {
                    result += i + 1;
                }
            }

            return result;
        }

        private TreeSet<Packet> getPacketsWithDividers(Packet divider1, Packet divider2) {
            var packets = new TreeSet<>(Packet::compareTo);

            packets.add(divider1);
            packets.add(divider2);

            packets.addAll(this.pairs.stream().map(p -> p.left).collect(Collectors.toList()));
            packets.addAll(this.pairs.stream().map(p -> p.right).collect(Collectors.toList()));

            return packets;
        }

        public int decoderKey() {
            var divider1 = new GroupPacket("[[2]]");
            var divider2 = new GroupPacket("[[6]]");

            var orderedPacketsWithDividers = getPacketsWithDividers(divider1, divider2);

            return (orderedPacketsWithDividers.headSet(divider1).size() + 1)
                    * (orderedPacketsWithDividers.headSet(divider2).size() + 1);
        }
    }

    static class PacketPair {
        Packet left;
        Packet right;

        boolean hasCorrectOrder() {
            return left.compareTo(right) < 0;
        }
    }

    static class GroupPacket implements Packet {
        List<Packet> packets;

        public GroupPacket(IntegerPacket packet) {
            this.packets = List.of(packet);
        }

        public GroupPacket(String line) {
            var lineToProcess = line.substring(1, line.length() - 1);

            var chars = lineToProcess.toCharArray();

            packets = new ArrayList<>();

            for (var i = 0; i < chars.length; i++) {
                char c = chars[i];
                if ('[' == c) {
                    var cursor = 0;
                    var depth = 1;

                    while (depth != 0) {
                        cursor++;
                        c = chars[i + cursor];

                        if ('[' == c) {
                            depth++;

                        } else if (']' == c) {
                            depth--;
                        }
                    }

                    packets.add(new GroupPacket(lineToProcess.substring(i, i + cursor + 1)));
                    i += cursor - 1;
                } else {
                    var currentNumber = "";
                    var numberSize = 0;

                    while ('0' <= c && '9' >= c && i + numberSize < chars.length) {
                        numberSize++;
                        currentNumber += c;

                        if (i + numberSize < chars.length) {
                            c = chars[i + numberSize];
                        }
                    }

                    if (numberSize > 0) {
                        i += numberSize - 1;
                        packets.add(new IntegerPacket(Integer.parseInt(currentNumber)));
                    }
                }
            }
        }

        @Override
        public int compareTo(Packet packet) {
            if (packet instanceof IntegerPacket) {
                return -packet.compareTo(this);
            }

            var group = (GroupPacket) packet;

            if (group.packets.isEmpty()) {
                if (packets.isEmpty()) {
                    return 0;
                }
            }

            for (var i = 0; i < packets.size(); i++) {
                if (group.packets.size() == i) {
                    return 1;
                }

                var comparison = packets.get(i).compareTo(group.packets.get(i));

                if (comparison != 0) {
                    return comparison;
                }
            }

            return -1;
        }

        @Override
        public String toString() {
            return "[" + packets.stream().map(Packet::toString).collect(Collectors.joining(",")) + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupPacket that = (GroupPacket) o;
            return Objects.equals(packets, that.packets);
        }

        @Override
        public int hashCode() {
            return Objects.hash(packets);
        }
    }

    static class IntegerPacket implements Packet {
        Integer value;

        public IntegerPacket(Integer value) {
            this.value = value;
        }

        @Override
        public int compareTo(Packet packet) {
            if (packet instanceof IntegerPacket) {
                return value.compareTo(((IntegerPacket) packet).value);
            }

            return new GroupPacket(this).compareTo(packet);
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntegerPacket that = (IntegerPacket) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

}
