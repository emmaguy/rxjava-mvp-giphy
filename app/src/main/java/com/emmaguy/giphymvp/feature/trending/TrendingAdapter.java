package com.emmaguy.giphymvp.feature.trending;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.emmaguy.giphymvp.R;
import com.emmaguy.giphymvp.feature.trending.api.Gif;
import com.jakewharton.rxrelay.PublishRelay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingGifViewHolder> {
    private final List<Gif> gifs = new ArrayList<>();
    private final PublishRelay<Gif> gifClickedRelay;

    TrendingAdapter(@NonNull final PublishRelay<Gif> gifClickedRelay) {
        this.gifClickedRelay = gifClickedRelay;
    }

    @Override
    public TrendingGifViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TrendingGifViewHolder(inflater.inflate(R.layout.item_trending_gif,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull final TrendingGifViewHolder holder, int position) {
        final Gif gif = gifs.get(position);

        Glide.with(holder.imageView.getContext())
                .load(gif.downsizedImage())
                .centerCrop()
                .into(holder.imageView);
    }

    @Override public int getItemCount() {
        return gifs.size();
    }

    void setGifs(@NonNull final List<Gif> gifs) {
        this.gifs.clear();
        this.gifs.addAll(gifs);
        notifyDataSetChanged();
    }

    class TrendingGifViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_trending_gif_imageview) ImageView imageView;

        TrendingGifViewHolder(@NonNull final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_trending_gif_imageview) void onGifClicked() {
            gifClickedRelay.call(gifs.get(getAdapterPosition()));
        }
    }
}
