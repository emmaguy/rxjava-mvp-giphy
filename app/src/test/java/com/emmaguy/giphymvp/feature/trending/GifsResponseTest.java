package com.emmaguy.giphymvp.feature.trending;

import com.emmaguy.giphymvp.feature.trending.api.TrendingGifsResponse;
import com.ryanharter.auto.value.moshi.AutoValueMoshiAdapterFactory;
import com.squareup.moshi.Moshi;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

public class GifsResponseTest {
    private TrendingGifsResponse trendingGifsResponse;

    @Before public void setUp() throws Exception {
        final Moshi moshi = new Moshi.Builder().add(new AutoValueMoshiAdapterFactory()).build();
        final String json = loadResource("trending-gifs-response.json");

        trendingGifsResponse = moshi.adapter(TrendingGifsResponse.class).lenient().fromJson(json);
    }

    @Test public void response_has5Gifs() throws Exception {
        assertThat(trendingGifsResponse.gifs().size()).isEqualTo(5);
    }

    @Test public void downsizedImage_usesDownsizedStillUrl() throws Exception {
        assertThat(trendingGifsResponse.gifs().get(0).downsizedImage()).isEqualTo(
                "http://media3.giphy.com/media/JIS3HjZexQJsk/200_s.gif");
    }

    @Test public void downsizedGif_usesDownsizedStillUrl() throws Exception {
        assertThat(trendingGifsResponse.gifs().get(0).downsizedGif()).isEqualTo(
                "http://media3.giphy.com/media/JIS3HjZexQJsk/200_d.gif");
    }

    private String loadResource(final String path) throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream(path);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}