package com.spotware.connect.protocol;


public final class ChannelMessageTimestamp {
    private final long create = System.nanoTime();
    private long start;
    private long finish;

    public ChannelMessageTimestamp() {
    }

    public void markStart() {
        this.start = System.nanoTime();
    }

    public void markFinish() {
        this.finish = System.nanoTime();
    }

    public long getCreate() {
        return this.create;
    }

    public long getStart() {
        return this.start;
    }

    public long getFinish() {
        return this.finish;
    }
}
