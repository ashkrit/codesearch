package org.search.codesearch.metrics;

import java.util.concurrent.atomic.LongAdder;

public class SearchMetrics {

    private final LongAdder filesVisited = new LongAdder();
    private final LongAdder filesProcessed = new LongAdder();
    private final LongAdder bytesRead = new LongAdder();

    public void recordFileProcessed() {
        filesProcessed.increment();
    }

    public void recordFileVisited() {
        filesVisited.increment();
    }

    public void recordBytesRead(long bytes) {
        bytesRead.add(bytes);
    }

    public long filesProcessed() {
        return filesProcessed.longValue();
    }

    public long filesVisited() {
        return filesVisited.longValue();
    }

    public long bytesRead() {
        return bytesRead.longValue();
    }
}
