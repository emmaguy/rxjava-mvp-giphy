package com.emmaguy.giphymvp.feature.trending;

import android.support.annotation.Nullable;

import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
abstract class TrendingGifsModel {
    @Nullable abstract List<Gif> gifs();
    abstract boolean success();

    private static TrendingGifsModel create(List<Gif> gifs, boolean success) {
        return new AutoValue_TrendingGifsModel(gifs, success);
    }

    static TrendingGifsModel success(List<Gif> gifs) {
        return create(gifs, true);
    }

    static TrendingGifsModel failure() {
        return create(null, false);
    }
}
