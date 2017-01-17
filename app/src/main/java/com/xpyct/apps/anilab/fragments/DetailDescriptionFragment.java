package com.xpyct.apps.anilab.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.activities.ScreenshotsActivity;
import com.xpyct.apps.anilab.models.InfoItem;
import com.xpyct.apps.anilab.models.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailDescriptionFragment extends Fragment {

    public static final String SELECTED_MOVIE = "selected_movie";
    public static final String INDEX = "index";
    private SharedPreferences pref;
    @Bind(R.id.info)
    TableLayout info;

    @Bind(R.id.layout_gallery)
    LinearLayout gallery_holder;
    @Bind(R.id.screen1)
    ImageView screen1;
    @Bind(R.id.screen2)
    ImageView screen2;

    @Bind(R.id.text_description)
    TextView description;

    Movie movie;

    public static DetailDescriptionFragment newInstance(Movie movie) {
        DetailDescriptionFragment fragment = new DetailDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(SELECTED_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        pref = PreferenceManager.getDefaultSharedPreferences(DetailDescriptionFragment.this.getActivity());
        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable(SELECTED_MOVIE);
        }

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_description, container, false);
        ButterKnife.bind(this, rootView);

        setupInfoView();
        if (movie.getDescription() != null && !movie.getDescription().isEmpty())
            description.setText(Html.fromHtml(movie.getDescription()));
        setupGallery();
        return rootView;
    }

    /**
     * Show or hide gallery view layout
     */
    private void setupGallery() {
        gallery_holder.setVisibility(View.GONE);
        if (movie.getInfo() != null) {
            if (movie.getInfo().getScreenshots().size() >= 2) {
                Glide.with(getActivity()).load(movie.getInfo().getScreenshots().get(0).getOriginal())
                        .thumbnail(0.3f).crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(screen1);
                Glide.with(getActivity()).load(movie.getInfo().getScreenshots().get(1).getOriginal())
                        .thumbnail(0.3f).crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(screen2);
                gallery_holder.setVisibility(View.VISIBLE);
            }
        }
    }

    /***
     * Prepare and show info anout movie
     */
    private void setupInfoView() {
        if (movie.getInfo() != null) {
            List<InfoItem> items = new ArrayList<>();

            if (movie.getInfo().getYear() != null && !movie.getInfo().getYear().isEmpty())
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_year), movie.getInfo().getYear()));
            if (movie.getInfo().getProduction() != null && !movie.getInfo().getProduction().isEmpty())
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_production), movie.getInfo().getProduction()));
            if (movie.getInfo().getGenres() != null && movie.getInfo().getGenres().size() > 0)
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_genres), TextUtils.join(", ", movie.getInfo().getGenres())));
            if (movie.getInfo().getType() != null && !movie.getInfo().getType().isEmpty())
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_type), movie.getInfo().getType()));
            if (movie.getInfo().getAired() != null && !movie.getInfo().getAired().isEmpty())
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_aired), movie.getInfo().getAired()));
            if (movie.getInfo().getSeries() != null && !movie.getInfo().getSeries().isEmpty())
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_series), movie.getInfo().getSeries()));
            if (movie.getInfo().getProducers() != null && movie.getInfo().getProducers().size() > 0)
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_producers), TextUtils.join(", ", movie.getInfo().getProducers())));
            if (movie.getInfo().getAuthors() != null && movie.getInfo().getAuthors().size() > 0)
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_authors), TextUtils.join(", ", movie.getInfo().getAuthors())));
            if (movie.getInfo().getPostscoring() != null && movie.getInfo().getPostscoring().size() > 0)
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_postscoring), TextUtils.join(", ", movie.getInfo().getPostscoring())));
            if (movie.getInfo().getStudio() != null && !movie.getInfo().getStudio().isEmpty())
                items.add(new InfoItem(getActivity().getString(R.string.movie_info_studio), movie.getInfo().getStudio()));

            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());
            float second_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

            for (InfoItem item : items) {
                TableRow row = new TableRow(getActivity());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView title = new TextView(getActivity());
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                title.setWidth((int) pixels);
                title.setText(item.getTitle());

                TextView value = new TextView(getActivity());
                value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                value.setText(item.getValue());
                value.setPadding(5, 0, 5, 0);
                value.setWidth((int) second_width);
                row.addView(title);
                row.addView(value);
                info.addView(row);
            }
        }
    }

    /**
     * Show screenshot activity
     */
    @Nullable
    @OnClick({R.id.more_button, R.id.screen1})
    void showScreenshots() {
        Intent screenIntent = new Intent(getActivity(), ScreenshotsActivity.class);
        screenIntent.putExtra(SELECTED_MOVIE, (Parcelable) movie);
        screenIntent.putExtra(INDEX, 0);
        startActivity(screenIntent);
    }

    @Nullable
    @OnClick({R.id.screen2})
    void showScreenshotsSecond() {
        Intent screenIntent = new Intent(getActivity(), ScreenshotsActivity.class);
        screenIntent.putExtra(SELECTED_MOVIE, (Parcelable) movie);
        screenIntent.putExtra(INDEX, 1);
        startActivity(screenIntent);
    }
}