package com.emmaguy.giphymvp.feature.gifdetail;

import android.support.annotation.NonNull;

import com.emmaguy.giphymvp.feature.trending.api.Gif;

class GifDetailModule {
    static GifDetailPresenter gifPresenter(@NonNull final Gif gif) {
        return new GifDetailPresenter(gif);
    }
}
