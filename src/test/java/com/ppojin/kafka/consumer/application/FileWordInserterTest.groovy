package com.ppojin.kafka.consumer.application

import spock.lang.Specification

import java.nio.file.FileSystems
import java.util.stream.IntStream

class FileWordInserterTest extends Specification {

    String currentPath = FileSystems.getDefault()
            .getPath(new File(".").getCanonicalPath() +
                    "/src/test/resources/"
            )
            .toAbsolutePath()
            .toString()
    String fileName = "fileWordInserterTestFile";

    def deleteTestFile(){
        new File(currentPath, fileName + ".txt").delete()
    }

    def setup(){
        deleteTestFile()
    }

    def cleanup(){
        deleteTestFile()
    }

    def "word 를 파일에 추가"() {
        given: "bufferSize 가 10 인 testFile FileWordInserter"
        int bufferSize = 10
        String word = "testWord"
        FileWordInserter inserter = new FileWordInserter(currentPath, fileName, bufferSize)

        when: "buffer size 보다 적게 word 입력"
        IntStream.range(0, 9).forEach {
            inserter.insertLine(word)
        }

        and: "파일 확인"
        new File(currentPath, fileName + ".txt").text

        then: "파일이 존재하지 않음"
        thrown FileNotFoundException

        when: "buffer size 만큼 word 입력"
        inserter.insertLine(word)

        then: "파일에 올바르게 word 가 삽입됨"
        String expectedContent = (word + System.lineSeparator()) * 10
        String actualContent = new File(currentPath, fileName + ".txt").text
        actualContent == expectedContent
    }

    def "close 호출 시 word 의 buffer 를 파일에 추가"() {
        given: "bufferSize 가 10 인 testFile FileWordInserter"
        int bufferSize = 10
        String word = "testWord"
        FileWordInserter inserter = new FileWordInserter(currentPath, fileName, bufferSize)

        when: "buffer size 보다 적게 word 입력 후 close"
        inserter.insertLine(word)
        inserter.close()

        then: "파일에 올바르게 word 가 삽입됨"
        String expectedContent = word + System.lineSeparator()
        String actualContent = new File(currentPath, fileName + ".txt").text
        actualContent == expectedContent
    }

    def "대소문자 구분 하지 않는 word 중복 확인"() {
        given: "word 삽입된 FileWordInserter"
        FileWordInserter inserter = new FileWordInserter(currentPath, fileName)
        String word1 = "testWord"
        String word2 = "Testword"
        inserter.insertLine(word1)

        when: "대소문자 변형된 word 의 중복 확인"
        boolean result1 = inserter.isDuplicate(word1)
        boolean result2 = inserter.isDuplicate(word2)
        boolean result3 = inserter.isDuplicate(word1.toUpperCase())
        boolean result4 = inserter.isDuplicate(word1.toLowerCase())

        then: "모든 isDuplicate == true"
        result1 && result2 && result3 && result4
    }


    def "RuntimeException, 파일 경로가 올바르지 않을 때"() {
        when: "올바르지 않은 경로로 FileWordInserter 생성"
        new FileWordInserter("/INVALID_PATH/", testfile)

        then: "RuntimeException 발생"
        thrown RuntimeException
    }
}
