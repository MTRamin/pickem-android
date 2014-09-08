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

package de.mtrstudios.nflpickem.API.Data;

import android.content.Context;

import de.mtrstudios.nflpickem.R;

/**
 * Stores appData about a teams score
 */
public class TeamScore {

    private String team;
    private int season;
    private SeasonType type;

    private int w;
    private int l;
    private int t;

    public TeamScore(String team, int w, int l, int t) {
        this.team = team;
        this.w = w;
        this.l = l;
        this.t = t;
    }

    /**
     * Creates an empty score String
     */
    public static String getScoreEmpty(Context context) {
        return context.getString(R.string.score_team_empty);
    }

    /**
     * Creates a score string from the teams score
     */
    public String getScoreNice() {
        return "(" + this.w + "-" + this.l + "-" + this.t + ")";
    }

    public String getTeam() {
        return team;
    }

    public int getSeason() {
        return season;
    }

    public SeasonType getType() {
        return type;
    }

    public int getW() {
        return w;
    }

    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }
}
