package com.billy.interview2;

public class ThreadTest1 implements Runnable{
    private SongCache cache;

    public ThreadTest1(SongCache cache) {
        this.cache = cache;
    }

    @Override
    public void run() {
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);
        System.out.println(cache.getPlaysForSong("ID-1")==4);
        System.out.println(cache.getTopNSongsPlayed(0).isEmpty());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(cache.getPlaysForSong("ID-9")==-1);
        System.out.println(cache.getTopNSongsPlayed(2));//"ID-3","ID-1"
        System.out.println(cache.getTopNSongsPlayed(2));//"ID-9", "ID-3"
    }
}
