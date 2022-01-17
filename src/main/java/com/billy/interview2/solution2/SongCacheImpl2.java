package com.billy.interview2.solution2;

import com.billy.interview2.SongCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SongCacheImpl2 implements SongCache {
    private final Map<String, AtomicLong> songPlayNum;

    public SongCacheImpl2() {
        this.songPlayNum = new ConcurrentHashMap<>();
    }

    /*
        if we use get() + put() , it's not thread safe even with concurrent hashmap because they have 2 steps
        so we can use atomic integer to count, but the initialization is not thread safe we need synchronzied
     */
    @Override
    public void recordSongPlays(String songId, int numPlays) {
        AtomicLong val = null;
        if(!songPlayNum.containsKey(songId)) {
            //we only synchronized current songid string, instead of synchronized whole map or method
            //and we use double check way to initialize our atomic long obj
            //we synchronized string.intern() to avoid different instance of string
            //string may not be stored in constant string pool yet if we use new String(char[])
            //using double check to achieve the thread safe.
            synchronized (songId.intern()) {
                if(!songPlayNum.containsKey(songId)) {
                    val = new AtomicLong(0);
                    songPlayNum.put(songId, val);
                }
            }
        }
        songPlayNum.get(songId).addAndGet(numPlays);
    }

    /*
        1.recommend long as return type;
        2.we don't need synchronized because concurrent hashmap is using volatile and get is thread safe
          get method will return data with current moment because of volatile happen before rule
          and we don't have other operation, we only get value from concurrent hashmap
    */
    @Override
    public int getPlaysForSong(String songId) {
        if(!songPlayNum.containsKey(songId)) {
            return -1;
        }
        return (int)songPlayNum.get(songId).get();
    }

    /*
        we don't use synchronized because it's slow
        and we use fail-safe iterator from ConcurrentHashmap to loop all entries, and copy data into new collection
        then get top n
        problems with this solution:
            1. it may consume large amount of space or time if we have too many different songs
                if we get too many different songs, we can use solution1
            2. result may not be accurate, even with fail safe, but people keep updating concurrent hashmap
                so we may get new data that didn't exist in last moment
     */
    @Override
    public List<String> getTopNSongsPlayed(int n) {
        //Object[]{SongId, frequency}
        //we can create another node class instead of object array, but object array can save space
        TreeMap<Long, List<String>> freqRank = new TreeMap<>(Comparator.reverseOrder());
        List<String> ans = new ArrayList<>();
        for(Map.Entry<String, AtomicLong> e: songPlayNum.entrySet()) {
            long freq = e.getValue().get();
            String songId = e.getKey();
            if(!freqRank.containsKey(freq)) {
                freqRank.put(freq, new ArrayList<>());
            }
            freqRank.get(freq).add(songId);
            //mock priority queue style, polling out smallest rank/freq from treemap
            //because that rank/freq is not the answer if treemap size > n
            if(freqRank.size() > n) {
                freqRank.remove(freqRank.lastKey());
            }
        }
        for(List<String> songs: freqRank.values()) {
            ans.addAll(songs);
        }
        return ans;
    }
}
