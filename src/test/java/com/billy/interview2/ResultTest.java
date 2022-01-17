package com.billy.interview2;

import com.billy.interview2.solution1.SongCacheImpl1;
import com.billy.interview2.solution2.SongCacheImpl2;
import com.billy.interview2.solution3.SongCacheImpl3;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ResultTest {

    private static ExecutorService pool;

    @BeforeAll
    public static void initExecutors() {
        pool = Executors.newCachedThreadPool();
    }

    @AfterAll
    public static void close() {
        pool.shutdown();
    }

    private static Stream<SongCache> implResource() {
        return Stream.of(
                new SongCacheImpl1(),
                new SongCacheImpl2(),
                new SongCacheImpl3()
        );
    }

    @ParameterizedTest
    @MethodSource("implResource")
    public void singleThreadCacheTest(SongCache cache) throws NullPointerException{
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);

        Assertions.assertEquals(cache.getPlaysForSong("ID-1"),4);
        Assertions.assertEquals(cache.getPlaysForSong("ID-9"),-1);
        Assertions.assertTrue(cache.getTopNSongsPlayed(2).contains("ID-3"));
        Assertions.assertTrue(cache.getTopNSongsPlayed(2).contains("ID-1"));
        Assertions.assertTrue(cache.getTopNSongsPlayed(0).isEmpty());
    }

    /*
        currently junit 5 doesn't support @ParameterizedTest with @RepeatedTest together
        they have issue tracker for this problem but not resolving it yet.
     */
    @RepeatedTest(100)
    public void testSongCacheImpl1() {
        multiThreadingTestHelper(new SongCacheImpl1());
    }

    @RepeatedTest(100)
    public void testSongCacheImpl2() {
        multiThreadingTestHelper(new SongCacheImpl2());
    }

    @RepeatedTest(100)
    public void testSongCacheImpl3() {
        multiThreadingTestHelper(new SongCacheImpl3());
    }

    private void multiThreadingTestHelper(SongCache cache) {
        List<CompletableFuture> futures = new ArrayList<>();
        futures.add(CompletableFuture.runAsync(() -> cache.recordSongPlays("ID-1", 3), pool));
        futures.add(CompletableFuture.runAsync(() -> cache.recordSongPlays("ID-1", 1), pool));
        futures.add(CompletableFuture.runAsync(() -> cache.recordSongPlays("ID-2", 2), pool));
        futures.add(CompletableFuture.runAsync(() -> cache.recordSongPlays("ID-3", 5), pool));
        CompletableFuture[] futuresArray = futures.toArray(new CompletableFuture[0]);
        CompletableFuture.allOf(futuresArray)
                .orTimeout(2, TimeUnit.SECONDS).join();
        Assertions.assertEquals(cache.getPlaysForSong("ID-1"),4);
        Assertions.assertEquals(cache.getPlaysForSong("ID-9"),-1);
        Assertions.assertTrue(cache.getTopNSongsPlayed(2).contains("ID-3"));
        Assertions.assertTrue(cache.getTopNSongsPlayed(2).contains("ID-1"));
        Assertions.assertTrue(cache.getTopNSongsPlayed(0).isEmpty());
    }
}