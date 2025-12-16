package com.sleekydz86.service.commu.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CommunityMetrics {

    private final Counter boardPostsCreated;
    private final Counter boardPostsRead;
    private final Counter boardListQueries;
    private final Timer boardPostProcessingTime;
    private final Timer boardQueryTime;
    private final MeterRegistry meterRegistry;

    public CommunityMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.boardPostsCreated = Counter.builder("community.board.posts.created")
                .description("Number of board posts created")
                .register(meterRegistry);

        this.boardPostsRead = Counter.builder("community.board.posts.read")
                .description("Number of board posts read")
                .register(meterRegistry);

        this.boardListQueries = Counter.builder("community.board.list.queries")
                .description("Number of board list queries")
                .register(meterRegistry);

        this.boardPostProcessingTime = Timer.builder("community.board.post.processing.time")
                .description("Time taken to process board post")
                .register(meterRegistry);

        this.boardQueryTime = Timer.builder("community.board.query.time")
                .description("Time taken to query board")
                .register(meterRegistry);
    }

    public void incrementBoardPostsCreated() {
        boardPostsCreated.increment();
    }

    public void incrementBoardPostsRead() {
        boardPostsRead.increment();
    }

    public void incrementBoardListQueries() {
        boardListQueries.increment();
    }

    public void recordBoardPostProcessingTime(long time, TimeUnit unit) {
        boardPostProcessingTime.record(time, unit);
    }

    public void recordBoardQueryTime(long time, TimeUnit unit) {
        boardQueryTime.record(time, unit);
    }

    public Timer.Sample startBoardPostProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer.Sample startBoardQueryTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer getBoardPostProcessingTime() {
        return boardPostProcessingTime;
    }

    public Timer getBoardQueryTime() {
        return boardQueryTime;
    }
}
