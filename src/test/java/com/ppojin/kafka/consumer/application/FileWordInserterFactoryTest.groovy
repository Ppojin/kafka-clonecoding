package com.ppojin.kafka.consumer.application


import spock.lang.Specification

class FileWordInserterFactoryTest extends Specification {
    def "FileWordInserterFactory 생성"() {
        given: "FileWordInserterFactory 생성"
        def factory = new FileWordInserterFactory(
                new File(".").getCanonicalPath() +
                "/src/test/resources/"
        )

        when: "getWordInserter 반환"
        factory.getWordInserter("testFile")

        then: "정상적으로 종료됨"
        notThrown Exception
    }
}
