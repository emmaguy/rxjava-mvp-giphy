package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;
import com.emmaguy.giphymvp.feature.trending.api.TrendingGifsResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TrendingManagerTest {
    @Mock private GiphyApi giphyApi;
    @Mock private TrendingStorage trendingStorage;

    private TrendingManager networkManager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(trendingStorage.gifs()).thenReturn(Observable.just(new ArrayList<>()));

        networkManager = new TrendingManager(giphyApi, trendingStorage);
    }

    @Test
    public void getTrendingGifs_succeeds() throws Exception {
        List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        TrendingGifsResponse response = TrendingGifsResponse.create(gifs);
        when(giphyApi.latestTrendingGifs()).thenReturn(Single.just(response));

        networkManager.getTrendingGifs()
                .test()
                .assertNoErrors()
                .assertComplete()
                .assertValue(TrendingModel.success(gifs));
    }

    @Test
    public void getTrendingGifs_succeeds_saveGifs() throws Exception {
        List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        TrendingGifsResponse response = TrendingGifsResponse.create(gifs);
        when(giphyApi.latestTrendingGifs()).thenReturn(Single.just(response));

        networkManager.getTrendingGifs()
                .test();

        verify(trendingStorage).saveGifs(gifs);
    }

    @Test
    public void getTrendingGifs_fails() throws Exception {
        when(giphyApi.latestTrendingGifs()).thenReturn(Single.error(new IOException()));

        networkManager.getTrendingGifs()
                .test()
                .assertNoErrors()
                .assertValue(TrendingModel.failure());
    }

    @Test
    public void getTrendingGifs_neverSaveGifs() throws Exception {
        when(giphyApi.latestTrendingGifs()).thenReturn(Single.error(new IOException()));

        networkManager.getTrendingGifs()
                .test();

        verify(trendingStorage, never()).saveGifs(any());
    }

    @Test
    public void getTrendingGifs_cacheIsEmpty_usesNetwork() throws Exception {
        when(trendingStorage.gifs()).thenReturn(Observable.just(Collections.emptyList()));

        networkManager.getTrendingGifs()
                .test();

        verify(giphyApi).latestTrendingGifs();
    }

    @Test
    public void getTrendingGifs_cacheHasGifs_usesCache() throws Exception {
        List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        when(trendingStorage.gifs()).thenReturn(Observable.just(gifs));

        networkManager.getTrendingGifs()
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(TrendingModel.success(gifs));
    }

    @Test
    public void getTrendingGifs_cacheHasGifs_doesNotCallNetwork() throws Exception {
        List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        when(trendingStorage.gifs()).thenReturn(Observable.just(gifs));

        networkManager.getTrendingGifs()
                .test();

        verify(giphyApi, never()).latestTrendingGifs();
    }
}