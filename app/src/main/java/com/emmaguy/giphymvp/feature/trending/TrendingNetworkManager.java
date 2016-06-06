package com.emmaguy.giphymvp.feature.trending;

import android.support.annotation.NonNull;
import android.util.Log;

import com.emmaguy.giphymvp.common.Funcs;
import com.emmaguy.giphymvp.common.Results;
import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;
import com.emmaguy.giphymvp.feature.trending.api.TrendingGifsResponse;
import com.jakewharton.rxrelay.PublishRelay;

import java.util.List;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.Scheduler;
import rx.subscriptions.CompositeSubscription;

class TrendingNetworkManager {
    private final PublishRelay<Void> refreshRelay = PublishRelay.create();

    private final PublishRelay<LoadingState> loadingStateRelay = PublishRelay.create();
    private final PublishRelay<List<Gif>> trendingGifsRelay = PublishRelay.create();

    private final CompositeSubscription subscription = new CompositeSubscription();

    private final GiphyApi giphyApi;
    private final Scheduler ioScheduler;

    TrendingNetworkManager(@NonNull final GiphyApi giphyApi, @NonNull final Scheduler ioScheduler) {
        this.giphyApi = giphyApi;
        this.ioScheduler = ioScheduler;
    }

    @NonNull Observable<LoadingState> onLoadingStateChanged() {
        return loadingStateRelay;
    }

    @NonNull PublishRelay<List<Gif>> onDataChanged() {
        return trendingGifsRelay;
    }

    void setup() {
        final Observable<Result<TrendingGifsResponse>> result = refreshRelay
                .doOnNext(ignored -> loadingStateRelay.call(LoadingState.LOADING))
                .flatMap(ignored -> giphyApi.latestTrending().subscribeOn(ioScheduler))
                .share();

        subscription.add(result.filter(Results.isSuccessful())
                .map(listResult -> listResult.response().body().gifs())
                .doOnNext(trendingGifsRelay::call)
                .subscribe(ignored -> loadingStateRelay.call(LoadingState.IDLE),
                        throwable -> Log.e("TrendingNetworkManager",
                                "Failed to parse and show latest trending gifs",
                                throwable)));

        subscription.add(result.filter(Funcs.not(Results.isSuccessful()))
                .subscribe(ignored -> loadingStateRelay.call(LoadingState.ERROR),
                        throwable -> Log.e("TrendingNetworkManager",
                                "Failed to retrieve latest trending gifs",
                                throwable)));
    }

    void refresh() {
        refreshRelay.call(null);
    }

    void teardown() {
        subscription.clear();
    }
}
