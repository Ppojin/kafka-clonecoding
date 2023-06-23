package com.ppojin.kafka.producer.application;

import com.ppojin.kafka.topic.Topic;
import com.ppojin.kafka.producer.interfaces.WordProducer;

public class TopicWordProducer implements WordProducer {
    private final Topic topic;

    public TopicWordProducer(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void send(String word) {
        topic.send(word);
    }
}
