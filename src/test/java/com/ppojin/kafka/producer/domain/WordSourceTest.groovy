package com.ppojin.kafka.producer.domain

import com.ppojin.kafka.producer.interfaces.WordProducer
import com.ppojin.kafka.producer.interfaces.WordReader
import spock.lang.Specification

class WordSourceTest extends Specification {
    def wordReader = Mock(WordReader)
    def wordProducer = Mock(WordProducer)
    def wordSource

    def setup() {
        wordSource = new WordSource(wordReader, wordProducer)
    }

    // 테스트 케이스 1: WordSource에서 올바른 단어를 전송하는지 테스트
    def "WordSource 가 올바른 단어들만 wordProducer 에 전달하는지 테스트"() {
        given: "wordReader 에서 반환될 단어들"
        wordReader.readLine()
                >> "hello"
                >> "world"
                >> "!invalid"
                >> "123"
                >> null

        when: "WordSource 시작"
        wordSource.start()

        then: "올바른 단어들만 wordProducer 로 전달됨"
        1 * wordProducer.send("hello")
        1 * wordProducer.send("world")
        1 * wordProducer.send("123")

        0 * wordProducer.send("!invalid")
    }

    def "WordSource 중지할 때 WordReader 도 중지하는지 테스트"() {
        when: "WordSource 중지"
        wordSource.stop()

        then: "WordReader의 close() 메서드가 호출됨"
        1 * wordReader.close()
    }
}
