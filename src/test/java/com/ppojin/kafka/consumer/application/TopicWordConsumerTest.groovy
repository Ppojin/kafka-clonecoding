package com.ppojin.kafka.consumer.application


import com.ppojin.kafka.topic.ConsumerClient
import com.ppojin.kafka.topic.Topic
import com.ppojin.kafka.consumer.interfaces.WordConsumer
import spock.lang.Specification

class TopicWordConsumerTest extends Specification {

    def topic = Mock(Topic)
    def consumerClient = Mock(ConsumerClient);

    def "consumer 를 poll 하면 client 의 poll 을 리턴함"() {
        given: "생성된 wordConsumer 에 consumerClient 주입"
        topic.getPartitionCount() >> 2
        topic.getConsumerClient() >> consumerClient
        WordConsumer wordConsumer = new TopicWordConsumer(topic)

        when: "TopicWordConsumer의 poll() 호출"
        wordConsumer.poll()
        wordConsumer.poll()

        then: "client 의 poll 을 호출"
        2 * consumerClient.poll()
    }

    def "getPartitionCount 테스트"() {
        given: "partitionCount 가 2 인 TopicWordConsumer"
        def partitionCount = 2
        topic.getPartitionCount() >> partitionCount
        topic.getConsumerClient() >> consumerClient
        WordConsumer wordConsumer = new TopicWordConsumer(topic)

        when: "getPartitionCount 호출"
        int count = wordConsumer.getPartitionCount()


        then: "client 의 poll 을 호출"
        count == partitionCount
    }
}
