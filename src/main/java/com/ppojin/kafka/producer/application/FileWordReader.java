package com.ppojin.kafka.producer.application;

import com.ppojin.kafka.producer.interfaces.WordReader;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class FileWordReader implements WordReader {
    private final FileReader fileReader;
    private final BufferedReader bufferedReader;
    private final String sourceFileName;

    public FileWordReader(String sourceFileName) {
        this.sourceFileName = sourceFileName;
        try {
            Paths.get(sourceFileName);
            this.fileReader = new FileReader(sourceFileName);
            this.bufferedReader = new BufferedReader(fileReader);
        } catch (InvalidPathException e) {
            log.error("sourceFileName '{}' is invalid: {}", sourceFileName, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("'{}' cannot access File: {}", sourceFileName, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String readLine() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            log.error("'{}' cannot access File: {}", sourceFileName, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void close() {
        try {
            fileReader.close();
        } catch (IOException e) {
            log.error("'{}' cannot access File: {}", sourceFileName, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
