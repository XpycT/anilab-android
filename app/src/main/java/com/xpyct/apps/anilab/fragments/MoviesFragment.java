package com.xpyct.apps.anilab.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.activities.DetailActivity;
import com.xpyct.apps.anilab.activities.MainActivity;
import com.xpyct.apps.anilab.adapters.MovieAdapter;
import com.xpyct.apps.anilab.managers.Utils;
import com.xpyct.apps.anilab.models.Images;
import com.xpyct.apps.anilab.models.Info;
import com.xpyct.apps.anilab.models.Movie;
import com.xpyct.apps.anilab.models.MovieFull;
import com.xpyct.apps.anilab.models.MovieList;
import com.xpyct.apps.anilab.models.orm.Favorites;
import com.xpyct.apps.anilab.network.AnilabApi;
import com.xpyct.apps.anilab.views.EndlessRecyclerOnScrollListener;
import com.xpyct.apps.anilab.views.OnItemClickListener;
import com.xpyct.apps.anilab.views.RecyclerInsetsDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tr.xip.errorview.ErrorView;
import tr.xip.errorview.ErrorView.RetryListener;

public class MoviesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String API_SERVICE = "api_service";
    public static final String SELECTED_MOVIE = "selected_movie";
    public static final String PREFIX_TAGS = "/tags/";
    public static final String PREFIX_XFSEARCH = "/xfsearch/";
    public static final String PREFIX_ANISTAR = "/filter/year/";
    private static final String PREF_ANIMATION = "animation";

    // saved instance
    private static final String STATE_RECYCLER_LAYOUT = "com.xpyct.apps.anilab.fragments.MoviesFragment.recycler.layout";
    private static final String STATE_MOVIES = "com.xpyct.apps.anilab.fragments.MoviesFragment.movies";
    private static final String STATE_PAGE = "com.xpyct.apps.anilab.fragments.MoviesFragment.page";
    private static final String STATE_CATEGORY = "com.xpyct.apps.anilab.fragments.MoviesFragment.category";
    private static final String STATE_REQUEST_TYPE = "com.xpyct.apps.anilab.fragments.MoviesFragment.request_type";

    private static int COLUMNS = 2;

    private enum RequestType {
        CATEGORY(1),
        SEARCH(2),
        FAVORITES(4);

        public final int id;

        private RequestType(int id) {
            this.id = id;
        }
    }

    private int currentRequestType = RequestType.CATEGORY.id;

    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovies;
    private ArrayList<Movie> mCurrentMovies;
    private EndlessRecyclerOnScrollListener endlessScrollListener;

    private Movie mFullMovie;
    private AnilabApi mApi = AnilabApi.getInstance();

    private SharedPreferences pref;
    private long apiService;
    private long serviceToFetch;

    private MaterialDialog waitDialog;

    private String categoryName;
    private int mPage;

    @Bind(R.id.fragment_movies_recycler)
    RecyclerView mMovieRecycler;
    @Bind(R.id.fragment_movies_empty_view)
    TextView mEmptyView;
    @Bind(R.id.fragment_movies_progress)
    ProgressBar mMovieProgress;
    @Bind(R.id.fragment_movies_error_view)
    ErrorView mMovieErrorView;
    @Bind(R.id.fragment_movies_refresh)
    SwipeRefreshLayout mMovieRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        pref = PreferenceManager.getDefaultSharedPreferences(MoviesFragment.this.getActivity());
        serviceToFetch = -1;

        if (MoviesFragment.this.getActivity() instanceof MainActivity) {
            ((MainActivity) MoviesFragment.this.getActivity()).setOnFilterChangedListener(new MainActivity.OnFilterChangedListener() {
                @Override
                public void onFilterChanged(long filter, String category) {
                    //Log.v("CAT",filter+"|"+category);
                    categoryName = category;
                    apiService = pref.getLong(API_SERVICE, 2);
                    if (mMovies != null) {
                        if (filter == MainActivity.Category.LATEST.id) {
                            currentRequestType = RequestType.CATEGORY.id;
                            mPage = 1;
                            showNewCategory(Utils.getApiServiceName(apiService), mPage, null);

                        } else if (filter == MainActivity.Category.DUBBERS.id) {
                            new MaterialDialog.Builder(MoviesFragment.this.getActivity())
                                    .title(R.string.select_dubber)
                                    .items(R.array.dubbers)
                                    .negativeText(R.string.cancel)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            String prefix = PREFIX_TAGS;
                                            if (apiService == MainActivity.AnimeServices.ANIDUB.id) {
                                                prefix = PREFIX_XFSEARCH;
                                            }

                                            mPage = 1;
                                            if (apiService == MainActivity.AnimeServices.ANIMELEND.id) {
                                                currentRequestType = RequestType.SEARCH.id;
                                                categoryName = text.toString();//Uri.encode(text.toString());
                                                //String readyQuery = URLEncoder.encode(tags, "windows-1251").replaceAll("\\+", "%20");
                                                showNewSearchResult(Utils.getApiServiceName(apiService), mPage, categoryName);
                                            } else {
                                                currentRequestType = RequestType.CATEGORY.id;
                                                categoryName = prefix + Uri.encode(text.toString()) + "/";
                                                showNewCategory(Utils.getApiServiceName(apiService), mPage, categoryName);
                                            }
                                        }
                                    })
                                    .show();
                        } else if (filter == MainActivity.Category.GENRES.id) {
                            String prefix = PREFIX_TAGS;
                            int genres_list = R.array.genres_animelend;

                            if (apiService == MainActivity.AnimeServices.ANIDUB.id) {
                                prefix = PREFIX_XFSEARCH;
                                genres_list = R.array.genres_anidub;
                            }
                            if (apiService == MainActivity.AnimeServices.ANIMESPIRIT.id) {
                                prefix = PREFIX_TAGS;
                                genres_list = R.array.genres_animespirit;
                            }

                            final String finalPrefix = prefix;
                            final boolean isAnimelend = (apiService == MainActivity.AnimeServices.ANIMELEND.id);
                            final boolean isSnimespirit = (apiService == MainActivity.AnimeServices.ANIMESPIRIT.id);
                            new MaterialDialog.Builder(MoviesFragment.this.getActivity())
                                    .title(R.string.select_genre)
                                    .items(genres_list)
                                    .negativeText(R.string.cancel)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            currentRequestType = RequestType.CATEGORY.id;
                                            if(isAnimelend){
                                                String[] genres_list = getResources().getStringArray(R.array.genres_animelend_link);
                                                categoryName = "/" + Uri.encode(genres_list[which].toString()) + "/";
                                            }else if(isSnimespirit){
                                                String[] genres_list = getResources().getStringArray(R.array.genres_animespirit_link);
                                                categoryName = finalPrefix + Uri.encode(genres_list[which].toString()) + "/";
                                            }else{
                                                categoryName = finalPrefix + Uri.encode(text.toString()) + "/";
                                            }

                                            mPage = 1;
                                            showNewCategory(Utils.getApiServiceName(apiService), mPage, categoryName);
                                        }
                                    })
                                    .show();
                        } else if (filter == MainActivity.Category.YEARS.id) {
                            new MaterialDialog.Builder(MoviesFragment.this.getActivity())
                                    .title(R.string.select_year)
                                    .negativeText(R.string.cancel)
                                    .items(R.array.years)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            currentRequestType = RequestType.CATEGORY.id;
                                            String prefix = PREFIX_TAGS;
                                            if (apiService == MainActivity.AnimeServices.ANIDUB.id) {
                                                prefix = PREFIX_XFSEARCH;
                                            }else if (apiService == MainActivity.AnimeServices.ANISTAR.id) {
                                                prefix = PREFIX_ANISTAR;
                                            }
                                            categoryName = prefix + text + "/";
                                            mPage = 1;
                                            showNewCategory(Utils.getApiServiceName(apiService), mPage, categoryName);
                                        }
                                    })
                                    .show();
                        }
                        if (filter == MainActivity.Category.SETTINGS.id ||
                                filter == MainActivity.Category.GENRES.id ||
                                filter == MainActivity.Category.YEARS.id ||
                                filter == MainActivity.Category.DUBBERS.id) {
                            ; // disable settings change
                        }else if (filter == MainActivity.Category.FAVORITES.id) {
                            currentRequestType = RequestType.FAVORITES.id;
                            mPage = 1;
                            showFavorites();
                        } else {
                            currentRequestType = RequestType.CATEGORY.id;
                            mPage = 1;
                            showNewCategory(Utils.getApiServiceName(apiService), mPage, categoryName);
                        }
                    }
                }
            });
            ((MainActivity) MoviesFragment.this.getActivity()).setOnServiceChangedListener(new MainActivity.OnServiceChangedListener() {
                @Override
                public void onServiceChanged(long filter) {
                    currentRequestType = RequestType.CATEGORY.id;
                    apiService = pref.getLong(API_SERVICE, 2);
                    mPage = 1;
                    showNewCategory(Utils.getApiServiceName(apiService), mPage, null);
                }
            });
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGE, mPage);
        outState.putString(STATE_CATEGORY, categoryName);
        outState.putInt(STATE_REQUEST_TYPE, currentRequestType);
        outState.putParcelableArrayList(STATE_MOVIES, mMovieAdapter.getMovies());
        outState.putParcelable(STATE_RECYCLER_LAYOUT, mMovieRecycler.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            String savedCategoryState = savedInstanceState.getString(STATE_CATEGORY);
            int savedRequestState = savedInstanceState.getInt(STATE_REQUEST_TYPE);
            int savedPageState = savedInstanceState.getInt(STATE_PAGE);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(STATE_RECYCLER_LAYOUT);
            ArrayList<Movie> movieParcelable = savedInstanceState.getParcelableArrayList(STATE_MOVIES);

            categoryName = savedCategoryState;
            mPage = savedPageState;
            apiService = pref.getLong(API_SERVICE, 2);
            currentRequestType = savedRequestState;
            mMovieRecycler.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            mMovieAdapter.updateData(movieParcelable);
            mMovies = movieParcelable;
            showMovieList();
        }else{
            mPage = 1;
            apiService = pref.getLong(API_SERVICE, 2);
            currentRequestType = RequestType.CATEGORY.id;
            showNewCategory(Utils.getApiServiceName(apiService), 1, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, rootView);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), COLUMNS);

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
                        float cardViewWidth = getActivity().getResources().getDimension(R.dimen.cardview_layout_width);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        newSpanCount++; // hack
                        gridLayoutManager.setSpanCount(newSpanCount);
                        gridLayoutManager.requestLayout();
                    }
                });


        mMovieRecycler.setLayoutManager(gridLayoutManager);
        mMovieRecycler.addItemDecoration(new RecyclerInsetsDecoration(getActivity()));
        mMovieRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        endlessScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager,mPage) {
            @Override
            public void onLoadMore(int current_page) {
                mPage = current_page;
                //Log.v("PAGE", mPage+"");
                mMovieRefreshLayout.setRefreshing(true);
                if (currentRequestType == RequestType.FAVORITES.id) {
                    mMovieRefreshLayout.setRefreshing(false); // don't load from api
                }else if (currentRequestType == RequestType.CATEGORY.id) {
                    showCategory(Utils.getApiServiceName(apiService), mPage, (categoryName != null) ? categoryName : null);
                } else if (currentRequestType == RequestType.SEARCH.id && categoryName != null) {
                    showSearchResult(Utils.getApiServiceName(apiService), mPage, categoryName);
                }
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

        waitDialog = new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .autoDismiss(false)
                .title(R.string.progress_dialog_description)
                .content(R.string.please_wait)
                .progress(true, 0).build();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movies, menu);
    }

    @Override
    public void onRefresh() {
        apiService = pref.getLong(API_SERVICE, 2);
        final String apiServiceName = Utils.getApiServiceName(apiService);
        if (currentRequestType == RequestType.CATEGORY.id) {
            showNewCategory(apiServiceName, 1, categoryName);
        } else if (currentRequestType == RequestType.SEARCH.id) {
            showNewSearchResult(apiServiceName, 1, categoryName);
        }else if(currentRequestType == RequestType.FAVORITES.id){
            showFavorites();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentRequestType == RequestType.FAVORITES.id){
            showFavorites();
        }
        endlessScrollListener.reset(mPage, 0, true);
    }

    /**
     * Fill adapter with custom movie
     */
    private void showFavorites() {
        hideMovieList();

        List<Favorites> favorites;

        boolean common_favorites = pref.getBoolean("common_favorites", false);
        if(common_favorites){
            favorites = Favorites.listAll(Favorites.class);
        }else{
            favorites = Favorites.find(Favorites.class, "service = ?", Long.toString(pref.getLong(API_SERVICE, 2)));
        }

        ArrayList<Movie> favoriteMovies = new ArrayList<>();

        for (Favorites item:favorites){
            Images images = new Images();
            images.setOriginal(item.getPoster());
            images.setThumbnail(item.getPoster());

            Info info = new Info();
            info.setImages(images);

            Movie movie = new Movie();
            movie.setInfo(info);
            movie.setMovieId(item.getMovieId());
            movie.setTitle(item.getTitle());
            movie.setCommonFavorite(true);
            movie.setMovieService(item.getService());

            favoriteMovies.add(movie);
        }
        resetAdapter(favoriteMovies);
        showMovieList();
    }

    /**
     * Get category from API, reset adapter
     *
     * @param service String service name (ex. 'animeland')
     * @param page    int page index
     * @param path    String category path
     */
    private void showNewCategory(String service, int page, String path) {
        hideMovieList();

        AppObservable.bindSupportFragment(this, mApi.fetchCategory(service, page, path))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerNewCategory);
    }

    /**
     * Get category from API
     *
     * @param service Service name (ex. 'animeland')
     * @param page    int Page index
     * @param path    String category path
     */
    private void showCategory(String service, int page, String path) {
        AppObservable.bindSupportFragment(this, mApi.fetchCategory(service, page, path))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * Get search query from API, reset adapter
     *
     * @param service String service name (ex. 'animeland')
     * @param page    int page index
     * @param query   String search query
     */
    private void showNewSearchResult(String service, int page, String query) {
        hideMovieList();

        AppObservable.bindSupportFragment(this, mApi.searchQuery(service, page, query))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerNewSearch);
    }

    /**
     * Get search query from API
     *
     * @param service String service name (ex. 'animeland')
     * @param page    int page index
     * @param query   String search query
     */
    private void showSearchResult(String service, int page, String query) {
        AppObservable.bindSupportFragment(this, mApi.searchQuery(service, page, query))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerSearch);
    }

    /**
     * Prepare to show description
     *
     * @param movie_id int movie_id
     */
    private void prepareToShowDescription(Movie selected_movie,int movie_id) {
        waitDialog.show();
        serviceToFetch = apiService;
        if(selected_movie.getMovieService() > 0){
            serviceToFetch = selected_movie.getMovieService();
        }

        AppObservable.bindSupportFragment(this, mApi.fetchFullPage(Utils.getApiServiceName(serviceToFetch), movie_id))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerDescription);
    }


    /**
     * Update Movie Adapter, reset scroll
     *
     * @param movies ArrayList movies
     */
    private void updateAdapter(ArrayList<Movie> movies) {
        mCurrentMovies = movies;
        mMovieAdapter.appendData(mCurrentMovies);
        if (mMovieAdapter.getItemCount() == 0) mMovieRecycler.scrollToPosition(0);
        checkForEmptyMovieData();
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
        endlessScrollListener.reset(mPage,0, true);
        checkForEmptyMovieData();
    }

    /**
     * Check for empty data, and show error label
     */
    private void checkForEmptyMovieData() {
        if (mMovieAdapter.getItemCount() == 0) {
            mMovieRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mMovieRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Api observer
     */
    private Observer<MovieList> observer = new Observer<MovieList>() {
        @Override
        public void onNext(final MovieList movies) {
            mMovies = movies.getMovies();
            updateAdapter(mMovies);
            /*if (MoviesFragment.this.getActivity() instanceof MainActivity) {
                ((MainActivity) MoviesFragment.this.getActivity()).setCategoryCount(movies);
            }*/
        }

        @Override
        public void onCompleted() {
            // Dismiss loading dialog
            showMovieList();
        }

        @Override
        public void onError(final Throwable error) {
            checkRetrofitOnError(error);
            mMovieErrorView.setOnRetryListener(new RetryListener() {
                @Override
                public void onRetry() {
                    showNewCategory(Utils.getApiServiceName(apiService), mPage, (categoryName != null) ? categoryName : null);
                }
            });
        }
    };

    private Observer<MovieList> observerNewCategory = new Observer<MovieList>() {
        @Override
        public void onNext(final MovieList movies) {
            mMovies = movies.getMovies();
            resetAdapter(mMovies);
        }

        @Override
        public void onCompleted() {
            // Dismiss loading dialog
            showMovieList();
        }

        @Override
        public void onError(final Throwable error) {
            checkRetrofitOnError(error);

            mMovieErrorView.setOnRetryListener(new RetryListener() {
                @Override
                public void onRetry() {
                    mPage = 1;
                    showNewCategory(Utils.getApiServiceName(apiService), mPage, (categoryName != null) ? categoryName : null);
                }
            });
        }
    };
    private Observer<MovieList> observerNewSearch = new Observer<MovieList>() {
        @Override
        public void onNext(final MovieList movies) {
            mMovies = movies.getMovies();
            resetAdapter(mMovies);
        }

        @Override
        public void onCompleted() {
            // Dismiss loading dialog
            showMovieList();
        }

        @Override
        public void onError(final Throwable error) {
            checkRetrofitOnError(error);

            mMovieErrorView.setOnRetryListener(new RetryListener() {
                @Override
                public void onRetry() {
                    apiService = pref.getLong(API_SERVICE, 2);
                    mPage = 1;
                    showNewSearchResult(Utils.getApiServiceName(apiService), mPage, categoryName);
                }
            });
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
            // Dismiss loading dialog
            showMovieList();
        }

        @Override
        public void onError(final Throwable error) {
            checkRetrofitOnError(error);

            mMovieErrorView.setOnRetryListener(new RetryListener() {
                @Override
                public void onRetry() {
                    apiService = pref.getLong(API_SERVICE, 2);
                    mPage = 1;
                    showNewSearchResult(Utils.getApiServiceName(apiService), mPage, categoryName);
                }
            });
        }
    };

    private Observer<MovieFull> observerDescription = new Observer<MovieFull>() {
        @Override
        public void onNext(final MovieFull movie) {
            mFullMovie = movie.getMovie();
        }

        @Override
        public void onCompleted() {
            long prefService = pref.getLong(API_SERVICE, 2);

            if(serviceToFetch > 0){
                prefService = serviceToFetch;
            }
            Log.v("ANILAB",""+ prefService);
            Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
            detailIntent.putExtra(SELECTED_MOVIE, (Parcelable) mFullMovie);
            detailIntent.putExtra(API_SERVICE, prefService);
            waitDialog.dismiss();

            // animate if version >= 21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && pref.getBoolean(PREF_ANIMATION, false)) {
                getActivity().startActivity(detailIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }else{
                getActivity().startActivity(detailIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }

        @Override
        public void onError(final Throwable error) {
            waitDialog.dismiss();
            Log.v("ANILAB", "[MoviesFragment:observerDescription]:"+error.getMessage());
            Snackbar.make(getView(), getActivity().getString(R.string.error_timeout_cant_get_movie), Snackbar.LENGTH_SHORT).show();
        }
    };

    /**
     * Retrofit error checker
     *
     * @param error
     */
    private void checkRetrofitOnError(Throwable error) {
        if (error instanceof RetrofitError) {
            RetrofitError e = (RetrofitError) error;
            if (e.getKind() == RetrofitError.Kind.NETWORK) {
                mMovieErrorView.setTitle(R.string.error_network);
                mMovieErrorView.setSubtitle(R.string.error_network_subtitle);
            } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                mMovieErrorView.setTitle(R.string.error_server);
                mMovieErrorView.setSubtitle(R.string.error_server_subtitle);
            } else {
                mMovieErrorView.setTitle(R.string.error_uncommon);
                mMovieErrorView.setSubtitle(R.string.error_uncommon_subtitle);
            }
        }
        mMovieRefreshLayout.setRefreshing(false);
        mMovieProgress.setVisibility(View.GONE);
        mMovieRecycler.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        mMovieErrorView.setVisibility(View.VISIBLE);
    }

    /**
     * Dismiss loading dialog. Show movie list and hide progress with error
     */
    private void showMovieList() {
        mMovieProgress.setVisibility(View.GONE);
        mMovieRecycler.setVisibility(View.VISIBLE);
        mMovieErrorView.setVisibility(View.GONE);
        mMovieRefreshLayout.setRefreshing(false);
    }

    /**
     * Show loading dialog. Hide movie list and show progress
     */
    private void hideMovieList() {
        mMovieProgress.setVisibility(View.VISIBLE);
        mMovieRecycler.setVisibility(View.GONE);
        mMovieErrorView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        //mMovieRefreshLayout.setRefreshing(true);
    }

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {
            Movie selectedMovie = mMovieAdapter.getMovies().get(position);

            prepareToShowDescription(selectedMovie,Integer.parseInt(selectedMovie.getMovieId()));
        }
    };
}
