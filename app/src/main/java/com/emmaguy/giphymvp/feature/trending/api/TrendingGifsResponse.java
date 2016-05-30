package com.emmaguy.giphymvp.feature.trending.api;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue public abstract class TrendingGifsResponse {
    public static TrendingGifsResponse create(final List<Gif> gifs) {
        return new AutoValue_TrendingGifsResponse(gifs);
    }

    public static JsonAdapter<TrendingGifsResponse> typeAdapter(final Moshi moshi) {
        return new AutoValue_TrendingGifsResponse.MoshiJsonAdapter(moshi);
    }

    @Json(name = "data") public abstract List<Gif> gifs();
}
