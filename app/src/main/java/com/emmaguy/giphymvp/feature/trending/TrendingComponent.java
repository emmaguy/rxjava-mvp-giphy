package com.emmaguy.giphymvp.feature.trending;

import android.support.annotation.NonNull;

import com.emmaguy.giphymvp.common.base.BaseComponent;

interface TrendingComponent extends BaseComponent {
    @NonNull TrendingPresenter getPresenter();
}
