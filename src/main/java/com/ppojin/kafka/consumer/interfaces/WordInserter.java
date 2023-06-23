package com.ppojin.kafka.consumer.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface WordInserter {
    final static Logger log = LoggerFactory.getLogger(WordInserter.class);

    void insertLine(String word);

    void close();

    boolean isDuplicate(String word);
}
