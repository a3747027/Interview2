package com.billy.interview2.solution3;

import com.billy.interview2.SongCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SongCacheImpl3 implements SongCache {
    //using lazy loading, final for?
    //difference between AtomicLong and AtomicInteger, should I use LongAdder?
    //difference between instance in scope or instance in constructor?
    private final ConcurrentHashMap<String, AtomicLong> cache;
    public SongCacheImpl3() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public void recordSongPlays(String songId, int numPlays) {
        //https://blog.csdn.net/qq_34115899/article/details/83018870
        //use addAndGet to achieve thread safe
        if (cache.containsKey(songId)) {
                cache.get(songId).addAndGet(numPlays);
        } else {
            //is there any situation to make the key of string changed?
            //https://www.cnblogs.com/Qian123/p/5707154.html
            //.intern(): without it, will check the string pool exist "xxxx" first.
            // with this to guarantee the string is in the string pool.
            // new String("aaa") != "aaa", (new String("aaa")).intern() == "aaa", new String("aaa") != (new String("aaa")).intern()
            // if it is in string constant pool...
            synchronized (songId.intern()) {
                cache.put(songId, new AtomicLong(numPlays));
            }
        }
        //cache.put(songId, cache.getOrDefault(songId,0)+numPlays);
    }

    @Override
    public int getPlaysForSong(String songId) {
        //internal operation of ConcurrentHashmap is volatile include Node, Table and .....
        if (cache.containsKey(songId)) {
            return cache.get(songId).intValue();
        }
        return -1;
    }

    @Override
    public List<String> getTopNSongsPlayed(int n) {
        if(n < 0) {
            throw new IllegalArgumentException("cannot get top songs played with input number : " + n);
        }
        //is stream atomic?
        Object[] arrayAllSongs =
                cache.entrySet().stream().sorted((e1, e2) -> e2.getValue().intValue() - e1.getValue().intValue())
                .map(Map.Entry::getKey).toArray();
        List<String> ans = new ArrayList<>();
        if (n==0) return ans;
        for (Object arrayAllSong : arrayAllSongs) {
            // if put in here, ex: 2 : 2 do add, 1 do add, 0 break (but wont do add at 0)
//            if (--n <=0) {
//                break;
//            }
            ans.add(arrayAllSong.toString());
            // count back, ex: 2 : do add 2, do add 1, do add 0 break???
            if(--n <=0) {
                break;
            }
        }
        return ans;
    }
}
