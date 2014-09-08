/*
 * Copyright 2014 MTRamin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.mtrstudios.nflpickem.API.Responses;

import android.os.Parcel;
import android.os.Parcelable;

import de.mtrstudios.nflpickem.API.Data.SeasonType;

/**
 * Stores appData about information about the current season
 * Received as a response from the server
 */
public class SeasonInfo implements Parcelable {
    private int season;
    private int week;
    private SeasonType type;

    public SeasonInfo(int season, int week, SeasonType type) {
        this.season = season;
        this.week = week;
        this.type = type;
    }

    /**
     * Parses the appData and returns the season type as a string
     */
    public String getSeasonNice() {
        String prefix = "";

        if (type == SeasonType.PRE) {
            prefix = "Pre-";
        } else if (type == SeasonType.POST) {
            prefix = "Post-";
        }

        return prefix + "Season " + String.valueOf(season);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeasonInfo that = (SeasonInfo) o;

        if (season != that.season) return false;
        if (week != that.week) return false;
        if (type != that.type) return false;

        return true;
    }

    public int getSeason() {
        return season;
    }

    public int getWeek() {
        return week;
    }

    public SeasonType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int result = season;
        result = 31 * result + week;
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.season);
        parcel.writeInt(this.week);
        parcel.writeString(this.type.toString());
    }

    public SeasonInfo(Parcel in) {
        this.season = in.readInt();
        this.week = in.readInt();
        this.type = SeasonType.valueOf(in.readString());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SeasonInfo createFromParcel(Parcel in) {
            return new SeasonInfo(in);
        }

        @Override
        public SeasonInfo[] newArray(int i) {
            return new SeasonInfo[i];
        }
    };

    public void setWeek(int week) {
        this.week = week;
    }
}
