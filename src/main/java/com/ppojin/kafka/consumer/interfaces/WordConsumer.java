package com.ppojin.kafka.consumer.interfaces;

public interface WordConsumer {
    Iterable<String> poll();

    int getPartitionCount();
}
