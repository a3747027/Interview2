package com.billy.interview2;

import org.springframework.boot.SpringApplication;

public class Interview3Application {

    public static void main(String[] args) {
        SongCache cache = new SongCacheImpl();
        Thread threadTest1 = new Thread(new ThreadTest1(cache));
        Thread threadTest2 = new Thread(new ThreadTest2(cache));
        threadTest1.start();
        threadTest2.start();
    }

}
