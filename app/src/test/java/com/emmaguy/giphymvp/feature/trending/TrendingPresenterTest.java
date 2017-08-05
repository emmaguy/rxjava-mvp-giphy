package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.common.base.BasePresenterTest;
import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.jakewharton.rxrelay.PublishRelay;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrendingPresenterTest extends BasePresenterTest<TrendingPresenter, TrendingPresenter.View> {
    private final PublishRelay<Void> refreshRelay = PublishRelay.create();
    private final PublishRelay<Gif> gifClickedRelay = PublishRelay.create();

    @Mock private TrendingManager trendingManager;

    @Before
    public void setUp() throws Exception {
        List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        when(trendingManager.getTrendingGifs()).thenReturn(Observable.just(TrendingGifsModel.success(gifs)));
    }

    @Override
    protected TrendingPresenter createPresenter() {
        return new TrendingPresenter(trendingManager, Schedulers.trampoline(), Schedulers.trampoline());
    }

    @Override
    protected TrendingPresenter.View createView() {
        final TrendingPresenter.View view = mock(TrendingPresenter.View.class);
        when(view.onRefreshAction()).thenReturn(refreshRelay);
        when(view.onGifClicked()).thenReturn(gifClickedRelay);
        return view;
    }

    @Test
    public void presenterOnViewAttached_showLoading() throws Exception {
        presenterOnViewAttached();

        verify(view).showLoading();
    }

    @Test
    public void presenterOnViewAttached_showThenHideLoading() throws Exception {
        presenterOnViewAttached();

        InOrder inOrder = inOrder(view);
        inOrder.verify(view).showLoading();
        inOrder.verify(view).hideLoading();
    }

    @Test
    public void presenterOnViewAttached_success_showTrendingGifs() throws Exception {
        presenterOnViewAttached();

        verify(view).showTrendingGifs(any());
    }

    @Test
    public void presenterOnViewAttached_failure_showError() throws Exception {
        when(trendingManager.getTrendingGifs()).thenReturn(Observable.just(TrendingGifsModel.failure()));
        presenterOnViewAttached();

        verify(view).showError();
    }

    @Test
    public void onGifClicked_goToGif() throws Exception {
        presenterOnViewAttached();

        gifClickedRelay.call(mock(Gif.class));
    }
}