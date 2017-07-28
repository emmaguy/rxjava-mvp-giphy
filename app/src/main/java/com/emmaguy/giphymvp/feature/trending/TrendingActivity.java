package com.emmaguy.giphymvp.feature.trending;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emmaguy.giphymvp.R;
import com.emmaguy.giphymvp.common.base.BaseActivity;
import com.emmaguy.giphymvp.common.base.BasePresenter;
import com.emmaguy.giphymvp.feature.gifdetail.GifDetailActivity;
import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.jakewharton.rxrelay.PublishRelay;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindView;
import rx.Observable;

public class TrendingActivity extends BaseActivity<TrendingPresenter.View, TrendingComponent>
        implements TrendingPresenter.View {
    private final PublishRelay<Void> refreshRelay = PublishRelay.create();
    private final PublishRelay<Gif> gifClickedRelay = PublishRelay.create();

    @BindView(R.id.trending_root_viewgroup) ViewGroup rootViewGroup;
    @BindView(R.id.trending_gifs_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.trending_swiperefreshlayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.trending_info_textview) TextView infoTextView;
    @BindView(R.id.trending_loading_progressbar) ProgressBar loadingProgressBar;
    @ColorInt @BindColor(R.color.colorAccent) int accent;
    @BindInt(R.integer.trending_gifs_columns) int numberOfColumns;

    private TrendingPresenter presenter;
    private TrendingAdapter trendingAdapter;

    @Override protected int getLayoutId() {
        return R.layout.activity_trending_gifs;
    }

    @Override @NonNull protected TrendingComponent createComponent() {
        return TrendingModule::trendingGifsPresenter;
    }

    @Override protected void inject(@NonNull final TrendingComponent component) {
        presenter = component.getPresenter();
    }

    @NonNull @Override protected BasePresenter<TrendingPresenter.View> getPresenter() {
        return presenter;
    }

    @NonNull @Override protected TrendingPresenter.View getPresenterView() {
        return this;
    }

    @Override protected void onViewCreated(@Nullable final Bundle savedInstanceState) {
        trendingAdapter = new TrendingAdapter(gifClickedRelay);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(trendingAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        swipeRefreshLayout.setColorSchemeColors(accent);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshRelay.call(null));
    }

    @NonNull @Override public Observable<Gif> onGifClicked() {
        return gifClickedRelay;
    }

    @NonNull @Override public Observable<Void> onRefreshAction() {
        return refreshRelay;
    }

    @Override public void showEmpty() {
        infoTextView.setText(R.string.trending_empty_state);
        infoTextView.setVisibility(View.VISIBLE);
    }

    @Override public void showError() {
        infoTextView.setText(R.string.trending_refresh_gifs_failed);
        infoTextView.setVisibility(View.VISIBLE);
    }

    @Override public void showIncrementalError() {
        Snackbar.make(rootViewGroup, R.string.trending_incremental_error, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override public void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override public void showIncrementalLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override public void hideIncrementalLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override public void setIncrementalLoadingEnabled(boolean enabled) {
        swipeRefreshLayout.setEnabled(enabled);
    }

    @Override public void goToGif(@NonNull final Gif gif) {
        GifDetailActivity.start(this, gif);
    }

    @Override public void setTrendingGifs(@NonNull final List<Gif> gifs) {
        trendingAdapter.setGifs(gifs);
    }
}
