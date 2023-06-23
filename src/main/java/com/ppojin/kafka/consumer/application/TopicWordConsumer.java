package com.ppojin.kafka.consumer.application;

import com.ppojin.kafka.consumer.interfaces.WordConsumer;
import com.ppojin.kafka.topic.ConsumeRecord;
import com.ppojin.kafka.topic.ConsumerClient;
import com.ppojin.kafka.topic.Topic;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class TopicWordConsumer implements WordConsumer {
    private final Queue<ConsumerClient> clientList;
    private final int partitionCount;

    public TopicWordConsumer(Topic topic) {
        this.partitionCount = topic.getPartitionCount();
        this.clientList = new LinkedBlockingQueue<>();
        for (int i = 0; i < partitionCount; i++) {
            ConsumerClient consumerClient = topic.getConsumerClient();
            clientList.add(consumerClient);
        }
    }
    @Override
    public ConsumeRecord poll() {
        ConsumerClient client = clientList.poll();
        clientList.add(client);
        return client != null ? client.poll() : null;
    }

    @Override
    public int getPartitionCount() {
        return partitionCount;
    }
}
