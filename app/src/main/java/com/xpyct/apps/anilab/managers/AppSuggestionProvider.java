package com.xpyct.apps.anilab.managers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * anilab-android
 * Created by XpycT on 30.07.2015.
 */
public class AppSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.xpyct.apps.anilab.managers.AppSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public AppSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}