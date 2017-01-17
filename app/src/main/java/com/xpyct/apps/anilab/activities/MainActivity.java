package com.xpyct.apps.anilab.activities;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.BaseDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.models.orm.Favorites;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AbstractActivity {

    public static final String PREF_API_SERVICE = "api_service";
    public static final String PREF_ANIMATION = "animation";
    public static final String COMPACT_HEADER = "compact_header";

    public enum Category {
        SETTINGS(999),
        LATEST(1000),
        POPULAR(1001),
        GENRES(1002),
        DUBBERS(1003),
        YEARS(1004),
        FAVORITES(1005),
        RUS_TV(1),
        RUS_OVA(2),
        RUS_ONA(3),
        RUS_MOVIES(4),
        RUS_ONGOING(8),
        RUS_DORAMA(16),
        RUS_SPECIALS(18),
        RUS_LIVE_ACTION(20),
        SUB_TV(32),
        SUB_OVA(64),
        SUB_ONA(65),
        SUB_MOVIES(128),
        SUB_ONGOING(256),
        SUB_DORAMA(512),
        SUB_SPECIALS(520),
        SUB_LIVE_ACTION(540),
        NEW(1024);


        public final int id;

        private Category(int id) {
            this.id = id;
        }
    }

    public enum AnimeServices {
        ANIMELEND(1),
        ANIDUB(2),
        ANISTAR(4),
        ANIMERU(8),
        ANIMESPIRIT(16);

        public final long id;

        private AnimeServices(long id) {
            this.id = id;
        }
    }

    private static final IDrawerItem[] ANIMELEND_MENU_ITEMS = new IDrawerItem[]{
            new PrimaryDrawerItem().withName(R.string.menu_latest).withIcon(FontAwesome.Icon.faw_home).withIdentifier(Category.LATEST.id),
            new PrimaryDrawerItem().withName(R.string.menu_genres).withIcon(FontAwesome.Icon.faw_tags).withIdentifier(Category.GENRES.id).withSelectable(false),
            new PrimaryDrawerItem().withName(R.string.menu_favorites).withIcon(FontAwesome.Icon.faw_heart).withIdentifier(Category.FAVORITES.id),
            new SectionDrawerItem().withName(R.string.menu_catalog),
            new SecondaryDrawerItem().withName(R.string.menu_new).withIcon(FontAwesome.Icon.faw_bars).withTag("/new/").withIdentifier(Category.NEW.id),
            new SecondaryDrawerItem().withName(R.string.menu_ongoing).withIcon(FontAwesome.Icon.faw_bars).withTag("/ongoing/").withIdentifier(Category.RUS_ONGOING.id),
            new SecondaryDrawerItem().withName(R.string.menu_tv_shows).withIcon(FontAwesome.Icon.faw_bars).withTag("/serialy/").withIdentifier(Category.RUS_TV.id),
            new SecondaryDrawerItem().withName(R.string.menu_movies).withIcon(FontAwesome.Icon.faw_bars).withTag("/polnometrazhnoe/").withIdentifier(Category.RUS_MOVIES.id),
            new SecondaryDrawerItem().withName(R.string.menu_dorama).withIcon(FontAwesome.Icon.faw_bars).withTag("/dorama/").withIdentifier(Category.RUS_DORAMA.id),
    };
    private static final IDrawerItem[] ANIDUB_MENU_ITEMS = new IDrawerItem[]{
            new PrimaryDrawerItem().withName(R.string.menu_latest).withIcon(FontAwesome.Icon.faw_home).withIdentifier(Category.LATEST.id),
            new PrimaryDrawerItem().withName(R.string.menu_genres).withIcon(FontAwesome.Icon.faw_tags).withIdentifier(Category.GENRES.id).withSelectable(false),
            new PrimaryDrawerItem().withName(R.string.menu_dubbers).withIcon(FontAwesome.Icon.faw_bullhorn).withIdentifier(Category.DUBBERS.id).withSelectable(false),
            new PrimaryDrawerItem().withName(R.string.menu_years).withIcon(FontAwesome.Icon.faw_calendar_o).withIdentifier(Category.YEARS.id).withSelectable(false),
            new PrimaryDrawerItem().withName(R.string.menu_favorites).withIcon(FontAwesome.Icon.faw_heart).withIdentifier(Category.FAVORITES.id),
            new SectionDrawerItem().withName(R.string.menu_catalog),
            new SecondaryDrawerItem().withName(R.string.menu_ongoing).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime_tv/anime_ongoing/").withIdentifier(Category.RUS_ONGOING.id),
            new SecondaryDrawerItem().withName(R.string.menu_tv_shows).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime_tv/").withIdentifier(Category.RUS_TV.id),
            new SecondaryDrawerItem().withName(R.string.menu_movies).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime_movie/").withIdentifier(Category.RUS_MOVIES.id),
            new SecondaryDrawerItem().withName(R.string.menu_ova).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime_ova/").withIdentifier(Category.RUS_OVA.id),
            new SecondaryDrawerItem().withName(R.string.menu_dorama).withIcon(FontAwesome.Icon.faw_bars).withTag("/dorama/").withIdentifier(Category.RUS_DORAMA.id),
    };
    private static final IDrawerItem[] ANISTAR_MENU_ITEMS = new IDrawerItem[]{
            new PrimaryDrawerItem().withName(R.string.menu_latest).withIcon(FontAwesome.Icon.faw_home).withIdentifier(Category.LATEST.id),
            new PrimaryDrawerItem().withName(R.string.menu_years).withIcon(FontAwesome.Icon.faw_calendar_o).withIdentifier(Category.YEARS.id).withSelectable(false),
            new PrimaryDrawerItem().withName(R.string.menu_favorites).withIcon(FontAwesome.Icon.faw_heart).withIdentifier(Category.FAVORITES.id),
            new SectionDrawerItem().withName(R.string.menu_catalog),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_thriller).withIcon(FontAwesome.Icon.faw_bars).withTag("/thriller/").withIdentifier(3000),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_vampires).withIcon(FontAwesome.Icon.faw_bars).withTag("/vampires/").withIdentifier(3002),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_detective).withIcon(FontAwesome.Icon.faw_bars).withTag("/detective/").withIdentifier(3004),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_drama).withIcon(FontAwesome.Icon.faw_bars).withTag("/drama/").withIdentifier(3008),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_history).withIcon(FontAwesome.Icon.faw_bars).withTag("/history/").withIdentifier(3010),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_cyberpunk).withIcon(FontAwesome.Icon.faw_bars).withTag("/cyberpunk/").withIdentifier(30010),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_comedy).withIcon(FontAwesome.Icon.faw_bars).withTag("/comedy/").withIdentifier(3012),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_maho_shoujo).withIcon(FontAwesome.Icon.faw_bars).withTag("/maho-shoujo/").withIdentifier(3014),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_fur).withIcon(FontAwesome.Icon.faw_bars).withTag("/fur/").withIdentifier(3016),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_parodies).withIcon(FontAwesome.Icon.faw_bars).withTag("/parodies/").withIdentifier(3018),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_senen).withIcon(FontAwesome.Icon.faw_bars).withTag("/senen/").withIdentifier(3020),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_sports).withIcon(FontAwesome.Icon.faw_bars).withTag("/sports/").withIdentifier(3022),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_misticism).withIcon(FontAwesome.Icon.faw_bars).withTag("/mysticism/").withIdentifier(3024),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_music).withIcon(FontAwesome.Icon.faw_bars).withTag("/music/").withIdentifier(3026),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_everyday_life).withIcon(FontAwesome.Icon.faw_bars).withTag("/everyday-life/").withIdentifier(3028),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_adventures).withIcon(FontAwesome.Icon.faw_bars).withTag("/adventures/").withIdentifier(3030),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_romance).withIcon(FontAwesome.Icon.faw_bars).withTag("/romance/").withIdentifier(3032),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_shoujo).withIcon(FontAwesome.Icon.faw_bars).withTag("/shoujo/").withIdentifier(3034),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_senen_ay).withIcon(FontAwesome.Icon.faw_bars).withTag("/senen-ay/").withIdentifier(3036),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_triller).withIcon(FontAwesome.Icon.faw_bars).withTag("/horror/").withIdentifier(3038),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_horror).withIcon(FontAwesome.Icon.faw_bars).withTag("/horor/").withIdentifier(3040),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_fantastic).withIcon(FontAwesome.Icon.faw_bars).withTag("/fantasy/").withIdentifier(3042),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_fantasy).withIcon(FontAwesome.Icon.faw_bars).withTag("/fentezi/").withIdentifier(3044),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_school).withIcon(FontAwesome.Icon.faw_bars).withTag("/school/").withIdentifier(3046),
            new SecondaryDrawerItem().withName(R.string.menu_anistar_erotic).withIcon(FontAwesome.Icon.faw_bars).withTag("/echchi-erotic/").withIdentifier(3048)
    };

    private static final IDrawerItem[] ANIMESPIRIT_MENU_ITEMS = new IDrawerItem[]{
            new PrimaryDrawerItem().withName(R.string.menu_latest).withIcon(FontAwesome.Icon.faw_home).withIdentifier(Category.LATEST.id),
            new PrimaryDrawerItem().withName(R.string.menu_genres).withIcon(FontAwesome.Icon.faw_tags).withIdentifier(Category.GENRES.id).withSelectable(false),
            new PrimaryDrawerItem().withName(R.string.menu_favorites).withIcon(FontAwesome.Icon.faw_heart).withIdentifier(Category.FAVORITES.id),

            new ExpandableDrawerItem().withName(R.string.menu_anime_rus).withIcon(FontAwesome.Icon.faw_sitemap).withSelectable(false).withSubItems(
                    new SecondaryDrawerItem().withName(R.string.menu_tv_shows).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rs/series-rus/").withIdentifier(Category.RUS_TV.id),
                    new SecondaryDrawerItem().withName(R.string.menu_ova).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rs/ova-rus/").withIdentifier(Category.RUS_OVA.id),
                    new SecondaryDrawerItem().withName(R.string.menu_ona).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rs/ona-rus/").withIdentifier(Category.RUS_ONA.id),
                    new SecondaryDrawerItem().withName(R.string.menu_movies).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rs/movie-rus/").withIdentifier(Category.RUS_MOVIES.id),
                    new SecondaryDrawerItem().withName(R.string.menu_specials).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rs/special-rus/").withIdentifier(Category.RUS_SPECIALS.id),
                    new SecondaryDrawerItem().withName(R.string.menu_live_action).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/movies/laction-rus/").withIdentifier(Category.RUS_LIVE_ACTION.id),
                    new SecondaryDrawerItem().withName(R.string.menu_ongoing).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/ongoing/").withIdentifier(Category.RUS_ONGOING.id),
                    new SecondaryDrawerItem().withName(R.string.menu_dorama).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/movies/dorama-rus/").withIdentifier(Category.RUS_DORAMA.id)
            ),
            new ExpandableDrawerItem().withName(R.string.menu_anime_sub).withIcon(FontAwesome.Icon.faw_sitemap).withSelectable(false).withSubItems(
                    new SecondaryDrawerItem().withName(R.string.menu_tv_shows).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rsb/series-russub/").withIdentifier(Category.SUB_TV.id),
                    new SecondaryDrawerItem().withName(R.string.menu_ova).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rsb/ova-russub/").withIdentifier(Category.SUB_OVA.id),
                    new SecondaryDrawerItem().withName(R.string.menu_ona).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rsb/ona-russub/").withIdentifier(Category.SUB_ONA.id),
                    new SecondaryDrawerItem().withName(R.string.menu_movies).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rsb/movie-russub/").withIdentifier(Category.SUB_MOVIES.id),
                    new SecondaryDrawerItem().withName(R.string.menu_specials).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/anime/rsb/special-russub/").withIdentifier(Category.SUB_SPECIALS.id),
                    new SecondaryDrawerItem().withName(R.string.menu_live_action).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/movies/laction-russub/").withIdentifier(Category.SUB_LIVE_ACTION.id),
                    new SecondaryDrawerItem().withName(R.string.menu_dorama).withLevel(2).withIcon(FontAwesome.Icon.faw_bars).withTag("/movies/dorama-russub/").withIdentifier(Category.SUB_DORAMA.id)
                    )
    };

    @Bind(R.id.toolbar)
    Toolbar toolbar = null;

    private AccountHeader headerResult = null;
    private Drawer result = null;
    MenuItem searchItem;
    private boolean doubleBackToExitPressedOnce;

    private OnFilterChangedListener onFilterChangedListener;
    private OnServiceChangedListener onServiceChangedListener;

    /**
     * Change sidebar menu item
     *
     * @param onFilterChangedListener OnFilterChangedListener
     */
    public void setOnFilterChangedListener(OnFilterChangedListener onFilterChangedListener) {
        this.onFilterChangedListener = onFilterChangedListener;
    }

    /**
     * Change service listener
     *
     * @param onServiceChangedListener OnServiceChangedListener
     */
    public void setOnServiceChangedListener(OnServiceChangedListener onServiceChangedListener) {
        this.onServiceChangedListener = onServiceChangedListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initInstances();
        initDrawer(savedInstanceState);
    }

    /**
     * Init component toolbar
     */
    private void initInstances() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Init Navigation Drawer
     *
     * @param savedInstanceState Bundle
     */
    private void initDrawer(Bundle savedInstanceState) {
        buildHeader(pref.getBoolean(COMPACT_HEADER, false), savedInstanceState);

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(getMenuItems())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            getSupportActionBar().setTitle(((Nameable) drawerItem).getName().getText(MainActivity.this));
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == Category.SETTINGS.id) {
                            showPreference();
                        }
                        if (drawerItem != null && onFilterChangedListener != null) {
                            onFilterChangedListener.onFilterChanged(drawerItem.getIdentifier(), (String) drawerItem.getTag());
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(Category.SETTINGS.id).withSelectable(false)
                )
                .withActionBarDrawerToggleAnimated(true)
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();

        result.getRecyclerView().setVerticalScrollBarEnabled(false);

        setCategoryCount();
    }

    /**
     * Show preference dialog
     */
    private void showPreference() {
        if (Build.VERSION.SDK_INT >= 21 && pref.getBoolean(PREF_ANIMATION, false)) {
            startActivity(new Intent(getApplicationContext(), PreferenceActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    /**
     * Init Navigation Drawer header with account switcher
     *
     * @param compact            boolean is compact mode on
     * @param savedInstanceState Bundle
     */
    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        final IProfile[] iProfiles = {
                //new ProfileDrawerItem().withName(getString(R.string.profile_animelend_name)).withNameShown(true).withEmail(getString(R.string.profile_animelend_url)).withIcon(getResources().getDrawable(R.drawable.profile_animelend)).withIdentifier(AnimeServices.ANIMELEND.id),
                new ProfileDrawerItem().withName(getString(R.string.profile_anidub_name)).withNameShown(true).withEmail(getString(R.string.profile_anidub_url)).withIcon(getResources().getDrawable(R.drawable.profile_anidub)).withIdentifier(AnimeServices.ANIDUB.id),
                new ProfileDrawerItem().withName(getString(R.string.profile_anistar_name)).withNameShown(true).withEmail(getString(R.string.profile_anistar_url)).withIcon(getResources().getDrawable(R.drawable.profile_anistar)).withIdentifier(AnimeServices.ANISTAR.id),
                new ProfileDrawerItem().withName(getString(R.string.profile_animespirit_name)).withNameShown(true).withEmail(getString(R.string.profile_animespirit_url)).withIcon(getResources().getDrawable(R.drawable.profile_animespirit)).withIdentifier(AnimeServices.ANIMESPIRIT.id),
                //new ProfileDrawerItem().withName("AnimeRu").withNameShown(true).withEmail("http://animeru.tv/").withIcon(getResources().getDrawable(R.drawable.profile_tmp)).withIdentifier(AnimeServices.ANIMERU.id)
        };
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(compact)
                .addProfiles(iProfiles)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //false if you have not consumed the event and it should close the drawer
                        if (!current) {
                            changeApiService(profile.getIdentifier());

                            Bundle params = new Bundle();
                            params.putLong(FirebaseAnalytics.Param.ITEM_ID, profile.getIdentifier());
                            params.putString(FirebaseAnalytics.Param.ITEM_NAME, profile.getName().getText());
                            mFirebaseAnalytics.logEvent("change_service", params);

                            if (onServiceChangedListener != null) {
                                // change fragment
                                onServiceChangedListener.onServiceChanged(profile.getIdentifier());
                                //change side menu
                                result.setItems(new ArrayList<>(Arrays.asList(getMenuItems())));
                                result.setSelection(Category.LATEST.id, false);
                                setCategoryCount();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        headerResult.setActiveProfile(pref.getLong(PREF_API_SERVICE, 2));
    }

    /**
     * Set favorite category badge
     */
    public void setCategoryCount() {
        if (result.getDrawerItems() != null && result.getDrawerItems().size() >= 5) {
            // проверка на общее избранное
            long count = 0;
            boolean common_favorites = pref.getBoolean("common_favorites", false);
            if (common_favorites) {
                count = Favorites.count(Favorites.class);
            } else {
                count = Favorites.count(Favorites.class, "service = ?",
                        new String[]{Long.toString(pref.getLong(PREF_API_SERVICE, 2))});
            }
            IDrawerItem item = result.getDrawerItem(Category.FAVORITES.id);
            ((BaseDrawerItem) item).withEnabled(count > 0);
            result.updateItem(item);
            result.updateBadge(Category.FAVORITES.id, new StringHolder(count + ""));
        }
    }

    /**
     * Get menu items by service id
     *
     * @return menuItems IDrawerItem[] menu items
     */
    private IDrawerItem[] getMenuItems() {
        IDrawerItem[] menuItems = null;
        long serviceId = pref.getLong(PREF_API_SERVICE, AnimeServices.ANIDUB.id);
        if (serviceId == AnimeServices.ANIDUB.id) {
            menuItems = ANIDUB_MENU_ITEMS;
        } else if (serviceId == AnimeServices.ANISTAR.id) {
            menuItems = ANISTAR_MENU_ITEMS;
        } else if (serviceId == AnimeServices.ANIMELEND.id) {
            menuItems = ANIMELEND_MENU_ITEMS;
        } else if (serviceId == AnimeServices.ANIMESPIRIT.id) {
            menuItems = ANIMESPIRIT_MENU_ITEMS;
        }
        return menuItems;
    }

    /**
     * Change api service
     *
     * @param profileId int service id
     */
    public void changeApiService(long profileId) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong(PREF_API_SERVICE, profileId);
        edit.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchItem.setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_search).paddingDp(3).colorRes(R.color.icons).sizeDp(24));

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName cn = new ComponentName(this, SearchResultActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (searchItem != null && searchItem.isActionViewExpanded()) {
            searchItem.collapseActionView();
        } else if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
            setCategoryCount();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.tap_twice_for_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;

                }
            }, 2000);
        }
    }

    @Override
    protected void onResume() {
        setCategoryCount();
        setCompactHeader();
        super.onResume();
    }

    /**
     * set drawer header type
     */
    private void setCompactHeader() {
        buildHeader(pref.getBoolean(COMPACT_HEADER, false), null);
        result.setHeader(headerResult.getView());
        headerResult.setDrawer(result);
    }

    /**
     * OnFilterChangedListener interface
     */
    public interface OnFilterChangedListener {
        public void onFilterChanged(long filter, String category);
    }

    /**
     * OnServiceChangedListener interface
     */
    public interface OnServiceChangedListener {
        public void onServiceChanged(long filter);
    }
}
