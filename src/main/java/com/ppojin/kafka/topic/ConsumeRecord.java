package com.ppojin.kafka.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ConsumeRecord implements Iterator<String>, Iterable<String> {
    private final static Logger log = LoggerFactory.getLogger(ConsumeRecord.class);

    private final Partition partition;
    private final int timeoutMs;

    private boolean hasNext;
    private long waitFrom;

    public ConsumeRecord(Partition partition, int timeoutMs) {
        this.partition = partition;
        this.timeoutMs = timeoutMs;
        this.waitFrom = System.currentTimeMillis();
        this.hasNext = true;
    }

    @Override
    public ConsumeRecord iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public String next() {
        waitFrom = System.currentTimeMillis();
        while (true) {
            String word = partition.poll();
            if (word != null) {
                log.debug("word: {} ", word);
                waitFrom = System.currentTimeMillis();
                return word;
            } else {
                long now = System.currentTimeMillis();
                if ((now - waitFrom) >= timeoutMs) {
                    log.info("timeout consuming {}ms", now - waitFrom);
                    hasNext = false;
                    partition.setBusy(false);
                    return null;
                }
                int sleepInterval = 300;
                log.debug("queue have no message now, sleep {}ms", sleepInterval);
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
