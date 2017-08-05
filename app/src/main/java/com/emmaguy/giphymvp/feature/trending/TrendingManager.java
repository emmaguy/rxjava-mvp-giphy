package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;

import io.reactivex.Single;

class TrendingManager {
    private final GiphyApi giphyApi;

    TrendingManager(GiphyApi giphyApi) {
        this.giphyApi = giphyApi;
    }

    Single<TrendingGifsModel> getTrendingGifs() {
        return giphyApi.latestTrendingGifs()
                .map(result -> TrendingGifsModel.success(result.gifs()))
                .onErrorResumeNext(throwable -> Single.just(TrendingGifsModel.failure()));
    }
}
