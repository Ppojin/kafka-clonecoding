package com.ppojin.kafka.producer.domain;

import com.ppojin.kafka.producer.interfaces.WordProducer;
import com.ppojin.kafka.producer.interfaces.WordReader;
import com.ppojin.kafka.producer.application.FileWordReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class WordSource {
    private final static Logger log = LoggerFactory.getLogger(FileWordReader.class);

    private final Pattern validWordRegex;
    private final WordReader wordReader;
    private final WordProducer wordProducer;

    public WordSource(WordReader fileWordReader, WordProducer topicWordProducer) {
        this.validWordRegex = Pattern.compile("^[a-zA-Z0-9].*");
        this.wordReader = fileWordReader;
        this.wordProducer = topicWordProducer;
    }

    public void start() {
        log.info("start producing");
        while (true) {
            var word = wordReader.readLine();
            if (word == null) {
                log.info("Produce complete!");
                break;
            }
            if (!validWordRegex.matcher(word).matches()) {
                log.warn("'{}' is not valid", word);
                continue;
            }
            wordProducer.send(word);
        }
    }

    public void stop() {
        wordReader.close();
    }
}
