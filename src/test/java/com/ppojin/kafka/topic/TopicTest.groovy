package com.ppojin.kafka.topic

import spock.lang.Specification

class TopicTest extends Specification {

    def "메세지 발송 테스트"() {
        given: "파티션 2개인 토픽"
        def topic = new Topic(2)

        when: "메세지 5번 발송"
        topic.send("hello")
        topic.send("world")
        topic.send("foo")
        topic.send("bar")
        topic.send("baz")

        ConsumerClient client1 = topic.getConsumerClient()
        ConsumerClient client2 = topic.getConsumerClient()
        ConsumerClient client3 = topic.getConsumerClient()

        then: "1번째 컨슈머 정상 작동"
        client1 != null
        Iterable q1 = client1.poll();
        q1.hasNext()
        q1.next() == "hello"
        q1.next() == "world"
        q1.next() == "foo"
        q1.next() == null
        !q1.hasNext()

        then: "2번째 컨슈머 정상 작동"
        client2 != null
        Iterable q2 = client2.poll();
        q2.hasNext()
        q2.next() == "bar"
        q2.next() == "baz"
        q2.next() == null
        !q2.hasNext()


        then: "3번째 컨슈머 = null"
        client3 == null
    }

    def "RuntimeException, invalid partition count"() {
        when:
        new Topic(1)

        then:
        thrown RuntimeException

        when:
        new Topic(28)

        then:
        thrown RuntimeException
    }
}
