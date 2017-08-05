package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;

import rx.Observable;

class TrendingManager {
    private final GiphyApi giphyApi;

    TrendingManager(GiphyApi giphyApi) {
        this.giphyApi = giphyApi;
    }

    Observable<TrendingGifsModel> getTrendingGifs() {
        return giphyApi.latestTrendingGifs()
                .map(result -> TrendingGifsModel.success(result.gifs()))
                .onErrorResumeNext(throwable -> Observable.just(TrendingGifsModel.failure()));
    }
}
