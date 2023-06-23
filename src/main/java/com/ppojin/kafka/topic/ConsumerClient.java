package com.ppojin.kafka.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerClient {
    private final static Logger log = LoggerFactory.getLogger(ConsumerClient.class);
    private final Partition partition;

    public ConsumerClient(Partition partition) {
        this.partition = partition;
    }

    public ConsumeRecord poll() {
        return this.poll(3000);
    }

    public ConsumeRecord poll(int timeoutMs) {
        if (partition.isBusy()){
            return new ConsumeRecord(this.partition, timeoutMs);
        } else {
            log.warn("partition is busy, consume not started");
            return null;
        }
    }
}
