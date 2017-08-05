package com.emmaguy.giphymvp.feature.trending.api;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.Map;

@AutoValue
public abstract class Gif implements Parcelable {
    public static JsonAdapter<Gif> typeAdapter(final Moshi moshi) {
        return new AutoValue_Gif.MoshiJsonAdapter(moshi);
    }

    abstract Map<String, Map<String, String>> images();

    public String downsizedGif() {
        return images().get("fixed_height_downsampled").get("url");
    }

    public String downsizedImage() {
        return images().get("fixed_height_still").get("url");
    }
}