package com.emmaguy.giphymvp.common.base;

import android.support.annotation.CallSuper;

import org.junit.Before;

import static org.mockito.MockitoAnnotations.initMocks;

public abstract class BasePresenterTest<P extends BasePresenter<V>, V extends PresenterView> {
    protected P presenter;
    protected V view;

    @CallSuper @Before public void before() {
        initMocks(this);

        presenter = createPresenter();
        view = createView();
    }

    protected abstract P createPresenter();
    protected abstract V createView();

    protected void presenterOnViewAttached() {
        presenter.onViewAttached(view);
    }

    protected void presenterOnViewDetached() {
        presenter.onViewDetached();
    }
}
