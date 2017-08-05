package com.emmaguy.giphymvp.feature.gifdetail;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.emmaguy.giphymvp.R;
import com.emmaguy.giphymvp.common.base.BaseActivity;
import com.emmaguy.giphymvp.common.base.BasePresenter;
import com.emmaguy.giphymvp.feature.trending.api.Gif;

import butterknife.BindView;

import static com.emmaguy.giphymvp.feature.gifdetail.GifDetailModule.gifPresenter;

public class GifDetailActivity extends BaseActivity<GifDetailPresenter.View, GifDetailComponent> implements GifDetailPresenter.View {
    private static final String EXTRA_TRENDING_GIF = "trending_gif";

    @BindView(R.id.gif_imageview) ImageView gifImageView;

    private GifDetailPresenter presenter;

    public static void start(Context context, Gif gif) {
        final Intent intent = new Intent(context, GifDetailActivity.class);
        intent.putExtra(EXTRA_TRENDING_GIF, gif);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gif;
    }

    @Override
    protected GifDetailComponent createComponent() {
        return () -> gifPresenter(getIntent().getParcelableExtra(EXTRA_TRENDING_GIF));
    }

    @Override
    protected void inject(GifDetailComponent component) {
        presenter = component.getPresenter();
    }

    @NonNull
    @Override
    protected BasePresenter<GifDetailPresenter.View> getPresenter() {
        return presenter;
    }

    @NonNull
    @Override
    protected GifDetailPresenter.View getPresenterView() {
        return this;
    }

    @Override
    public void showGif(Gif gif) {
        Glide.with(this).load(gif.downsizedGif()).into(gifImageView);
    }
}
