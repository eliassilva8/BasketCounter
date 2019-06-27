package com.ems.android.basketcounter.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Created by Elias on 20/11/2018.
 */
@Database(entities = {Match.class}, version = 2)
public abstract class MatchRoomDatabase extends RoomDatabase {
    public abstract MatchDao matchDao();

    private static volatile MatchRoomDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) { }};

    static MatchRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MatchRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MatchRoomDatabase.class, "GamesSaved")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
