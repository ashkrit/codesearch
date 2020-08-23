package org.search.codesearch.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class IndexMetrics {
    public final AtomicLong noOfFiles = new AtomicLong();
    public final AtomicLong noOfLines = new AtomicLong();
    public final AtomicLong noOfFunction = new AtomicLong();
    public final AtomicLong noOfVariable = new AtomicLong();
    public final AtomicLong noOfClass = new AtomicLong();

    public void recordFunction(long value) {
        noOfFunction.addAndGet(value);
    }


    @Override
    public String toString() {
        return String.format(
                "%s{noOfFiles=%d, noOfLines=%d, noOfFunction=%d , noOfVariable=%d}",
                this.getClass().getSimpleName(),
                noOfFiles.get(),
                noOfLines.get(),
                noOfFunction.get(),
                noOfVariable.get()
        );
    }

    public void recordVariable(long count) {
        noOfVariable.addAndGet(count);
    }

    public void recordLines(long count) {
        noOfLines.addAndGet(count);
    }
}
