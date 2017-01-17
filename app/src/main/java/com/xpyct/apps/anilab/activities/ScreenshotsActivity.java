package com.xpyct.apps.anilab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.models.Movie;
import com.xpyct.apps.anilab.views.ScreenshotsViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

public class ScreenshotsActivity extends AbstractActivity {

    private static final String ISLOCKED_ARG = "isLocked";
    public static final String INDEX = "index";
    public static final String SELECTED_MOVIE = "selected_movie";

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    Movie movie = null;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra(SELECTED_MOVIE);
        index = intent.getIntExtra(INDEX, 0);

        setContentView(mViewPager);
        mViewPager.setAdapter(new SamplePagerAdapter(movie));
        mViewPager.setCurrentItem(index);
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((ScreenshotsViewPager) mViewPager).setLocked(isLocked);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_screenshots, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof ScreenshotsViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((ScreenshotsViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    static class SamplePagerAdapter extends PagerAdapter {

        private Movie mMovie;

        public SamplePagerAdapter(Movie movie) {
            this.mMovie = movie;
        }

        @Override
        public int getCount() {
            return mMovie.getInfo().getScreenshots().toArray().length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Glide.with(container.getContext()).load(mMovie.getInfo().getScreenshots().get(position).getOriginal()).asBitmap().into(photoView);
            //photoView.setImageResource(sDrawables[position]);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
