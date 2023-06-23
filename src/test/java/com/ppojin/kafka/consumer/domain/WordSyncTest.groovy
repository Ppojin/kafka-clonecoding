package com.ppojin.kafka.consumer.domain

import com.ppojin.kafka.consumer.interfaces.WordConsumer
import com.ppojin.kafka.consumer.interfaces.WordInserter
import com.ppojin.kafka.consumer.interfaces.WordInserterFactory
import spock.lang.Specification

import java.util.stream.Collectors

class WordSyncTest extends Specification {
    def "정상 작동 확인"() {
        given: "mock 객체"
        WordInserterFactory wordInserterFactory = Mock(WordInserterFactory)
        WordConsumer wordConsumer = Mock(WordConsumer)
        WordInserter wordInserter = Mock(WordInserter)
        1 * wordConsumer.getPartitionCount() >> 2

        and: "WordSync"
        WordSync wordSync = new WordSync(wordInserterFactory, wordConsumer)

        and: "consumer가 반환하는 단어 목록"
        List<String> words = ["apple", "banana", "cherry", "dog", "elephant", "1234"]

        def wordsWithNull = words.stream()
                .collect(Collectors.toList())

        wordsWithNull.add("apple")
        wordsWithNull.add(null)

        when: "wordSync start"
        wordSync.start()
        wordSync.stop()

        then: "consuming 시작"
        2 * wordConsumer.poll() >>> [
                wordsWithNull,
                null
        ]

        and: "word 마다 insertLine 한번씩 호출"
        words.each { word ->
            1 * wordInserterFactory.getWordInserter(_) >> wordInserter
            1 * wordInserter.isDuplicate(word) >> false
            1 * wordInserter.insertLine(word)
        }
        1 * wordInserter.isDuplicate("apple") >> true

        and: "WordSyncTest 종료"
        words.size() * wordInserter.close();
    }
}
