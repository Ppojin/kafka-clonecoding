package com.ppojin.kafka.producer.application

import spock.lang.Specification

import java.nio.file.FileSystems

class FileWordReaderTest extends Specification {
    String currentPath = FileSystems.getDefault()
            .getPath(new File(".").getCanonicalPath() +
                    "/src/test/java/" +
                    FileWordReaderTest.class
                            .getPackageName()
                            .replaceAll(".", "/")
            )
            .toAbsolutePath()
            .toString()
    String fileName = "testFile";

    def deleteTestFile(){
        new File(currentPath, fileName + ".txt").delete()
    }

    def setup(){
        deleteTestFile()
    }

    def cleanup(){
        deleteTestFile()
    }

    def "FileWordReader 읽기 테스트"() {
        given: "테스트용 파일 작성"
        File testFile = new File(currentPath, fileName + ".txt")
        testFile.write("hello\nworld\n123\n")

        when: "FileWordReader를 사용하여 파일의 내용을 읽음"
        FileWordReader fileWordReader = new FileWordReader(testFile.path)
        String line1 = fileWordReader.readLine()
        String line2 = fileWordReader.readLine()
        String line3 = fileWordReader.readLine()
        String line4 = fileWordReader.readLine()

        then: "파일에서 읽어온 내용이 기대한 값과 일치함"
        line1 == "hello"
        line2 == "world"
        line3 == "123"
        line4 == null

        cleanup:
        fileWordReader.close()
    }

    def "FileWordReader 종료 확인"() {
        given: "테스트용 파일 작성"
        File testFile = new File(currentPath, fileName + ".txt")
        testFile.write("hello\nworld\n123\n")

        and: "FileWordReader 생성"
        FileWordReader fileWordReader = new FileWordReader(testFile.path)

        when: "FileWordReader 종료"
        fileWordReader.close()

        then: "정상적으로 종료됨"
        notThrown Exception
    }

    def "RuntimeException, 없는 경로"() {
        when: "없는 path 의 FileWordReader 생성할 때"
        RuntimeException e
        new FileWordReader("/INVALID_PATH/"+fileName+".txt")

        then: "RuntimeException 발생"
        thrown RuntimeException
    }

    def "RuntimeException, 잘못된 경로"() {
        when: "잘못된 path 의 FileWordReader 생성할 때"
        RuntimeException e
        new FileWordReader("/INVALID_PATH:/"+fileName+".txt")

        then: "RuntimeException 발생"
        thrown RuntimeException
    }
}
