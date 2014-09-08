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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import de.mtrstudios.nflpickem.R;

/**
 * Stores appData about a game
 */
public class Game {

    private int season;
    private int week;
    private SeasonType type;

    private String homeTeam;
    private String awayTeam;

    private int homeScore;
    private int awayScore;

    private String gamekey;
    private String kickoff;

    private Quarter quarter;
    private String gameclock;

    private Team pick;
    private TeamScore homeTeamSeasonScore;
    private TeamScore awayTeamSeasonScore;

    public int getSeason() {
        return season;
    }

    public int getWeek() {
        return week;
    }

    public SeasonType getType() {
        return type;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public String getGamekey() {
        return gamekey;
    }

    public String getKickoff() {
        return kickoff;
    }

    public boolean isPreGame() {
        return quarter == Quarter.PREGAME;
    }

    public boolean isPostGame() {
        return (quarter == Quarter.FINAL || quarter == Quarter.FINALOVERTIME);
    }

    /**
     * Parses and returns the Kickoff time of this game
     */
    public Calendar getKickoffTime() {
        Calendar time = Calendar.getInstance();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        try {
            time.setTime(inputFormat.parse(this.kickoff));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    /**
     * Parses and returns the kickoff date of this game as a String
     * Takes into consideration the Setting about which timezone to use
     */
    public String getKickoffParsed(Context context) {
        if (isPreGame()) {
            String input = this.kickoff;

            // Date format received from server looks like this:
            // "2014-08-03T20:00:00-04:00"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd '-' HH:mm");

            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean useLocalTimezone = sharedPreferences.getBoolean("use_phone_timezone", true);

                if (!useLocalTimezone) { // Use EST (with AM/PM identifier)
                    outputFormat = new SimpleDateFormat("MM/dd '-' h:mma");
                    outputFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                }
                return outputFormat.format(inputFormat.parse(input));

            } catch (ParseException e) {
                e.printStackTrace();
                return "NO TIME";
            }
        } else if (quarter == Quarter.FINAL) {
            return context.getString(R.string.game_final);
        } else if (quarter == Quarter.FINALOVERTIME) {
            return context.getString(R.string.game_final_overtime);
        } else if (quarter == Quarter.HALFTIME) {
            return context.getString(R.string.game_halftime);
        } else {
            return getGameclockNice(context);
        }
    }

    /**
     * Returns the Gameclock as a nice String
     */
    public String getGameclockNice(Context context) {
        if (quarter != null && gameclock != null) {
            return context.getString(R.string.game_quarter) + this.quarter + " - " + this.gameclock;
        }

        return "";
    }

    /**
     * Calculates the winner of this game
     */
    public Team getWinner() {
        return (this.homeScore > this.awayScore) ? Team.HOME : Team.AWAY;
    }

    public Quarter getQuarter() {
        return quarter;
    }

    public String getGameclock() {
        return gameclock;
    }

    public Team getPick() {
        return pick;
    }

    public String getHomeTeamSeasonScore(Context context) {
        if (homeTeamSeasonScore != null) {
            return homeTeamSeasonScore.getScoreNice();
        }
        return TeamScore.getScoreEmpty(context);
    }

    public String getAwayTeamSeasonScore(Context context) {
        if (awayTeamSeasonScore != null) {
            return awayTeamSeasonScore.getScoreNice();
        }
        return TeamScore.getScoreEmpty(context);
    }

    public void setPick(Team pick) {
        this.pick = pick;
    }
}