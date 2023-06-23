package com.ppojin.kafka.consumer.domain;

import com.ppojin.kafka.consumer.application.FileWordInserter;
import com.ppojin.kafka.consumer.interfaces.WordConsumer;
import com.ppojin.kafka.consumer.interfaces.WordInserter;
import com.ppojin.kafka.consumer.interfaces.WordInserterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class WordSync {
    private final static Logger log = LoggerFactory.getLogger(FileWordInserter.class);

    private final Set<String> numericWord;

    private final WordInserterFactory factory;
    private final Map<String, WordInserter> wordInserterMap;

    private final int partitionCount;
    private final WordConsumer consumers;

    public WordSync(WordInserterFactory factory, WordConsumer consumer) {
        this.numericWord = Set.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        this.wordInserterMap = new HashMap<>();

        this.factory = factory;
        this.partitionCount = consumer.getPartitionCount();
        this.consumers = consumer;
    }

    public void start() {
        final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                partitionCount, partitionCount,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(partitionCount)
        );

        final Consumer<WordConsumer> pollAndInsertConsumer = (WordConsumer consumer) -> {
            log.info(threadLogPrefix(executorService) + "start consuming");
            long startTime = System.currentTimeMillis();
            int consumeCounter = 0;

            Iterable<String> iterable = consumer.poll();
            if(iterable == null){
                return;
            }

            for (String word : iterable) {
                if (word == null) {
                    break;
                }
                consumeCounter++;

                WordInserter inserter = getWordInserter(word);
                if (inserter.isDuplicate(word)) {
                    log.warn(threadLogPrefix(executorService) + "word '{}' is duplicated", word);
                } else {
                    inserter.insertLine(word);
                    log.debug(threadLogPrefix(executorService) + "word '{}' inserted", word);
                }
            }

            log.info(threadLogPrefix(executorService) + "'{}' messages consume completed! ({}ms)",
                    consumeCounter, System.currentTimeMillis() - startTime
            );
        };

        var completableFutures = new CompletableFuture[partitionCount];
        for (int i = 0; i < partitionCount; i++) {
            completableFutures[i] = CompletableFuture
                    .supplyAsync(() -> consumers)
                    .thenAcceptAsync(pollAndInsertConsumer, executorService)
                    .exceptionally((ex)->{
                        log.error("consume partition failed");
                        ex.printStackTrace();
                        return null;
                    });
        }

        CompletableFuture
                .allOf(completableFutures)
                .join();

        executorService.shutdown();
    }


    public void stop() {
        wordInserterMap.values()
                .forEach(WordInserter::close);
    }

    private WordInserter getWordInserter(String word) {
        String target = getTarget(word);
        if (wordInserterMap.containsKey(target)) {
            return wordInserterMap.get(target);
        } else {
            WordInserter wordInserter = factory.getWordInserter(target);
            wordInserterMap.put(target, wordInserter);
            return wordInserter;
        }
    }

    private String getTarget(String word) {
        String c = word.toUpperCase().substring(0, 1);
        if (numericWord.contains(c)) {
            return "number";
        } else {
            return c;
        }
    }

    private String threadLogPrefix(ThreadPoolExecutor executorService) {
        return String.format("(%d pool | %d thread | %d active) ",
                executorService.getCorePoolSize(),
                executorService.getTaskCount(),
                executorService.getActiveCount()
        );
    }
}
