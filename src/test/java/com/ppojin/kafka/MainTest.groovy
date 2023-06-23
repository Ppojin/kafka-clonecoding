package com.ppojin.kafka

import spock.lang.Specification

class MainTest extends Specification {

    def "통합 테스트"() {
        when: "테스트 데이터, 경로 "
        String[] args = [
                new File(".").getCanonicalPath()+"/words.TXT",
                new File(".").getCanonicalPath()+"/result",
                "5"
        ]

        then:
        Main.main(args);
    }

    def "MainArguments에 잘못된 인자 개수를 전달하면 예외를 던지는지 확인"() {
        given: "잘못된 인자 배열"
        String[] args = ["a", "b"]

        when: "MainArguments 인스턴스 생성"
        new Main.MainArguments(args)

        then: "RuntimeException 발생"
        thrown RuntimeException
    }

    def "MainArguments에 잘못된 파티션 개수를 전달하면 예외를 던지는지 확인"() {
        given: "잘못된 인자 배열"
        String[] args = ["1234", "1234", "abc"]

        when: "MainArguments 인스턴스 생성"
        new Main.MainArguments(args)
        then: "NumberFormatException 발생"
        thrown RuntimeException
    }
}
