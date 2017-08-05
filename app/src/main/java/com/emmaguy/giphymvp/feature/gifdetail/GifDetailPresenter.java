package com.emmaguy.giphymvp.feature.gifdetail;

import com.emmaguy.giphymvp.common.base.BasePresenter;
import com.emmaguy.giphymvp.common.base.PresenterView;
import com.emmaguy.giphymvp.feature.trending.api.Gif;

class GifDetailPresenter extends BasePresenter<GifDetailPresenter.View> {
    private final Gif gif;

    GifDetailPresenter(Gif gif) {
        this.gif = gif;
    }

    @Override
    public void onViewAttached(View view) {
        super.onViewAttached(view);

        view.showGif(gif);
    }

    interface View extends PresenterView {
        void showGif(Gif gif);
    }
}
