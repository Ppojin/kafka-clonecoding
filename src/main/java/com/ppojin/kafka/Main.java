package com.ppojin.kafka;

import com.ppojin.kafka.consumer.application.FileWordInserterFactory;
import com.ppojin.kafka.consumer.application.TopicWordConsumer;
import com.ppojin.kafka.consumer.domain.WordSync;
import com.ppojin.kafka.producer.application.FileWordReader;
import com.ppojin.kafka.producer.application.TopicWordProducer;
import com.ppojin.kafka.topic.Topic;
import com.ppojin.kafka.producer.domain.WordSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        MainArguments arguments = new MainArguments(args);
        log.info("::: application started :::");

        Topic topic = new Topic(arguments.getPartitionCount());

        WordSource wordSource = new WordSource(
                new FileWordReader(arguments.getFileName()),
                new TopicWordProducer(topic)
        );
        WordSync wordSync = new WordSync(
                new FileWordInserterFactory(arguments.getTargetDir()),
                new TopicWordConsumer(topic)
        );

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture.allOf(
                CompletableFuture
                        .supplyAsync(()->wordSource, executorService)
                        .thenAcceptAsync((source) -> {
                            source.start();
                            source.stop();
                        }, executorService)
                        .exceptionally((ex)->{
                            log.error("source job failed");
                            ex.printStackTrace();
                            return null;
                        }),
                CompletableFuture
                        .supplyAsync(()->wordSync, executorService)
                        .thenAcceptAsync((sync) -> {
                            sync.start();
                            sync.stop();
                        }, executorService)
                        .exceptionally((ex)->{
                            log.error("sync job failed");
                            ex.printStackTrace();
                            return null;
                        })
        ).join();
        executorService.shutdown();

        log.info("::: application complete :::");
    }

    public static class MainArguments {
        private final static Logger log = LoggerFactory.getLogger(MainArguments.class);
        private final String fileName;
        private final String targetDir;
        private final int partitionCount;

        public MainArguments(String... args) {
            if (args.length != 3) {
                log.error("args must be 3");
                throw new RuntimeException("args must be 3");
            }
            String fileName = args[0];
            String targetDir = args[1];
            int partitionCount;
            try {
                partitionCount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                log.error("Partition count must be integer between 1 < N < 28");
                e.printStackTrace();
                throw e;
            }

            this.fileName = fileName;
            this.targetDir = targetDir;
            this.partitionCount = partitionCount;
        }

        public String getFileName() {
            return fileName;
        }

        public String getTargetDir() {
            return targetDir;
        }

        public int getPartitionCount() {
            return partitionCount;
        }
    }
}
