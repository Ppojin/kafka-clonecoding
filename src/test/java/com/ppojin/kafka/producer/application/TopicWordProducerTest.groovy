package com.ppojin.kafka.producer.application

import com.ppojin.kafka.topic.Topic
import spock.lang.Specification

class TopicWordProducerTest extends Specification {
    def "send 메서드 호출 확인"() {
        given: "TopicWordProducer 생성"
        def topicWordProducer = new TopicWordProducer(new Topic(2));

        when: "send 메서드를 호출"
        String word = "testWord"
        topicWordProducer.send(word)

        then:
        notThrown Exception
    }
}
