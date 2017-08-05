package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.jakewharton.rxrelay2.BehaviorRelay;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;

class TrendingStorage {
    private final BehaviorRelay<List<Gif>> cache = BehaviorRelay.create();

    TrendingStorage() {
        cache.accept(Collections.emptyList());
    }

    Observable<List<Gif>> gifs() {
        return cache;
    }

    void saveGifs(List<Gif> gifs) {
        cache.accept(gifs);
    }
}
