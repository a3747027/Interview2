package com.billy.interview2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SongCacheImpl implements SongCache{
    private ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<>();
    public SongCacheImpl() {}

    @Override
    public void recordSongPlays(String songId, int numPlays) {
        cache.put(songId, cache.getOrDefault(songId,0)+numPlays);
    }

    @Override
    public int getPlaysForSong(String songId) {
        return cache.getOrDefault(songId,-1);
    }

    @Override
    public List<String> getTopNSongsPlayed(int n) {
        List<String> sortList= cache.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue()- e1.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<String> ans = new ArrayList<>();
        for (int i = 0; i<sortList.size() && i < n; i++) {
            ans.add(sortList.get(i));
        }
        return ans;
    }
}
