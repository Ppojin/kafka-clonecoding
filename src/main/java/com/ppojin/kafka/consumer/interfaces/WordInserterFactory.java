package com.ppojin.kafka.consumer.interfaces;

public interface WordInserterFactory {
    WordInserter getWordInserter(String key);
}
