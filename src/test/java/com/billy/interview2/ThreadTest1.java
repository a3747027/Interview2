package com.billy.interview2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class ThreadTest1 implements Runnable{
    private SongCache cache;

    public ThreadTest1(SongCache cache) {
        this.cache = cache;
    }

    @Override
    public void run() {
        try {
            cache.recordSongPlays("ID-1", 3);
            cache.recordSongPlays("ID-1", 1);
            cache.recordSongPlays("ID-2", 2);
            cache.recordSongPlays("ID-3", 5);
            assertThat(cache.getPlaysForSong("ID-1"), is(4));
            assertThat(cache.getPlaysForSong("ID-9"), is(-1));
            assertThat(cache.getTopNSongsPlayed(2), contains("ID-3",
                    "ID-1"));
            assertThat(cache.getTopNSongsPlayed(0), is(empty()));
            System.out.println("Thread1 waiting");
            wait(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            notifyAll();
            System.out.println("Thread1 going");
            assertThat(cache.getTopNSongsPlayed(2), contains("ID-9",
                    "ID-3"));
        }

    }
}
