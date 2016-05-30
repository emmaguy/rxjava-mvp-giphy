package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;
import com.emmaguy.giphymvp.feature.trending.api.TrendingGifsResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TrendingNetworkManagerTest {
    private final TestSubscriber<LoadingState> loadingStateTestSubscriber = new TestSubscriber<>();
    private final TestSubscriber<List<Gif>> trendingGifsTestSubscriber = new TestSubscriber<>();

    private final List<Gif> gifs = Collections.singletonList(mock(Gif.class));
    private final TestScheduler testScheduler = new TestScheduler();

    @Mock private GiphyApi giphyApi;

    private TrendingNetworkManager networkManager;

    @Before public void setUp() throws Exception {
        initMocks(this);

        final TrendingGifsResponse trendingGifsResponse = TrendingGifsResponse.create(gifs);
        final Result<TrendingGifsResponse> response = Result.response(Response.success(
                trendingGifsResponse));
        when(giphyApi.latestTrending()).thenReturn(Observable.just(response));

        networkManager = new TrendingNetworkManager(giphyApi, testScheduler);
        networkManager.setup();
    }

    @Test public void refresh_showsLoadingWhilstRetrievingTrendingGifs() throws Exception {
        networkManager.onLoadingStateChanged().subscribe(loadingStateTestSubscriber);
        networkManager.onDataChanged().subscribe(trendingGifsTestSubscriber);

        networkManager.refresh();

        loadingStateTestSubscriber.assertValues(LoadingState.LOADING);
        trendingGifsTestSubscriber.assertNoValues();

        testScheduler.triggerActions();

        trendingGifsTestSubscriber.assertValue(gifs);
        loadingStateTestSubscriber.assertValues(LoadingState.LOADING, LoadingState.IDLE);
    }

    @Test public void refresh_networkError_showsError() throws Exception {
        final Result<TrendingGifsResponse> response = Result.error(new Exception("Fail"));
        when(giphyApi.latestTrending()).thenReturn(Observable.just(response));

        networkManager.onLoadingStateChanged().subscribe(loadingStateTestSubscriber);
        networkManager.onDataChanged().subscribe(trendingGifsTestSubscriber);

        networkManager.refresh();
        testScheduler.triggerActions();

        loadingStateTestSubscriber.assertValues(LoadingState.LOADING, LoadingState.ERROR);
    }

    @Test public void refresh_emptyResponse_showsError() throws Exception {
        final Result<TrendingGifsResponse> response = Result.response(Response.error(404,
                ResponseBody.create(MediaType.parse(""), "")));
        when(giphyApi.latestTrending()).thenReturn(Observable.just(response));

        networkManager.onLoadingStateChanged().subscribe(loadingStateTestSubscriber);
        networkManager.onDataChanged().subscribe(trendingGifsTestSubscriber);

        networkManager.refresh();
        testScheduler.triggerActions();

        loadingStateTestSubscriber.assertValues(LoadingState.LOADING, LoadingState.ERROR);
    }

    @Test public void refresh_successfulRequestAfterError_emitsAsNormal() throws Exception {
        final Result<TrendingGifsResponse> response = Result.response(Response.error(404,
                ResponseBody.create(MediaType.parse(""), "")));
        when(giphyApi.latestTrending()).thenReturn(Observable.just(response));

        networkManager.onLoadingStateChanged().subscribe(loadingStateTestSubscriber);
        networkManager.onDataChanged().subscribe(trendingGifsTestSubscriber);

        networkManager.refresh();
        testScheduler.triggerActions();

        loadingStateTestSubscriber.assertValues(LoadingState.LOADING, LoadingState.ERROR);

        final TrendingGifsResponse trendingGifsResponse = TrendingGifsResponse.create(gifs);
        when(giphyApi.latestTrending()).thenReturn(Observable.just(Result.response(Response.success(
                trendingGifsResponse))));

        networkManager.refresh();
        testScheduler.triggerActions();

        loadingStateTestSubscriber.assertValues(LoadingState.LOADING,
                LoadingState.ERROR,
                LoadingState.LOADING,
                LoadingState.IDLE);
    }

    @Test public void teardown_refresh_emitsNoLoadingState() throws Exception {
        networkManager.onLoadingStateChanged().subscribe(loadingStateTestSubscriber);
        networkManager.teardown();

        networkManager.refresh();

        loadingStateTestSubscriber.assertNoValues();
    }

    @Test public void teardown_refresh_emitsNoTrendingGifs() throws Exception {
        networkManager.onDataChanged().subscribe(trendingGifsTestSubscriber);
        networkManager.teardown();

        networkManager.refresh();

        trendingGifsTestSubscriber.assertNoValues();
    }
}