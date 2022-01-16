package com.billy.interview2;

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
