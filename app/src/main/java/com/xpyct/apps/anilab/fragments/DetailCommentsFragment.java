package com.xpyct.apps.anilab.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.adapters.CommentAdapter;
import com.xpyct.apps.anilab.managers.Utils;
import com.xpyct.apps.anilab.models.Comment;
import com.xpyct.apps.anilab.models.Comments;
import com.xpyct.apps.anilab.models.Movie;
import com.xpyct.apps.anilab.network.AnilabApi;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailCommentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String SELECTED_MOVIE = "selected_movie";
    public static final String API_SERVICE = "api_service";

    private SharedPreferences pref;
    private Movie mMovie;
    private long apiService;

    private Comments mComments;
    private CommentAdapter mCommentAdapter;

    private AnilabApi mApi = AnilabApi.getInstance();

    @Bind(R.id.fragment_comments_recycler)
    RecyclerView mCommentRecycler;
    @Bind(R.id.fragment_comments_progress)
    ProgressBar mCommentProgress;
    @Bind(R.id.fragment_comments_empty_view)
    TextView mEmptyView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie movie.
     * @return A new instance of fragment DetailCommentsFragment.
     */
    public static DetailCommentsFragment newInstance(Movie movie, long api) {
        DetailCommentsFragment fragment = new DetailCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(SELECTED_MOVIE, movie);
        args.putLong(API_SERVICE, api);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        pref = PreferenceManager.getDefaultSharedPreferences(DetailCommentsFragment.this.getActivity());
        if (getArguments() != null) {
            mMovie = (Movie) getArguments().getSerializable(SELECTED_MOVIE);
            apiService = getArguments().getLong(API_SERVICE, 2);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_detail_comments, container, false);
        ButterKnife.bind(this, rootView);

        mCommentAdapter = new CommentAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mCommentRecycler.setLayoutManager(layoutManager);
        mCommentRecycler.setHasFixedSize(true);
        mCommentRecycler.setAdapter(mCommentAdapter);

        getComments();

        return rootView;
    }

    private void getComments() {
        AppObservable.bindSupportFragment(this, mApi.fetchComments(Utils.getApiServiceName(apiService), Integer.parseInt(mMovie.getMovieId())))
                .cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observer<Comments> observer = new Observer<Comments>() {
        @Override
        public void onNext(final Comments comments) {
            mComments = comments;
            updateAdapter(mComments.getList());
        }

        @Override
        public void onCompleted() {
            // Dismiss loading dialog
            showCommentsList();
        }

        @Override
        public void onError(final Throwable error) {
            checkRetrofitOnError(error);
        }
    };

    private void showCommentsList() {
        mCommentProgress.setVisibility(View.GONE);
        mCommentRecycler.setVisibility(View.VISIBLE);
    }

    private void hideCommentsList() {
        mCommentProgress.setVisibility(View.VISIBLE);
        mCommentRecycler.setVisibility(View.GONE);
    }

    /**
     * Reset Movie Adapter, reset scroll
     *
     * @param comments ArrayList movies
     */
    private void updateAdapter(ArrayList<Comment> comments) {
        mCommentAdapter.updateData(comments);

        if (mCommentAdapter.getItemCount() == 0) mCommentRecycler.scrollToPosition(0);
        checkForEmptyMovieData();
    }

    /**
     * Check for empty data, and show error label
     */
    private void checkForEmptyMovieData() {
        if (mCommentAdapter.getItemCount() == 0) {
            mCommentRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mCommentRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Retrofit error checker
     *
     * @param error
     */
    private void checkRetrofitOnError(Throwable error) {
        if (error instanceof RetrofitError) {
        }
        mCommentProgress.setVisibility(View.GONE);
        mCommentRecycler.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onRefresh() {
        hideCommentsList();
        getComments();
    }
}
