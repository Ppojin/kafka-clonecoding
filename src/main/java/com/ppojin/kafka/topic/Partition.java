package com.ppojin.kafka.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public class Partition {
    private final static Logger log = LoggerFactory.getLogger(Partition.class);
    private final Queue<String> queue;
    private final int index;
    private boolean busy;

    public Partition(int index) {
        this.busy = false;
        this.index = index;
        this.queue = new LinkedList<>();
    }

    public void add(String word) {
        log.debug("partition_{}({}): {}", index, queue.size(), word);
        queue.add(word);
    }

    public String poll() {
        return queue.poll();
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
