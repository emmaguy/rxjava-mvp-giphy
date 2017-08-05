package com.emmaguy.giphymvp.common.base;

import android.support.annotation.CallSuper;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BasePresenter<V extends PresenterView> {
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();
    private V view;

    @CallSuper
    public void onViewAttached(V view) {
        if (this.view != null) {
            throw new IllegalStateException("View " + this.view + " is already attached. Cannot attach " + view);
        }
        this.view = view;
    }

    @CallSuper
    public void onViewDetached() {
        if (view == null) {
            throw new IllegalStateException("View is already detached");
        }
        view = null;
        compositeSubscription.clear();
    }

    @CallSuper
    protected final void addToAutoUnsubscribe(Subscription subscription) {
        compositeSubscription.add(subscription);
    }
}
