package com.ems.android.basketcounter.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ems.android.basketcounter.data.GameDbContract.GameEntry;
import com.ems.android.basketcounter.data.GamePOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elias on 02/08/2018.
 */
public class GameDbUtils {
    public static List<GamePOJO> getGameFromDatabase(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        List<GamePOJO> games = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(GameEntry._ID));
            String date = cursor.getString(cursor.getColumnIndex(GameEntry.COLUMN_DATE));
            String homeTeamName = cursor.getString(cursor.getColumnIndex(GameEntry.COLUMN_HOME_TEAM));
            String guestTeamName = cursor.getString(cursor.getColumnIndex(GameEntry.COLUMN_GUEST_TEAM));
            String homeTeamPoints = cursor.getString(cursor.getColumnIndex(GameEntry.COLUMN_HOME_POINTS));
            String guestTeamPoints = cursor.getString(cursor.getColumnIndex(GameEntry.COLUMN_GUEST_POINTS));
            games.add(new GamePOJO(id, date, homeTeamName, guestTeamName, homeTeamPoints, guestTeamPoints));
        }
        cursor.close();
        return games;
    }
}
