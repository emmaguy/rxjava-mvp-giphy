package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.GiphyApi;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ryanharter.auto.value.moshi.AutoValueMoshiAdapterFactory;
import com.squareup.moshi.Moshi;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

class TrendingModule {
    private static TrendingPresenter presenter;

    static TrendingPresenter trendingGifsPresenter() {
        if (presenter == null) {
            presenter = new TrendingPresenter(trendingGifManager(), AndroidSchedulers.mainThread(),
                    Schedulers.io());
        }
        return presenter;
    }

    private static TrendingManager trendingGifManager() {
        return new TrendingManager(giphyApi());
    }

    private static GiphyApi giphyApi() {
        final Moshi moshi = new Moshi.Builder().add(new AutoValueMoshiAdapterFactory()).build();

        final Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.giphy.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        return retrofit.create(GiphyApi.class);
    }
}
