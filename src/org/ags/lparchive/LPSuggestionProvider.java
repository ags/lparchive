package org.ags.lparchive;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Provides suggestions when searching for LPs.  
 */
public class LPSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "org.ags.lparchive.LPSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public LPSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
