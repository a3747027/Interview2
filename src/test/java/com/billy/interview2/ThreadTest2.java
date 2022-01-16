package com.billy.interview2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class ThreadTest2 implements Runnable{

    private SongCache cache;

    public ThreadTest2(SongCache cache) {
        this.cache = cache;
    }
    @Override
    public void run() {
        try {
            System.out.println("Thread2 waiting");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread2 going");
        cache.recordSongPlays("ID-9", 10);
    }
}
