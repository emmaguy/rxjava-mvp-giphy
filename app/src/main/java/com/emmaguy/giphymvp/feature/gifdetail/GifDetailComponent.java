package com.emmaguy.giphymvp.feature.gifdetail;

import android.support.annotation.NonNull;

import com.emmaguy.giphymvp.common.base.BaseComponent;

interface GifDetailComponent extends BaseComponent {
    @NonNull
    GifDetailPresenter getPresenter();
}
