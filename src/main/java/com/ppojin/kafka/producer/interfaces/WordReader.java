package com.ppojin.kafka.producer.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface WordReader {
    final static Logger log = LoggerFactory.getLogger(WordReader.class);

    String readLine();

    void close();
}
