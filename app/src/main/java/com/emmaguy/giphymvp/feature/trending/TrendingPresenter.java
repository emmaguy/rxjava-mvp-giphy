package com.emmaguy.giphymvp.feature.trending;

import android.util.Log;

import com.emmaguy.giphymvp.common.base.BasePresenter;
import com.emmaguy.giphymvp.common.base.PresenterView;
import com.emmaguy.giphymvp.feature.trending.api.Gif;

import java.util.List;

import rx.Observable;
import rx.Scheduler;

class TrendingPresenter extends BasePresenter<TrendingPresenter.View> {
    private final TrendingManager trendingManager;
    private final Scheduler uiScheduler;
    private final Scheduler ioScheduler;

    TrendingPresenter(TrendingManager trendingManager, Scheduler uiScheduler, Scheduler ioScheduler) {
        this.trendingManager = trendingManager;
        this.uiScheduler = uiScheduler;
        this.ioScheduler = ioScheduler;
    }

    @Override
    public void onViewAttached(View view) {
        super.onViewAttached(view);

        addToAutoUnsubscribe(view.onRefreshAction()
                .startWith((Void) null)
                .doOnNext(ignored -> view.showLoading())
                .switchMap(ignored -> trendingManager.getTrendingGifs()
                        .subscribeOn(ioScheduler))
                .observeOn(uiScheduler)
                .doOnNext(ignored -> view.hideLoading())
                .subscribe(model -> {
                    if (model.success()) {
                        view.showTrendingGifs(model.gifs());
                    } else {
                        view.showError();
                    }
                }, e -> Log.e("TrendingPresenter", "Failed to refresh", e)));

        addToAutoUnsubscribe(view.onGifClicked().subscribe(view::openGifScreen));
    }

    interface View extends PresenterView {
        Observable<Void> onRefreshAction();
        Observable<Gif> onGifClicked();

        void showTrendingGifs(List<Gif> gifs);

        void showError();

        void showLoading();
        void hideLoading();

        void openGifScreen(Gif gif);
    }
}
