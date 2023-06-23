package com.ppojin.kafka.topic

import spock.lang.Specification

class ConsumeRecordTest extends Specification {
    // 테스트 케이스 1: hasNext() 메서드 테스트
    def "hasNext() 메서드가 정상적으로 작동하는지 테스트"() {
        given: "Partition 인스턴스와 함께 ConsumeRecord 인스턴스 생성"
        Partition partition = new Partition(808)
        int timeoutMs = 1000
        ConsumeRecord consumeRecord = new ConsumeRecord(partition, timeoutMs)

        expect: "hasNext()가 true를 반환"
        consumeRecord.hasNext()
    }

    // 테스트 케이스 2: next() 메서드 테스트
    def "next() 메서드가 정상적으로 작동하는지 테스트"() {
        given: "Partition 인스턴스에 메시지 추가"
        Partition partition = new Partition(808)
        String message = "Hello, World!"
        partition.add(message)

        and: "ConsumeRecord 인스턴스 생성"
        int timeoutMs = 1000d
        ConsumeRecord consumeRecord = new ConsumeRecord(partition, timeoutMs)

        expect: "next() 메서드가 정상적으로 메시지를 반환"
        consumeRecord.next() == message
    }

    // 테스트 케이스 3: next() 메서드의 타임아웃 테스트
    def "next() 메서드가 타임아웃되는 경우 테스트"() {
        given: "비어 있는 Partition 인스턴스와 함께 ConsumeRecord 인스턴스 생성"
        Partition partition = new Partition(808)
        int timeoutMs = 1000
        ConsumeRecord consumeRecord = new ConsumeRecord(partition, timeoutMs)

        when: "next() 메서드를 호출하고 반환값이 null인지 확인"
        String result = consumeRecord.next()

        then: "next() 메서드가 null을 반환하고 hasNext()가 false를 반환"
        result == null
        !consumeRecord.hasNext()
    }
}
