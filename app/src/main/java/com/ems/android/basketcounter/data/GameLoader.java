package com.ems.android.basketcounter.data;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.ems.android.basketcounter.utils.GameDbUtils;

import java.util.List;

/**
 * Created by Elias on 02/08/2018.
 */
public class GameLoader extends AsyncTaskLoader<List<GamePOJO>> {
    private final Uri uri;

    public GameLoader(Context context, Uri contentUri) {
        super(context);
        this.uri = contentUri;
    }

    @Nullable
    @Override
    public List<GamePOJO> loadInBackground() {
        return GameDbUtils.getGameFromDatabase(getContext(), uri);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
