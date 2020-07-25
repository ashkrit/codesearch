package org.search.codesearch.index;

import org.search.codesearch.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.walkFileTree;

public class BruteForceCodeSearch implements Search {
    private static final Logger logger = LoggerFactory.getLogger(BruteForceCodeSearch.class);

    private final String rootPath;

    public BruteForceCodeSearch(String rootPath) {
        this.rootPath = rootPath;
    }


    @Override
    public void match(String pattern, Consumer<Path> consumer) {
        try {
            FileProcessor visitor = new FileProcessor(consumer);
            walkFileTree(Paths.get(rootPath), visitor);
            logger.info("Folder visited {} , files visited {} , files processed {} ", visitor.folderVisited(), visitor.filesVisited(), visitor.filesProcessed);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static class FileProcessor extends SimpleFileVisitor<Path> {

        private final Consumer<Path> consumer;
        private final AtomicLong filesVisited = new AtomicLong();
        private final AtomicLong folderVisited = new AtomicLong();
        private final AtomicLong filesProcessed = new AtomicLong();

        public FileProcessor(Consumer<Path> consumer) {
            this.consumer = consumer;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            File file = dir.toFile();
            if (skipProcessing(file)) {
                return FileVisitResult.SKIP_SUBTREE;
            } else {
                return FileVisitResult.CONTINUE;
            }
        }

        private boolean skipProcessing(File file) {
            return file.isHidden() || FileTypes.isGitFolder(file);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            filesVisited.incrementAndGet();
            if (FileTypes.isCompiledFile(file.toFile())) {
                return FileVisitResult.SKIP_SIBLINGS;
            }

            filesProcessed.incrementAndGet();
            consumer.accept(file);
            return CONTINUE;

        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            folderVisited.incrementAndGet();
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                                               IOException exc) {
            logger.error("Failed", exc);
            return CONTINUE;
        }

        public long filesVisited() {
            return filesVisited.longValue();
        }


        public long folderVisited() {
            return folderVisited.longValue();
        }

        public long filesProcessed() {
            return filesProcessed.longValue();
        }
    }
}
