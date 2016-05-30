package com.emmaguy.giphymvp.feature.trending.api;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import rx.Observable;

public interface GiphyApi {
    @GET("v1/gifs/trending?api_key=dc6zaTOxFJmzC&rating=g")
    Observable<Result<TrendingGifsResponse>> latestTrending();
}
