package com.codingnagger.days;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day7 implements Day {
    @Override
    public String partOne(List<String> input) {
        return new DirectoryParser().parse(input).totalSizeOfDirectoriesUnder(100000) + "";
    }

    @Override
    public String partTwo(List<String> input) {
        var directory = new DirectoryParser().parse(input);

        return directory.smallestDirectorySizeToDeleteToGetOver(70000000 - directory.size(), 30000000) + "";
    }

    interface File {
        long size();

        boolean isDirectory();
    }

    static class RegularFile implements File {
        private final long size;

        RegularFile(long size) {
            this.size = size;
        }

        @Override
        public long size() {
            return size;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }
    }

    static class Directory implements File {
        private final Map<String, File> files = new HashMap<>();
        private final Directory parent;

        Directory(Directory parent) {
            this.parent = parent;
        }

        @Override
        public long size() {
            return files.values().stream().mapToLong(File::size).sum();
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        public void makeDirectory(String name) {
            files.put(name, new Directory(this));
        }

        public Directory getDirectory(String name) {
            return files.entrySet().stream()
                    .filter(e -> e.getKey().equals(name) && e.getValue().isDirectory())
                    .map(Map.Entry::getValue)
                    .map(Directory.class::cast)
                    .findFirst()
                    .orElse(parent);
        }

        public void addFile(String name, long size) {
            files.put(name, new RegularFile(size));
        }

        public long totalSizeOfDirectoriesUnder(int minSize) {
            return files.values().stream().filter(File::isDirectory).map(Directory.class::cast).filter(d -> d.size() <= minSize).mapToLong(Directory::size).sum() +
                    files.values().stream().filter(File::isDirectory).map(Directory.class::cast).mapToLong(d -> d.totalSizeOfDirectoriesUnder(minSize)).sum();
        }

        public long smallestDirectorySizeToDeleteToGetOver(long freeSpaceAvailable, long freeSpaceRequired) {
            return files.values().stream().filter(File::isDirectory).map(Directory.class::cast)
                    .filter(d -> d.size() + freeSpaceAvailable >= freeSpaceRequired)
                    .mapToLong(d -> d.smallestDirectorySizeToDeleteToGetOver(freeSpaceAvailable, freeSpaceRequired))
                    .min()
                    .orElse(this.size());
        }
    }

    static class DirectoryParser {
        Directory root = null;
        Directory current = null;

        public Directory parse(List<String> commands) {
            for (var i = 0; i < commands.size() - 1; ) {
                var command = commands.get(i);

                if (command.startsWith("$")) {
                    i = parseAndExecuteCommand(command.substring(2), i, commands);
                }
            }

            return root;
        }

        private int parseAndExecuteCommand(String command, int index, List<String> commands) {
            var exeAndParams = command.split(" ", 2);
            var exe = exeAndParams[0];

            if (exe.equals("cd")) {
                var params = exeAndParams[1];

                if (params.equals("/")) {
                    root = current = new Directory(null);
                } else {
                    current = current.getDirectory(params);
                }

                return index + 1;
            } else if (exe.equals("ls")) {
                String nextFile;
                do {
                    index++;

                    nextFile = commands.get(index);
                    var fileInfo = nextFile.split(" ", 2);

                    if (nextFile.matches("^[0-9]+ .*$")) {
                        current.addFile(fileInfo[1], Long.parseLong(fileInfo[0]));
                    } else if (nextFile.startsWith("dir")) {
                        current.makeDirectory(fileInfo[1]);
                    }
                } while (!nextFile.startsWith("$") && index < commands.size() - 1);

                return index;
            }

            throw new IllegalStateException("Shouldn't be here");
        }
    }
}
