package com.ppojin.kafka.producer


import com.ppojin.kafka.producer.application.FileWordReader
import spock.lang.Specification

class ReadWordsImplTestReader extends Specification {
    String currentPath;

    void "setup" (){
        String packagePath = "/src/test/resources/static";
        String absolutePath = new File(".").getCanonicalPath()
        this.currentPath = absolutePath + packagePath
    }

    private FileWordReader getReader(String fileName){
        String filePath = this.currentPath + "/" + fileName
        return new FileWordReader(filePath);
    }

    void "첫줄 읽기" (){
        given:
        FileWordReader wordFile = getReader("firstLine.txt")

        when:
        String line1 = wordFile.readLine();

        then:
        assert line1 == "firstLine"
        println line1
    }

    void "빈 파일" (){
        given:
        FileWordReader wordFile = getReader("empty.txt")

        when:
        var line = wordFile.readLine();

        then:
        assert line == null;
        println line
    }
}
