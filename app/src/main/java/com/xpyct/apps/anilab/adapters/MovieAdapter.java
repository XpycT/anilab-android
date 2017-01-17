package com.xpyct.apps.anilab.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.activities.MainActivity;
import com.xpyct.apps.anilab.models.Movie;
import com.xpyct.apps.anilab.views.OnItemClickListener;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MoviesViewHolder> {

    private Context mContext;

    private ArrayList<Movie> mMovies;

    private SharedPreferences pref;

    private int mDefaultTextColor;
    private int mDefaultBackgroundColor;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MovieAdapter() {
        mMovies = new ArrayList<>();
    }

    public MovieAdapter(ArrayList<Movie> movies) {
        this.mMovies = movies;
    }


    public void updateData(ArrayList<Movie> movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    public void appendData(ArrayList<Movie> movies) {
        this.mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_movies_item, viewGroup, false);

        //set the mContext
        this.mContext = viewGroup.getContext();

        pref = PreferenceManager.getDefaultSharedPreferences(viewGroup.getContext());

        //get the colors
        mDefaultTextColor = mContext.getResources().getColor(R.color.text_without_palette);
        mDefaultBackgroundColor = mContext.getResources().getColor(R.color.image_without_palette_opacity);

        return new MoviesViewHolder(rowView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder moviesViewHolder, final int position) {

        final Movie currentMovie = mMovies.get(position);
        moviesViewHolder.movieTitle.setText(currentMovie.getTitle());
        //imagesViewHolder.fileImage.setDrawingCacheEnabled(true);
        moviesViewHolder.moviePoster.setImageBitmap(null);

        //reset colors so we prevent crazy flashes :D
        moviesViewHolder.movieTitle.setTextColor(mDefaultTextColor);
        moviesViewHolder.movieTextContainer.setBackgroundColor(mDefaultBackgroundColor);

        String posterImage = mMovies.get(position).getInfo().getImages().getOriginal();

        if(pref.getBoolean("common_favorites",false)){
            //badge
            long i = mMovies.get(position).getMovieService();
            if(i > 0){
                int resImg = -1;
                if (i == MainActivity.AnimeServices.ANIDUB.id) {
                    resImg = R.drawable.profile_anidub;
                }else if (i == MainActivity.AnimeServices.ANIMELEND.id) {
                    resImg = R.drawable.profile_animelend;
                }else if (i == MainActivity.AnimeServices.ANISTAR.id) {
                    resImg = R.drawable.profile_anistar;
                }else if (i == MainActivity.AnimeServices.ANIMESPIRIT.id) {
                    resImg = R.drawable.profile_animespirit;
                }
                moviesViewHolder.serviceBadge.setImageResource(resImg);
                moviesViewHolder.serviceBadge.setVisibility(View.VISIBLE);
            }else{
                moviesViewHolder.serviceBadge.setVisibility(View.GONE);
            }
        }else{
            moviesViewHolder.serviceBadge.setVisibility(View.GONE);
        }

        // poster
        Glide.clear(moviesViewHolder.moviePoster);

        if (!posterImage.trim().isEmpty()) {

            Glide.with(mContext)
                    .load(posterImage)
                    .crossFade()
                    .thumbnail(0.3f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            moviesViewHolder.movieCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onItemClickListener.onClick(v, position);
                                }
                            });
                            return false;
                        }
                    }).into(moviesViewHolder.moviePoster);
        }
    }

    public ArrayList<Movie> getMovies() {
        return mMovies;
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}

class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected final FrameLayout movieTextContainer;
    protected final ImageView moviePoster;
    protected final TextView movieTitle;
    protected final CardView movieCard;
    protected final ImageView serviceBadge;
    private final OnItemClickListener onItemClickListener;

    public MoviesViewHolder(View itemView, OnItemClickListener onItemClickListener) {

        super(itemView);
        this.onItemClickListener = onItemClickListener;

        movieTextContainer = (FrameLayout) itemView.findViewById(R.id.item_movie_text_container);
        moviePoster = (ImageView) itemView.findViewById(R.id.item_movie_poster);
        movieTitle = (TextView) itemView.findViewById(R.id.item_movie_title);
        movieCard = (CardView) itemView.findViewById(R.id.item_movie_card);
        serviceBadge = (ImageView) itemView.findViewById(R.id.badge);

    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onClick(v, getPosition());
    }
}