package com.ems.android.basketcounter.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Elias on 01/08/2018.
 */
public final class GameDbContract {
    public static final String CONTENT_AUTHORITY = "com.ems.android.basketcounter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_GAMES = "games_saved";

    private GameDbContract() {
    }

    public static class GameEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_GAMES)
                .build();

        public static final String TABLE_NAME = PATH_GAMES;
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HOME_TEAM = "home";
        public static final String COLUMN_GUEST_TEAM = "guest";
        public static final String COLUMN_HOME_POINTS = "home_points";
        public static final String COLUMN_GUEST_POINTS = "guest_points";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + GameEntry.TABLE_NAME + " (" +
                    GameEntry._ID + " INTEGER PRIMARY KEY," +
                    GameEntry.COLUMN_DATE + " TEXT," +
                    GameEntry.COLUMN_HOME_TEAM + " TEXT," +
                    GameEntry.COLUMN_GUEST_TEAM + " TEXT," +
                    GameEntry.COLUMN_HOME_POINTS + " TEXT," +
                    GameEntry.COLUMN_GUEST_POINTS + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME;
}
