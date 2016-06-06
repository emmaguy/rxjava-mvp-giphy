# Giphy RxJava MVP

A showcase of RxJava and Model View Presenter, plus a number of other popular libraries for android development, including AutoValue, Retrofit, Moshi, and ButterKnife. Unit tests covering any business logic and Robolectric tests verifying the ui. 

The app is a simple master/detail implementation: we retrieve a list of gifs from the Giphy api and present them on the `TrendingActivity` in a `RecyclerView`. When a gif is clicked on, we load it in by itself in the `GifDetailActivity`.
 
<img src="images/app1.png" width="30%" />

## Benefits

This setup has a number of advantages over a non-MVP app architecture
 - it separates our concerns
    - the `Presenter` is view agnostic and does not care how an action was triggered, making a clear division which is easy to change
    - the view which implements the `View` interface is very simple - the methods are usually one liners, doing something on the android `Activity` e.g. just setting a view's state to `View.GONE` - which also makes them easy to test
 - it allows us to place all our business logic within the `Presenter` object and abstracts the `View` for easy mocking, so we can unit test all the things, e.g: 
    - when we're doing a network request, does the loading indicator show when it starts, and hide when it ends?
    - are we ignoring clicks on the 'refresh' button when a network call doing a refresh is already in progress?
    - what happens when a network call fails?
    - ... etc 
 - support for orientation changes (e.g. device rotation) with very little effort
 - the power of rxjava 
    - `Observable`s exposing future actions via the `View` interface, allowing our `Presenter`s to be entirely stateless
    - easy to do long running operations off the main thread
    - in app code but also in the unit tests, e.g the excellent `TestScheduler`  

## Architecture

### Packaging

The app is packaged by component/feature, under the `com.emmaguy.giphymvp.feature` package, to keep everything as private as possible. This means that any classes which contain logic for the `trending` feature is contained within `feature.trending` package and cannot be mistakenly used or extended elsewhere.

Each component consists of a `Presenter` class, a `View` interface which the corresponding `Activity` implements and a `Module`/`Component` for dependencies. The components currently map 1-1 to Activities, but could easily use custom views instead.

<img src="images/packaging.png" width="30%" />

### View interface

The `View` interface enables the `Presenter` to be pure Java and not have to know about anything android:
```java
    interface View extends PresenterView {
            @NonNull Observable<Void> onRefreshAction();

            void setTrendingGifs(@NonNull final List<Gif> gifs);
            
            void showLoading();
            void hideLoading();
            
            void goToGif(@NonNull final Gif gif);
            ...
        }
```

The interface exposes:
 - actions that the user can perform e.g. clicking a button, swiping, etc. (these are the methods that return `Observable<Object>`) 
    - we subscribe to each of these in the `Presenter`'s one lifecycle method, `onViewAttached`
    - each subscription is added to a `CompositeSubscription` via the method `unsubscribeOnViewDetach`, which will unsubscribe from all subscriptions when the view is detached
    - we limit what the `Presenter` is exposed to by using a return type of `Observable<Void>`, often it's enough just to know the action has happened
 - actions which immediately update the view with a simple operation e.g. show or hide a progress bar (method name will usually starts with `show`/`hide`), or methods which `set` data/state 
 - actions that start another Activity (prefixed with `goTo` e.g. `goToGif`)

## Separating loading state from data availability 

When we retrieve data from the network, we have to manage a number of states - loading, idle, error cases. Coupled to this, we also have a number of combinations of data - no data, initial data and later, incremental data. 

The `TrendingNetworkManager` separately exposes to the presenter `Observable`s for both the loading state and the data itself. We can thus distinguish between the following states:

 - No data and loading => show a spinner over the entire `Activity`
 - No data and idle    => show full page empty state, explaining how to get data
 - No data and error   => show full page error message
 - Data and loading    => show incremental spinner (e.g. swipe to refresh or perhaps a progress bar animating under the action bar)
 - Data and idle       => display the data
 - Data and error      => display the data that we do have, and perhaps a `Toast` or `Snackbar` detailing what went wrong with the incremental load
 
Whilst this could all be managed by the `TrendingPresenter`, it is much more easily modeled separately. Using one rx chain we could perform both the showing of progress and the data at the same time, but updating the view then becomes a side effect and it very easily gets messy, particularly when edge cases are discovered.

With them decoupled, the view expresses an action to refresh data, and the manager of that is responsible for emitting the progress state change. When the network request is complete, it can emit that it succeeded or failed alongside whatever data it managed to retrieve. 

Note: the network manager could easily be extended to decide whether or not to even make the network request and retrieve from a cache instead.

## Dependency Injection

This project does not use Dagger, instead it does the simple DI required manually.

We instead create simple classes suffixed with `Module` that contain static factory methods that construct the required dependencies, and create interfaces suffixed `Component` which list the injectable items for each `feature`.
 
Example Module:
```java
class TrendingModule {
    private static TrendingPresenter presenter;

    static TrendingPresenter trendingGifsPresenter() {
        if (presenter == null) {
            presenter = new TrendingPresenter(trendingGifManager(), AndroidSchedulers.mainThread());
        }
        return presenter;
    }

    private static TrendingNetworkManager trendingGifManager() {
        return new TrendingNetworkManager(giphyApi(), Schedulers.io());
    }

    private static GiphyApi giphyApi() {
        final Moshi moshi = new Moshi.Builder().add(new AutoValueMoshiAdapterFactory()).build();

        final Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.giphy.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        return retrofit.create(GiphyApi.class);
    }
}

```

Example Component:
```java
interface TrendingComponent extends BaseComponent {
    @NonNull TrendingPresenter getPresenter();
}
```
 
Then, when the dependencies are needed, we can create the required components using the factory methods. We abstract this into the `BaseActivity`, which also performs the ButterKnife binding/unbinding and `Presenter` lifecycle methods attaching/detaching the view.

```java
@CallSuper @Override protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    inject(createComponent());
    setContentView(getLayoutId());
    unbinder = ButterKnife.bind(this);
    onViewCreated(savedInstanceState);
    getPresenter().onViewAttached(getPresenterView());
}
```

We separate the `createComponent` and `inject` steps as an easy way to support orientation change - the first time we call both `create` and `inject`, any subsequent times we need to inject we can just call `inject` and we can reuse the classes - which means we have an in memory cache of data from our network requests (held in the `Presenter`) for free.
 

# License

    Copyright 2016 Emma Guy

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
