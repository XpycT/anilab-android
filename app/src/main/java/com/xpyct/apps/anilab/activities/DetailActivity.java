package com.xpyct.apps.anilab.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.fragments.DetailCommentsFragment;
import com.xpyct.apps.anilab.fragments.DetailDescriptionFragment;
import com.xpyct.apps.anilab.fragments.DetailFileFragment;
import com.xpyct.apps.anilab.models.Movie;
import com.xpyct.apps.anilab.models.orm.Favorites;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AbstractActivity {

    public static final String POSITION = "position";
    public static final String SELECTED_MOVIE = "selected_movie";
    public static final String PREF_ANIMATION = "animation";
    public static final String API_SERVICE = "api_service";

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIM_DURATION_FAB = 400;
    private static final int ANIM_DURATION_LiKE = 300;

    private ShareActionProvider mShareActionProvider;

    @Bind(R.id.toolbar)
    Toolbar toolbar = null;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.fabBtn)
    FloatingActionButton mFabLikeButton;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.backdrop)
    KenBurnsView mBackdrop;
    @Bind(R.id.main_content)
    CoordinatorLayout mContentView;

    private Movie mSelectedMovie;
    private long mApiService;

    private Drawable mDrawableHeart_o;
    private Drawable mDrawableHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // Recover items from the intent
        final int position = getIntent().getIntExtra(POSITION, 0);
        mApiService = getIntent().getLongExtra(API_SERVICE, 2);
        mSelectedMovie = (Movie) getIntent().getSerializableExtra(SELECTED_MOVIE);
        mDrawableHeart_o = new IconicsDrawable(this, FontAwesome.Icon.faw_heart_o).color(Color.WHITE).sizeDp(24);
        mDrawableHeart = new IconicsDrawable(this, FontAwesome.Icon.faw_heart).color(Color.WHITE).sizeDp(24);
        initInstances();

        startContentAnimation();
    }

    /**
     * Init components
     */
    private void initInstances() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

        mFabLikeButton.setScaleX(0);
        mFabLikeButton.setScaleY(0);

        // mFabLikeButton.setImageDrawable(mDrawableHeart_o);
        changeLikeIcon();

        mFabLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHeartButton(true);
            }
        });

        //placeholder
        mBackdrop.setImageResource(R.drawable.header);
        //image
        String url = mSelectedMovie.getInfo().getImages().getOriginal();
        Glide.with(this).load(url).asBitmap().into(mBackdrop);
        Glide.with(this)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBackdrop);
        String title = mSelectedMovie.getTitle();
        setTitle(title);
        collapsing_toolbar.setTitle(title);
        mTabs.setupWithViewPager(mViewPager);
    }

    private void updateHeartButton(boolean animated) {
        if (animated) {
            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(mFabLikeButton, "rotation", 0f, 360f);
            rotationAnim.setDuration(300);
            rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(mFabLikeButton, "scaleX", 0.2f, 1f);
            bounceAnimX.setDuration(300);
            bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(mFabLikeButton, "scaleY", 0.2f, 1f);
            bounceAnimY.setDuration(300);
            bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
            bounceAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    appendToFavorites();
                }
            });

            animatorSet.play(rotationAnim);
            animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                }
            });

            animatorSet.start();

        } else {
            appendToFavorites();
        }
    }

    private void changeLikeIcon() {
        long count = Favorites.count(Favorites.class, "movie_id = ? and service = ?",
                new String[]{mSelectedMovie.getMovieId(), Long.toString(mApiService)});
        if (count > 0) {
            mFabLikeButton.setImageDrawable(mDrawableHeart);
        } else {
            mFabLikeButton.setImageDrawable(mDrawableHeart_o);
        }
    }

    private void appendToFavorites() {
        long count = Favorites.count(Favorites.class, "movie_id = ? and service = ?",
                new String[]{mSelectedMovie.getMovieId(), Long.toString(mApiService)});
        if (count > 0) {
            Favorites.deleteAll(Favorites.class, "movie_id = ? and service = ?", mSelectedMovie.getMovieId(), Long.toString(mApiService));
        } else {
            Favorites favorite = new Favorites(mSelectedMovie.getMovieId(), mApiService,
                    mSelectedMovie.getTitle(), mSelectedMovie.getInfo().getImages().getOriginal());
            favorite.save();
        }
        changeLikeIcon();
    }

    /**
     * Starting FAB animaion
     */
    private void startContentAnimation() {

        mFabLikeButton.animate()
                .scaleX(1.f)
                .scaleY(1.f)
                .setInterpolator(OVERSHOOT_INTERPOLATOR)
                .setStartDelay(400)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    /**
     * Setup mViewPager
     *
     * @param viewPager ViewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(DetailDescriptionFragment.newInstance(mSelectedMovie), getString(R.string.tab_detail));
        adapter.addFragment(DetailFileFragment.newInstance(mSelectedMovie, mApiService), getString(R.string.tab_episodes));
        if(mApiService != MainActivity.AnimeServices.ANISTAR.id){
            adapter.addFragment(DetailCommentsFragment.newInstance(mSelectedMovie,mApiService), getString(R.string.tab_comments));
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mFabLikeButton.show();
                        break;

                    default:
                        mFabLikeButton.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        shareItem.setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_share_alt).paddingDp(3).colorRes(R.color.icons).sizeDp(24));

        shareItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(getDefaultIntent());
                return true;
            }
        });

        return true;
    }

    private Intent getDefaultIntent(){
        StringBuilder textBuilder = new StringBuilder().append("Советую посмотреть: ")
                .append(mSelectedMovie.getTitle())
                .append(" - ");
        if (mApiService == MainActivity.AnimeServices.ANIDUB.id) {
            textBuilder.append("http://online.anidub.com/?newsid="+mSelectedMovie.getMovieId());
        } else if (mApiService == MainActivity.AnimeServices.ANISTAR.id) {
            textBuilder.append("http://anistar.ru/?newsid="+mSelectedMovie.getMovieId());
        } else if (mApiService == MainActivity.AnimeServices.ANIMELEND.id) {
            textBuilder.append("http://animelend.info/?newsid="+mSelectedMovie.getMovieId());
        } else if (mApiService == MainActivity.AnimeServices.ANIMESPIRIT.id) {
            textBuilder.append("http://www.animespirit.ru/?newsid="+mSelectedMovie.getMovieId());
        }
        //Log.d("SHARE",textBuilder.toString());
        Intent shareIntent = new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, textBuilder.toString())
                .setType("text/plain");
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("SHARE id", id+"");
        if (id == R.id.action_settings) {
            // animate if version >= 21
            if (Build.VERSION.SDK_INT >= 21 && pref.getBoolean(PREF_ANIMATION, true)) {
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            return true;
        }else if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Fragment adapter
     */
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
