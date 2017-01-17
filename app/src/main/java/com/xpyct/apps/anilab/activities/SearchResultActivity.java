package com.xpyct.apps.anilab.activities;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.adapters.MovieAdapter;
import com.xpyct.apps.anilab.managers.AppSuggestionProvider;
import com.xpyct.apps.anilab.managers.Utils;
import com.xpyct.apps.anilab.models.Movie;
import com.xpyct.apps.anilab.models.MovieFull;
import com.xpyct.apps.anilab.models.MovieList;
import com.xpyct.apps.anilab.network.AnilabApi;
import com.xpyct.apps.anilab.views.EndlessRecyclerOnScrollListener;
import com.xpyct.apps.anilab.views.OnItemClickListener;
import com.xpyct.apps.anilab.views.RecyclerInsetsDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * anilab-android
 * Created by XpycT on 30.07.2015.
 */
public class SearchResultActivity extends AbstractActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String API_SERVICE = "api_service";
    public static final String SELECTED_MOVIE = "selected_movie";
    private static final String PREF_ANIMATION = "animation";

    private static final String STATE_PAGE = "com.xpyct.apps.anilab.activities.SearchResultActivity.page";
    private static final String STATE_RECYCLER_LAYOUT = "com.xpyct.apps.anilab.activities.SearchResultActivity.recycler.layout";
    private static final String STATE_MOVIES = "com.xpyct.apps.anilab.activities.SearchResultActivity.movies";
    private static final String STATE_QUERY = "com.xpyct.apps.anilab.activities.SearchResultActivity.query";

    private AnilabApi mApi = AnilabApi.getInstance();

    @Bind(R.id.toolbar)
    Toolbar toolbar = null;
    @Bind(R.id.fragment_movies_recycler)
    RecyclerView mMovieRecycler;
    @Bind(R.id.fragment_movies_refresh)
    SwipeRefreshLayout mMovieRefreshLayout;

    private MaterialDialog waitDialog;

    private int mPage;
    private long apiService;
    private String query;
    private ArrayList<Movie> mMovies;
    private ArrayList<Movie> mCurrentMovies;
    private Movie mFullMovie;

    private MovieAdapter mMovieAdapter;
    private EndlessRecyclerOnScrollListener endlessScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        apiService = pref.getLong(API_SERVICE, 2);

        initInstances();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    AppSuggestionProvider.AUTHORITY, AppSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            setTitle(query);
            Answers.getInstance().logSearch(new SearchEvent()
                    .putQuery(query));

            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, params);
        }

        int COLUMNS = 2;
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, COLUMNS);

        mMovieRecycler.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mMovieRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            mMovieRecycler.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        int viewWidth = mMovieRecycler.getMeasuredWidth();
                        float cardViewWidth = getResources().getDimension(R.dimen.cardview_layout_width);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        newSpanCount++; // hack
                        gridLayoutManager.setSpanCount(newSpanCount);
                        gridLayoutManager.requestLayout();
                    }
                });


        mMovieRecycler.setLayoutManager(gridLayoutManager);
        mMovieRecycler.addItemDecoration(new RecyclerInsetsDecoration(this));
        mMovieRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        endlessScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager, mPage) {
            @Override
            public void onLoadMore(int current_page) {
                mPage = current_page;
                mMovieRefreshLayout.setRefreshing(true);
                showSearchResult(Utils.getApiServiceName(apiService), mPage, query);
            }
        };
        mMovieRecycler.setOnScrollListener(endlessScrollListener);

        mMovieAdapter = new MovieAdapter();
        mMovieAdapter.setOnItemClickListener(recyclerRowClickListener);

        mMovieRecycler.setAdapter(mMovieAdapter);

        mMovieRefreshLayout.setOnRefreshListener(this);
        mMovieRefreshLayout.setColorSchemeResources(R.color.blue,
                R.color.green,
                R.color.orange,
                R.color.red);

        waitDialog = new MaterialDialog.Builder(this)
                .cancelable(false)
                .autoDismiss(false)
                .title(R.string.progress_dialog_description)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        if (savedInstanceState == null) {
            doSearch();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGE, mPage);
        outState.putString(STATE_QUERY, query);
        outState.putParcelableArrayList(STATE_MOVIES, mMovieAdapter.getMovies());
        outState.putParcelable(STATE_RECYCLER_LAYOUT, mMovieRecycler.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String queryState = savedInstanceState.getString(STATE_QUERY);
        int savedPageState = savedInstanceState.getInt(STATE_PAGE);
        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_RECYCLER_LAYOUT);
        ArrayList<Movie> movieParcelable = savedInstanceState.getParcelableArrayList(STATE_MOVIES);

        query = queryState;
        mPage = savedPageState;
        apiService = pref.getLong(API_SERVICE, 2);
        mMovieRecycler.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        mMovieAdapter.updateData(movieParcelable);
        mMovies = movieParcelable;
    }

    private void doSearch() {
        showNewSearchResult(Utils.getApiServiceName(apiService), 1, query);
    }

    /**
     * Get search query from API
     *
     * @param service String service name (ex. 'animeland')
     * @param page    int page index
     * @param query   String search query
     */
    private void showSearchResult(String service, int page, String query) {
        AppObservable.bindActivity(this, mApi.searchQuery(service, page, query))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerSearch);
    }

    /**
     * Get search query from API, reset adapter
     *
     * @param service String service name (ex. 'animeland')
     * @param page    int page index
     * @param query   String search query
     */
    private void showNewSearchResult(String service, int page, String query) {
        AppObservable.bindActivity(this, mApi.searchQuery(service, page, query))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerNewSearch);
    }

    /**
     * Prepare to show description
     *
     * @param movie_id int movie_id
     */
    private void prepareToShowDescription(int movie_id) {
        waitDialog.show();
        AppObservable.bindActivity(this, mApi.fetchFullPage(Utils.getApiServiceName(apiService), movie_id))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerDescription);
    }

    private Observer<MovieList> observerNewSearch = new Observer<MovieList>() {
        @Override
        public void onNext(final MovieList movies) {
            mMovies = movies.getMovies();
            resetAdapter(mMovies);
        }

        @Override
        public void onCompleted() {
            mMovieRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(final Throwable error) {
        }
    };

    private Observer<MovieList> observerSearch = new Observer<MovieList>() {
        @Override
        public void onNext(final MovieList movies) {
            mMovies = movies.getMovies();
            updateAdapter(mMovies);

        }

        @Override
        public void onCompleted() {
            mMovieRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(final Throwable error) {

        }
    };

    private Observer<MovieFull> observerDescription = new Observer<MovieFull>() {
        @Override
        public void onNext(final MovieFull movie) {
            mFullMovie = movie.getMovie();
        }

        @Override
        public void onCompleted() {
            Intent detailIntent = new Intent(SearchResultActivity.this, DetailActivity.class);
            detailIntent.putExtra(SELECTED_MOVIE, (Parcelable) mFullMovie);
            detailIntent.putExtra(API_SERVICE, pref.getLong(API_SERVICE, 2));
            waitDialog.dismiss();
            // animate if version >= 21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && pref.getBoolean(PREF_ANIMATION, false)) {
                startActivity(detailIntent, ActivityOptions.makeSceneTransitionAnimation(SearchResultActivity.this).toBundle());
            } else {
                startActivity(detailIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }

        @Override
        public void onError(final Throwable error) {
            waitDialog.dismiss();
            Snackbar.make(mMovieRefreshLayout, getString(R.string.error_timeout_cant_get_movie), Snackbar.LENGTH_SHORT).show();
        }
    };

    /**
     * Update Movie Adapter, reset scroll
     *
     * @param movies ArrayList movies
     */
    private void updateAdapter(ArrayList<Movie> movies) {
        mCurrentMovies = movies;
        mMovieAdapter.appendData(mCurrentMovies);
        if (mMovieAdapter.getItemCount() == 0) mMovieRecycler.scrollToPosition(0);
    }

    /**
     * Reset Movie Adapter, reset scroll
     *
     * @param movies ArrayList movies
     */
    private void resetAdapter(ArrayList<Movie> movies) {
        mCurrentMovies = movies;
        mMovieAdapter.updateData(mCurrentMovies);

        mMovieRecycler.scrollToPosition(0);
        endlessScrollListener.reset(mPage, 0, true);
    }

    /**
     * Init component toolbar
     */
    private void initInstances() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        doSearch();
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {
            Movie selectedMovie = mMovieAdapter.getMovies().get(position);

            prepareToShowDescription(Integer.parseInt(selectedMovie.getMovieId()));
        }
    };
}
