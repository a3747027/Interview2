package com.billy.interview2;

import com.billy.interview2.solution1.SongCacheImpl1;
import com.billy.interview2.solution2.SongCacheImpl2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ResultTest {
    //use @BeforeAll to initalize
    static ExecutorService pool;

    static SongCache cache;

    @BeforeAll
    static void initExecutors()
    {
        ExecutorService pool = Executors.newCachedThreadPool();
    }

    //use @AfterAll to shutdown thread pool
    @AfterAll
    public static void close() {
        pool.shutdown();
    }
        //use @BeforeEach to initialize
    @BeforeEach
    public void setUp(){
        cache = new SongCacheImpl1();
    }
    @Test
    public void cacheIsWorking() {
        //SongCache cache = new SongCacheImpl1();
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);

        System.out.println(cache.getPlaysForSong("ID-1") == 4);
        System.out.println(cache.getPlaysForSong("ID-9") == -1);
        System.out.println(cache.getTopNSongsPlayed(2).contains("ID-3"));
        System.out.println(cache.getTopNSongsPlayed(2).contains("ID-1"));
        System.out.println(cache.getTopNSongsPlayed(0).isEmpty());
    }

    @RepeatedTest(10)
    public static void multiThreadingTest() {
        //SongCache cache = new SongCacheImpl2();
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
//        System.out.println(cache.getPlaysForSong("ID-1") == 4);
//        System.out.println(cache.getPlaysForSong("ID-9") == -1);
//        System.out.println(cache.getTopNSongsPlayed(2).contains("ID-3"));
//        System.out.println(cache.getTopNSongsPlayed(2).contains("ID-1"));
//        System.out.println(cache.getTopNSongsPlayed(0).isEmpty());
    }
}