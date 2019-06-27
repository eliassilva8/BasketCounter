package com.ems.android.basketcounter.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Elias on 20/11/2018.
 */

@Entity(tableName = "games_saved")
public class Match implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo
    private String date;

    @NonNull
    @ColumnInfo(name = "home")
    private String homeTeamName;

    @NonNull
    @ColumnInfo(name = "guest")
    private String guestTeamName;

    @NonNull
    @ColumnInfo(name = "home_points")
    private String homeTeamPoints;

    @NonNull
    @ColumnInfo(name = "guest_points")
    private String guestTeamPoints;

    public Match(@NonNull int id, @NonNull String date, @NonNull String homeTeamName, @NonNull String guestTeamName, @NonNull String homeTeamPoints, @NonNull String guestTeamPoints) {
        this.id = id;
        this.date = date;
        this.homeTeamName = homeTeamName;
        this.guestTeamName = guestTeamName;
        this.homeTeamPoints = homeTeamPoints;
        this.guestTeamPoints = guestTeamPoints;
    }

    @NonNull
    public int getId() {
        return id;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    @NonNull
    public String getHomeTeamName() {
        return homeTeamName;
    }

    @NonNull
    public String getGuestTeamName() {
        return guestTeamName;
    }

    @NonNull
    public String getHomeTeamPoints() {
        return homeTeamPoints;
    }

    @NonNull
    public String getGuestTeamPoints() {
        return guestTeamPoints;
    }

    protected Match(Parcel in) {
        id = in.readInt();
        date = in.readString();
        homeTeamName = in.readString();
        guestTeamName = in.readString();
        homeTeamPoints = in.readString();
        guestTeamPoints = in.readString();
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeString(homeTeamName);
        dest.writeString(guestTeamName);
        dest.writeString(homeTeamPoints);
        dest.writeString(guestTeamPoints);
    }
}
