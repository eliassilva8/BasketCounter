package com.ems.android.basketcounter.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Elias on 02/08/2018.
 */
public class GamePOJO implements Parcelable {
    private int id;
    private String date;
    private String homeTeamName;
    private String guestTeamName;
    private String homeTeamPoints;
    private String guestTeamPoints;

    public GamePOJO(int id, String date, String homeTeamName, String guestTeamName, String homeTeamPoints, String guestTeamPoints) {
        this.id = id;
        this.date = date;
        this.homeTeamName = homeTeamName;
        this.guestTeamName = guestTeamName;
        this.homeTeamPoints = homeTeamPoints;
        this.guestTeamPoints = guestTeamPoints;
    }

    protected GamePOJO(Parcel in) {
        id = in.readInt();
        date = in.readString();
        homeTeamName = in.readString();
        guestTeamName = in.readString();
        homeTeamPoints = in.readString();
        guestTeamPoints = in.readString();
    }

    public static final Creator<GamePOJO> CREATOR = new Creator<GamePOJO>() {
        @Override
        public GamePOJO createFromParcel(Parcel in) {
            return new GamePOJO(in);
        }

        @Override
        public GamePOJO[] newArray(int size) {
            return new GamePOJO[size];
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

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getGuestTeamName() {
        return guestTeamName;
    }

    public String getHomeTeamPoints() {
        return homeTeamPoints;
    }

    public String getGuestTeamPoints() {
        return guestTeamPoints;
    }
}
