package com.emmaguy.giphymvp.feature.trending;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emmaguy.giphymvp.R;
import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.jakewharton.rxrelay2.PublishRelay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingGifViewHolder> {
    private final RequestOptions requestOptions = new RequestOptions().centerCrop();
    private final List<Gif> gifList = new ArrayList<>();

    private final PublishRelay<Gif> gifClickedRelay;

    TrendingAdapter(PublishRelay<Gif> gifClickedRelay) {
        this.gifClickedRelay = gifClickedRelay;
    }

    @Override
    public TrendingGifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TrendingGifViewHolder(inflater.inflate(R.layout.item_trending_gif, parent, false));
    }

    @Override
    public void onBindViewHolder(TrendingGifViewHolder holder, int position) {
        Gif gif = gifList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(gif.downsizedImage())
                .apply(requestOptions)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return gifList.size();
    }

    void setGifs(List<Gif> gifs) {
        this.gifList.clear();
        this.gifList.addAll(gifs);
        notifyDataSetChanged();
    }

    class TrendingGifViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_trending_gif_imageview) ImageView imageView;

        TrendingGifViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_trending_gif_imageview)
        void onGifClicked() {
            gifClickedRelay.accept(gifList.get(getAdapterPosition()));
        }
    }
}
