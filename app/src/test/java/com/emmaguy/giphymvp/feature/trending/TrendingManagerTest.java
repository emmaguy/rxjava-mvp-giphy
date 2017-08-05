package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;
import com.emmaguy.giphymvp.feature.trending.api.TrendingGifsResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TrendingManagerTest {
    @Mock private GiphyApi giphyApi;

    private TrendingManager networkManager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        networkManager = new TrendingManager(giphyApi);
    }

    @Test
    public void getTrendingGifs_succeeds() throws Exception {
        List<Gif> gifs = Collections.singletonList(mock(Gif.class));
        TrendingGifsResponse response = TrendingGifsResponse.create(gifs);
        when(giphyApi.latestTrendingGifs()).thenReturn(Observable.just(response));

        networkManager.getTrendingGifs()
                .test()
                .assertCompleted()
                .assertNoErrors()
                .assertValue(TrendingGifsModel.success(gifs));
    }

    @Test
    public void getTrendingGifs_fails() throws Exception {
        when(giphyApi.latestTrendingGifs()).thenReturn(Observable.error(new IOException()));

        networkManager.getTrendingGifs()
                .test()
                .assertCompleted()
                .assertValue(TrendingGifsModel.failure());
    }
}