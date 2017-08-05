package com.emmaguy.giphymvp.feature.trending.api;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface GiphyApi {
    @GET("v1/gifs/trending?api_key=dc6zaTOxFJmzC&rating=g&limit=50")
    Single<TrendingGifsResponse> latestTrendingGifs();
}
