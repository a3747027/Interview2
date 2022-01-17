package com.billy.interview2.solution3;

import com.billy.interview2.SongCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SongCacheImpl3 implements SongCache {
    private final ConcurrentHashMap<String, AtomicLong> songPlayNum;
    public SongCacheImpl3() {
        this.songPlayNum = new ConcurrentHashMap<>();
    }

    @Override
    public void recordSongPlays(String songId, int numPlays) {
        AtomicLong val = null;
        if(!songPlayNum.containsKey(songId)) {
            synchronized (songId.intern()) {
                if(!songPlayNum.containsKey(songId)) {
                    val = new AtomicLong(0);
                    songPlayNum.put(songId, val);
                }
            }
        }
        songPlayNum.get(songId).addAndGet(numPlays);
    }

    @Override
    public int getPlaysForSong(String songId) {
        if (songPlayNum.containsKey(songId)) {
            return songPlayNum.get(songId).intValue();
        }
        return -1;
    }

    @Override
    public List<String> getTopNSongsPlayed(int n) {
        Object[] arrayAllSongs =
                songPlayNum.entrySet().stream().sorted((e1, e2) -> e2.getValue().intValue() - e1.getValue().intValue())
                .map(Map.Entry::getKey).toArray();
        List<String> ans = new ArrayList<>();
        if (n==0) return ans;
        for (Object arrayAllSong : arrayAllSongs) {
            ans.add(arrayAllSong.toString());
            if(--n <=0) {
                break;
            }
        }
        return ans;
    }
}
