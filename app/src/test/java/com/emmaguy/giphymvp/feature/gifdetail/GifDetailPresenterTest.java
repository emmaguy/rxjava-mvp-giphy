package com.emmaguy.giphymvp.feature.gifdetail;

import com.emmaguy.giphymvp.common.base.BasePresenterTest;
import com.emmaguy.giphymvp.feature.trending.api.Gif;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GifDetailPresenterTest
        extends BasePresenterTest<GifDetailPresenter, GifDetailPresenter.View> {
    private Gif gif;

    @Override protected GifDetailPresenter createPresenter() {
        gif = mock(Gif.class);
        return new GifDetailPresenter(gif);
    }

    @Override protected GifDetailPresenter.View createView() {
        return mock(GifDetailPresenter.View.class);
    }

    @Test public void onViewAttached_showGif() throws Exception {
        presenterOnViewAttached();

        verify(view).showGif(gif);
    }
}