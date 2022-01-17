package com.billy.interview2.solution1;

import com.billy.interview2.SongCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SongCacheImpl1 implements SongCache {

    private final TreeMap<Long, LinkedHashSet<String>> freqRank;
    private final Map<String, Long> songPlayNum;

    public SongCacheImpl1() {
        this.freqRank = new TreeMap<>(Comparator.reverseOrder());
        this.songPlayNum = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void recordSongPlays(String songId, int numPlays) {
        if(numPlays < 0) {
            throw new IllegalArgumentException(songId + " song input numPlays is not valid : " + numPlays);
        }
        long preFreq = songPlayNum.getOrDefault(songId, 0l);
        long nextFreq = preFreq + numPlays;
        if(nextFreq < 0) {
            throw new IllegalArgumentException("song play counts overflow");
        }
        if(preFreq != 0) {
            freqRank.get(preFreq).remove(songId);
        }
        freqRank.putIfAbsent(nextFreq, new LinkedHashSet<>());
        freqRank.get(nextFreq).add(songId);
        songPlayNum.put(songId, nextFreq);
    }

    /*
        1.recommend long as return type;
        2.we don't need synchronized because concurrent hashmap is using volatile and get is thread safe
          get method will return data with current moment because of volatile happen before rule
          and we don't have other operation, we only get value from concurrent hashmap
    */
    @Override
    public int getPlaysForSong(String songId) {
        return songPlayNum.getOrDefault(songId, -1L).intValue();
    }

    @Override
    public synchronized List<String> getTopNSongsPlayed(int n) {
        if(n < 0) {
            throw new IllegalArgumentException("cannot get top songs played with input number : " + n);
        }
        List<String> ans = new ArrayList<>();
        if(n == 0) {
            return ans;
        }
        for(Set<String> songs: freqRank.values()) {
            ans.addAll(songs);
            if(--n <= 0) {
                break;
            }
        }
        return ans;
    }
}

