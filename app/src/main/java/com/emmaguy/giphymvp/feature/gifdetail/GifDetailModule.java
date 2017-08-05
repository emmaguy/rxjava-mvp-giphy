package com.emmaguy.giphymvp.feature.gifdetail;

import com.emmaguy.giphymvp.feature.trending.api.Gif;

class GifDetailModule {
    static GifDetailPresenter gifPresenter(Gif gif) {
        return new GifDetailPresenter(gif);
    }
}
