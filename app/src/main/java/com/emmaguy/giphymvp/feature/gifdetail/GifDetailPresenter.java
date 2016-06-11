package com.emmaguy.giphymvp.feature.gifdetail;

import android.support.annotation.NonNull;

import com.emmaguy.giphymvp.common.base.BasePresenter;
import com.emmaguy.giphymvp.common.base.PresenterView;
import com.emmaguy.giphymvp.feature.trending.api.Gif;

class GifDetailPresenter extends BasePresenter<GifDetailPresenter.View> {
    private final Gif gif;

    GifDetailPresenter(@NonNull final Gif gif) {
        this.gif = gif;
    }

    @Override public void onViewAttached(@NonNull final View view) {
        super.onViewAttached(view);

        view.showGif(gif);
    }

    interface View extends PresenterView {
        void showGif(@NonNull final Gif gif);
    }
}
