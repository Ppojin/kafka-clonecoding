package com.ppojin.kafka.topic;


public class Topic {
    private final Partition[] partitions;
    private final int partitionCount;

    public Topic(int partitionCount) {
        if (1 >= partitionCount || partitionCount >= 28) {
            throw new RuntimeException("Partition count must be integer between 1 < N < 28");
        }
        this.partitionCount = partitionCount;
        this.partitions = new Partition[partitionCount];
        for (var i = 0; i < partitionCount; i++) {
            this.partitions[i] = new Partition(i);
        }
    }

    public void send(String word) {
        int idx = findPartitionIndex(word);
        this.partitions[idx].add(word);
    }

    public ConsumerClient getConsumerClient() {
        for (var i = 0; i < partitions.length; i++) {
            if (!partitions[i].isBusy()) {
                partitions[i].setBusy(true);
                return new ConsumerClient(partitions[i]);
            }
        }
        return null;
    }

    final int findPartitionIndex(String word) {
        return Math.abs(word.toUpperCase().hashCode() % partitionCount);
    }

    public int getPartitionCount() {
        return partitionCount;
    }
}
