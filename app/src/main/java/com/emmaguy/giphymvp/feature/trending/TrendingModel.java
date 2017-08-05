package com.emmaguy.giphymvp.feature.trending;

import android.support.annotation.Nullable;

import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
abstract class TrendingModel {
    @Nullable abstract List<Gif> gifs();
    abstract boolean success();

    private static TrendingModel create(List<Gif> gifs, boolean success) {
        return new AutoValue_TrendingModel(gifs, success);
    }

    static TrendingModel success(List<Gif> gifs) {
        return create(gifs, true);
    }

    static TrendingModel failure() {
        return create(null, false);
    }
}
