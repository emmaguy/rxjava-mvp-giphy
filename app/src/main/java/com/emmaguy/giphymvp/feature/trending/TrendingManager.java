package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;
import com.emmaguy.giphymvp.feature.trending.api.TrendingGifsResponse;

import io.reactivex.Observable;
import io.reactivex.Single;

class TrendingManager {
    private final GiphyApi giphyApi;
    private final TrendingStorage trendingStorage;

    TrendingManager(GiphyApi giphyApi, TrendingStorage trendingStorage) {
        this.giphyApi = giphyApi;
        this.trendingStorage = trendingStorage;
    }

    Observable<TrendingModel> getTrendingGifs() {
        return trendingStorage.gifs()
                .flatMap(gifs -> {
                    if (gifs == null || gifs.isEmpty()) {
                        return giphyApi.latestTrendingGifs()
                                .map(TrendingGifsResponse::gifs)
                                .doOnSuccess(trendingStorage::saveGifs)
                                .map(TrendingModel::success)
                                .onErrorResumeNext(throwable -> Single.just(TrendingModel.failure()))
                                .toObservable();
                    }
                    return Observable.just(TrendingModel.success(gifs));
                });
    }
}
