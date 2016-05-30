package com.emmaguy.giphymvp.feature.trending;

import android.support.annotation.NonNull;

import com.emmaguy.giphymvp.BuildConfig;
import com.emmaguy.giphymvp.R;
import com.emmaguy.giphymvp.feature.trending.api.Gif;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.CoreShadowsAdapter;
import org.robolectric.util.ActivityController;

import java.util.Arrays;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public class TrendingActivityTest {
    private ActivityController<TrendingActivity> controller;
    private TrendingActivity activity;

    @Before public void setUp() throws Exception {
        activity = new TrendingActivity(){
            @NonNull @Override protected TrendingComponent createComponent() {
                return () -> mock(TrendingPresenter.class);
            }
        };
        controller = ActivityController.of(new CoreShadowsAdapter(), activity);
        controller.create().start().resume();
    }

    @After public void tearDown() throws Exception {
        controller.pause().stop().destroy();
    }

    @Test public void activityStart_swipeRefreshLayout_visible() throws Exception {
        assertThat(activity.swipeRefreshLayout).isVisible();
    }

    @Test public void activityStart_recyclerView_visible() throws Exception {
        assertThat(activity.recyclerView).isVisible();
    }

    @Test public void activityStart_infoTextView_gone() throws Exception {
        assertThat(activity.infoTextView).isGone();
    }

    @Test public void activityStart_loadingProgressBar_gone() throws Exception {
        assertThat(activity.loadingProgressBar).isGone();
    }

    @Test public void hideLoading_loadingProgressBar_gone() throws Exception {
        activity.hideLoading();

        assertThat(activity.loadingProgressBar).isGone();
    }

    @Test public void showError_infoTextView_visible() throws Exception {
        activity.showError();

        assertThat(activity.infoTextView).hasText(R.string.trending_refresh_gifs_failed);
        assertThat(activity.infoTextView).isVisible();
    }

    @Test public void showEmpty_infoTextView_visible() throws Exception {
        activity.showEmpty();

        assertThat(activity.infoTextView).hasText(R.string.trending_empty_state);
        assertThat(activity.infoTextView).isVisible();
    }

    @Test public void setTrendingGifs_recyclerHasCorrectNumberOfChildren() throws Exception {
        final Gif gif = mock(Gif.class);
        when(gif.downsizedImage()).thenReturn("some_url");

        activity.setTrendingGifs(Arrays.asList(gif, gif, gif));

        // Measure and layout the recycler view
        activity.recyclerView.measure(0, 0);
        activity.recyclerView.layout(0, 0, 100, 1000);

        assertThat(activity.recyclerView).hasChildCount(3);
    }
}