package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.common.base.BasePresenterTest;
import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.jakewharton.rxrelay.PublishRelay;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import rx.schedulers.Schedulers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrendingPresenterTest
        extends BasePresenterTest<TrendingPresenter, TrendingPresenter.View> {
    private final PublishRelay<List<Gif>> dataChangedRelay = PublishRelay.create();
    private final PublishRelay<LoadingState> loadingStateChangedRelay = PublishRelay.create();
    private final PublishRelay<Void> refreshRelay = PublishRelay.create();
    private final PublishRelay<Gif> gifClickedRelay = PublishRelay.create();

    @Mock private TrendingNetworkManager networkManager;

    @Before public void setUp() throws Exception {
        when(networkManager.onDataChanged()).thenReturn(dataChangedRelay);
        when(networkManager.onLoadingStateChanged()).thenReturn(loadingStateChangedRelay);
    }

    @Override protected TrendingPresenter createPresenter() {
        return new TrendingPresenter(networkManager, Schedulers.immediate());
    }

    @Override protected TrendingPresenter.View createView() {
        final TrendingPresenter.View view = mock(TrendingPresenter.View.class);
        when(view.onRefreshAction()).thenReturn(refreshRelay);
        when(view.onGifClicked()).thenReturn(gifClickedRelay);
        return view;
    }

    @Test public void onViewAttached_setupNetworkManager() throws Exception {
        presenterOnViewAttached();

        verify(networkManager).setup();
    }

    @Test public void onViewDetached_teardownNetworkManager() throws Exception {
        presenterOnViewAttached();
        presenterOnViewDetached();

        verify(networkManager).teardown();
    }

    @Test public void onViewAttached_performsRefresh() throws Exception {
        presenterOnViewAttached();

        verify(networkManager).refresh();
    }

    @Test public void onRefreshAction_performsRefresh() throws Exception {
        presenterOnViewAttached();

        refreshRelay.call(null);

        verify(networkManager, times(2)).refresh();
    }

    @Test public void onLoadingStateChanged_noData_showLoading() throws Exception {
        presenterOnViewAttached();

        loadingStateChangedRelay.call(LoadingState.LOADING);

        verify(view).showLoading();
    }

    @Test public void onLoadingStateChanged_noData_idleAfterLoading_hideLoading() throws Exception {
        presenterOnViewAttached();

        loadingStateChangedRelay.call(LoadingState.LOADING);
        loadingStateChangedRelay.call(LoadingState.IDLE);

        verify(view).hideLoading();
    }

    @Test public void onLoadingStateChanged_withData_idleAfterLoading_hideLoading() throws
            Exception {
        presenterOnViewAttached();

        dataChangedRelay.call(Collections.singletonList(mock(Gif.class)));
        loadingStateChangedRelay.call(LoadingState.LOADING);
        loadingStateChangedRelay.call(LoadingState.IDLE);

        verify(view).hideIncrementalLoading();
    }

    @Test public void onLoadingStateChanged_idleWithNoData_showEmptyState() throws Exception {
        presenterOnViewAttached();

        loadingStateChangedRelay.call(LoadingState.IDLE);

        verify(view).showEmpty();
    }

    @Test public void onLoadingStateChanged_idleWithData_showData() throws Exception {
        presenterOnViewAttached();

        final List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        dataChangedRelay.call(gifs);
        loadingStateChangedRelay.call(LoadingState.IDLE);

        verify(view).setTrendingGifs(gifs);
    }

    @Test public void onLoadingStateChanged_errorWithNoData_showError() throws Exception {
        presenterOnViewAttached();

        loadingStateChangedRelay.call(LoadingState.ERROR);

        verify(view).showError();
    }

    @Test public void onLoadingStateChanged_idleWithData_showIncrementalError() throws Exception {
        presenterOnViewAttached();

        final List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        dataChangedRelay.call(gifs);
        loadingStateChangedRelay.call(LoadingState.ERROR);

        verify(view).showIncrementalError();
    }

    @Test public void onGifClicked_goToGif() throws Exception {
        presenterOnViewAttached();

        gifClickedRelay.call(mock(Gif.class));
    }
}