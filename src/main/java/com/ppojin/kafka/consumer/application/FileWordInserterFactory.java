package com.ppojin.kafka.consumer.application;

import com.ppojin.kafka.consumer.interfaces.WordInserter;
import com.ppojin.kafka.consumer.interfaces.WordInserterFactory;

public class FileWordInserterFactory implements WordInserterFactory {
    private final String targetPath;

    public FileWordInserterFactory(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public WordInserter getWordInserter(String target) {
        return new FileWordInserter(targetPath, target);
    }
}
