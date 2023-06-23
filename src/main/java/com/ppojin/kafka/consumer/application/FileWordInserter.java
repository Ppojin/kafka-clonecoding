package com.ppojin.kafka.consumer.application;

import com.ppojin.kafka.consumer.interfaces.WordInserter;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileWordInserter implements WordInserter {
    private final int bufferSize;
    private int currentBufferSize;
    private final StringBuffer buffer;
    private final Set<String> cache;

    private final String targetFileName;
    private final Lock lock;

    public FileWordInserter(String targetPath, String fileName) {
        this(targetPath, fileName, 3000);
    }

    public FileWordInserter(String targetPath, String fileName, int bufferSize) {
        this.bufferSize = bufferSize;
        this.currentBufferSize = 0;
        this.buffer = new StringBuffer();
        this.cache = new HashSet<>();

        try {
            Paths.get(targetPath);
        } catch (InvalidPathException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        this.targetFileName = targetPath + "/" + fileName + ".txt";

        this.lock = new ReentrantLock();
    }

    @Override
    public void insertLine(String word) {
        buffer.append(word).append(System.lineSeparator());
        if (++currentBufferSize >= bufferSize) {
            flush();
            currentBufferSize = 0;
        }
        cache.add(word.toUpperCase());
    }

    @Override
    public void close() {
        this.flush();
    }

    private void flush() {
        synchronized (lock) {
            try (
                    FileWriter file = new FileWriter(targetFileName);
                    BufferedWriter writer = new BufferedWriter(file)
            ) {
                writer.write(buffer.toString());
            } catch (IOException e) {
                log.error("'{}' cannot access File suddenly, {}", targetFileName, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isDuplicate(String word) {
        return cache.contains(word.toUpperCase());
    }
}
