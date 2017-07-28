package com.emmaguy.giphymvp.feature.trending;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.emmaguy.giphymvp.common.base.BasePresenter;
import com.emmaguy.giphymvp.common.base.PresenterView;
import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.google.auto.value.AutoValue;

import java.util.List;

import rx.Observable;
import rx.Scheduler;

import static com.emmaguy.giphymvp.feature.trending.LoadingState.ERROR;
import static com.emmaguy.giphymvp.feature.trending.LoadingState.IDLE;
import static com.emmaguy.giphymvp.feature.trending.LoadingState.LOADING;

class TrendingPresenter extends BasePresenter<TrendingPresenter.View> {
    private final TrendingNetworkManager trendingNetworkManager;
    private final Scheduler uiScheduler;

    TrendingPresenter(@NonNull final TrendingNetworkManager trendingNetworkManager,
                      @NonNull final Scheduler uiScheduler) {
        this.trendingNetworkManager = trendingNetworkManager;
        this.uiScheduler = uiScheduler;
    }

    @Override public void onViewAttached(@NonNull final View view) {
        super.onViewAttached(view);

        trendingNetworkManager.setup();

        addToAutoUnsubscribe(Observable.combineLatest(trendingNetworkManager.onLoadingStateChanged(),
                trendingNetworkManager.onDataChanged().startWith(Observable.just(null)),
                LoadingStateWithData::create)
                .observeOn(uiScheduler)
                .subscribe(loadingStateWithData -> {
                    final LoadingState loadingState = loadingStateWithData.loadingState();
                    final List<Gif> data = loadingStateWithData.trendingGifs();

                    if (loadingState == LOADING) {
                        if (data == null) {
                            view.showLoading();
                        } else {
                            view.showIncrementalLoading();
                        }
                    } else {
                        view.hideLoading();
                        view.hideIncrementalLoading();

                        if (loadingState == IDLE) {
                            if (data == null) {
                                view.showEmpty();
                            } else {
                                view.setTrendingGifs(data);
                            }
                        } else if (loadingState == ERROR) {
                            if (data == null) {
                                view.showError();
                            } else {
                                view.showIncrementalError();
                            }
                        }
                    }
                }, e -> Log.e("TrendingPresenter", "Failed to update UI", e)));

        addToAutoUnsubscribe(view.onRefreshAction()
                .startWith(Observable.just(null))
                .subscribe(
                        ignored -> trendingNetworkManager.refresh(),
                        e -> Log.e("TrendingPresenter", "Failed to refresh", e)));

        addToAutoUnsubscribe(view.onGifClicked().subscribe(view::goToGif));
    }

    @Override public void onViewDetached() {
        trendingNetworkManager.teardown();

        super.onViewDetached();
    }

    interface View extends PresenterView {
        @NonNull Observable<Void> onRefreshAction();
        @NonNull Observable<Gif> onGifClicked();

        void setTrendingGifs(@NonNull final List<Gif> gifs);

        void showEmpty();

        void showError();
        void showIncrementalError();

        void showLoading();
        void hideLoading();

        void showIncrementalLoading();
        void hideIncrementalLoading();

        void goToGif(@NonNull final Gif gif);
    }

    @AutoValue abstract static class LoadingStateWithData {
        static LoadingStateWithData create(@NonNull final LoadingState loadingState,
                                           @Nullable final List<Gif> gifs) {
            return new AutoValue_TrendingPresenter_LoadingStateWithData(loadingState, gifs);
        }

        abstract LoadingState loadingState();

        @Nullable abstract List<Gif> trendingGifs();
    }
}
