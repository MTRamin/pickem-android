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
package de.mtrstudios.nflpickem.Handlers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.API.Data.Score;
import de.mtrstudios.nflpickem.API.Responses.Highscores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;

/**
 * Class holding all the apps appData
 */
public class PickEmDataHandler {

    private static PickEmDataHandler instance;

    // Essential appData about the user
    private String userToken = "null";
    private String userName = "null";

    // App Data
    private SeasonInfo seasonInfo;
    private List<Game> games;
    private Map<Integer, Score> scoresByWeek;
    private List<Highscore> highscores;
    private Map<Integer, Integer> gamesPerWeek;
    private Map<Integer, Highscores> highscoresPerWeek;

    // Dates of last update calls
    private Calendar lastSeasonUpdate = new GregorianCalendar(2000, 0, 1);
    private Calendar lastDataUpdate = new GregorianCalendar(2000, 0, 1);
    private Calendar appStart = new GregorianCalendar();


    public PickEmDataHandler() {
        // Initialize collections
        this.games = new ArrayList<Game>();
        this.scoresByWeek = new TreeMap<Integer, Score>();
        this.gamesPerWeek = new TreeMap<Integer, Integer>();
        this.highscores = new ArrayList<Highscore>();
        this.highscoresPerWeek = new HashMap<Integer, Highscores>();

        // Load SharedPreferences
        loadDataFromSharedPreferences();
    }

    public static PickEmDataHandler getInstance() {
        if (instance == null) {
            Log.i("PickEmDataHandler", "Creating new instance!");
            instance = new PickEmDataHandler();
        }

        return instance;
    }

    /**
     * Pulls appData from the shared preferences
     * This appData is then stored in the appData variables
     * As appData from collections is stored as a JSON String, it needs to be deserialized
     */
    private void loadDataFromSharedPreferences() {
        PickEmSharedPreferences preferences = PickEmSharedPreferences.getInstance();
        Gson gson = GsonHandler.getInstance().getGson();

        // User Name and Access-Token
        this.userToken = preferences.loadString(PickEmSharedPreferences.NAME_TOKEN);
        this.userName = preferences.loadString(PickEmSharedPreferences.NAME_USERNAME);

        // Last Season Info update
        long lastUpdateMs = preferences.loadLong(PickEmSharedPreferences.NAME_LAST_SEASON_UPDATE);
        if (lastUpdateMs != 0) {
            this.lastSeasonUpdate.setTimeInMillis(lastUpdateMs);
        }

        // Last appData update
        long lastDataUpdateMs = preferences.loadLong(PickEmSharedPreferences.NAME_LAST_DATA_UPDATE);
        if (lastDataUpdateMs != 0) {
            this.lastDataUpdate.setTimeInMillis(lastUpdateMs);
        }

        // Season Info
        String jsonSeasonInfo = preferences.loadString(PickEmSharedPreferences.NAME_SEASON_INFO);
        if (!jsonSeasonInfo.equals("null")) {
            seasonInfo = gson.fromJson(jsonSeasonInfo, SeasonInfo.class);
        }

        // Games
        String jsonGames = preferences.loadString(PickEmSharedPreferences.NAME_GAMES);
        if (!jsonGames.equals("null")) {
            TypeToken<List<Game>> typeToken = new TypeToken<List<Game>>() {
            };
            games = gson.fromJson(jsonGames, typeToken.getType());
        }

        // Scores
        String jsonScores = preferences.loadString(PickEmSharedPreferences.NAME_SCORES);
        if (!jsonScores.equals("null")) {
            TypeToken<HashMap<Integer, Score>> typeToken = new TypeToken<HashMap<Integer, Score>>() {
            };
            scoresByWeek = gson.fromJson(jsonScores, typeToken.getType());
        }

        // Games/Week
        String jsonGamesPerWeek = preferences.loadString(PickEmSharedPreferences.NAME_GAMES_PER_WEEK);
        if (!jsonGamesPerWeek.equals("null")) {
            TypeToken<HashMap<Integer, Integer>> typeToken = new TypeToken<HashMap<Integer, Integer>>() {
            };
            gamesPerWeek = gson.fromJson(jsonGamesPerWeek, typeToken.getType());
        }

        // HighScores
        String jsonHighscores = preferences.loadString(PickEmSharedPreferences.NAME_HIGHSCORES);
        if (!jsonHighscores.equals("null")) {
            TypeToken<List<Highscore>> typeToken = new TypeToken<List<Highscore>>() {
            };
            highscores = gson.fromJson(jsonHighscores, typeToken.getType());
        }
    }

    /**
     * If the season information has changed, this appData will be removed to reset the client
     */
    public void clearDataForRefresh() {
        this.games.clear();
        this.gamesPerWeek.clear();
        this.scoresByWeek.clear();
        this.highscores.clear();

        PickEmSharedPreferences preferences = PickEmSharedPreferences.getInstance();

        preferences.removeData(PickEmSharedPreferences.NAME_GAMES);
        preferences.removeData(PickEmSharedPreferences.NAME_HIGHSCORES);
        preferences.removeData(PickEmSharedPreferences.NAME_SCORES);
        preferences.removeData(PickEmSharedPreferences.NAME_GAMES_PER_WEEK);
    }

    /**
     * Removes all appData the app has stored including the user appData
     */
    public void signOut() {

        this.userName = "null";
        this.userToken = "null";
        this.seasonInfo = null;
        this.games.clear();
        this.scoresByWeek.clear();
        this.gamesPerWeek.clear();
        this.highscores.clear();

        PickEmSharedPreferences preferences = PickEmSharedPreferences.getInstance();

        preferences.removeData(PickEmSharedPreferences.NAME_USERNAME);
        preferences.removeData(PickEmSharedPreferences.NAME_TOKEN);
        preferences.removeData(PickEmSharedPreferences.NAME_SEASON_INFO);
        preferences.removeData(PickEmSharedPreferences.NAME_LAST_SEASON_UPDATE);
        preferences.removeData(PickEmSharedPreferences.NAME_LAST_DATA_UPDATE);
        preferences.removeData(PickEmSharedPreferences.NAME_GAMES);
        preferences.removeData(PickEmSharedPreferences.NAME_HIGHSCORES);
        preferences.removeData(PickEmSharedPreferences.NAME_SCORES);
        preferences.removeData(PickEmSharedPreferences.NAME_GAMES_PER_WEEK);
        this.lastSeasonUpdate = new GregorianCalendar(2000, 0, 1);
        this.lastDataUpdate = new GregorianCalendar(2000, 0, 1);
        this.appStart = new GregorianCalendar();
    }

    /**
     * Checks if appData about games is available
     */
    public boolean isDataAvailable() {
        return ((this.seasonInfo != null) && (this.games.size() > 0));
    }

    /**
     * Sets the user token and saves it in the shared preferences
     */
    public void setUserToken(String userToken) {
        this.userToken = userToken;
        PickEmSharedPreferences.getInstance().saveData(PickEmSharedPreferences.NAME_TOKEN, userToken);
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     * Adds a highscore to the app appData
     * If it already existed the appData will be updated
     */
    public void addData(int week, Highscores scores) {
        this.highscoresPerWeek.put(week, scores);
    }

    /**
     * Saves appData about games in the shared preferences
     * The Collection is serialized into JSON format and stored as a string
     */
    public void saveGames() {
        PickEmSharedPreferences.getInstance().saveJson(PickEmSharedPreferences.NAME_GAMES, this.games);
    }

    /**
     * Saves appData about games in a week into JSON format and stored as a string
     */
    private void saveGamesPerWeek() {
        PickEmSharedPreferences.getInstance().saveJson(PickEmSharedPreferences.NAME_GAMES_PER_WEEK, this.gamesPerWeek);
    }

    public Map<Integer, Score> getScoresByWeek() {
        return scoresByWeek;
    }

    /**
     * Gets the total score of the currently logged in user
     */
    public int getTotalScore() {
        int score = 0;
        if (scoresByWeek.size() > 0) {
            for (Integer key : scoresByWeek.keySet()) {
                score += scoresByWeek.get(key).getScore();
            }

        }
        return score;
    }

    /**
     * Returns the score of the user for a specific week in the season
     * If no score appData is available it returns a safe value
     */
    public Score getScoreForWeek(SeasonInfo season) {
        if (scoresByWeek.containsKey(season.getWeek())) {
            return scoresByWeek.get(season.getWeek());
        } else {
            return new Score(0, 0);
        }
    }

    /**
     * Sets the scores per week for the current user
     * Saves this appData in the shared preference as a JSON String (Gson serialization)
     */
    public void setScoresByWeek(Map<Integer, Score> scoresByWeek) {
        this.scoresByWeek = scoresByWeek;
        PickEmSharedPreferences.getInstance().saveJson(PickEmSharedPreferences.NAME_SCORES, this.scoresByWeek);
    }

    /**
     * Gets the name of the currently logged in user if it is available
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets and stores the username
     * Saves it into the shared preferences
     */
    public void setUserName(String userName) {
        this.userName = userName;
        PickEmSharedPreferences.getInstance().saveData(PickEmSharedPreferences.NAME_USERNAME, this.userName);
    }

    /**
     * Sets and stores the season info
     * Checks if season Info has changed (week change/season change)
     * and removes some data accordingly so it can be refreshed
     */
    public void setSeasonInfo(SeasonInfo seasonInfo) {
        if ((this.seasonInfo == null) || (!seasonInfo.equals(this.seasonInfo))) {
            clearDataForRefresh();
        }

        this.seasonInfo = seasonInfo;
        PickEmSharedPreferences.getInstance().saveJson(PickEmSharedPreferences.NAME_SEASON_INFO, this.seasonInfo);
    }

    /**
     * Gets the total amount of games played in a specific season
     */
    public List<Game> getGamesForSeason(SeasonInfo season) {
        List<Game> results = new ArrayList<Game>();

        for (Game game : games) {
            if ((game.getSeason() == season.getSeason()) && (game.getWeek() == season.getWeek()) && (game.getType().equals(season.getType()))) {
                results.add(game);
            }
        }

        return results;
    }

    /**
     * Sets the update date for the last seasoninfo update
     * Saves into the shared preferences
     */
    public void setLastSeasonUpdate(Calendar lastUpdate) {
        this.lastSeasonUpdate = lastUpdate;
        PickEmSharedPreferences.getInstance().saveData(PickEmSharedPreferences.NAME_LAST_SEASON_UPDATE, this.lastSeasonUpdate.getTimeInMillis());
    }

    /**
     * Sets the update date for the last app appData update
     * Saves into the shared preferences
     */
    public void setLastDataUpdate(Calendar lastUpdate) {
        this.lastDataUpdate = lastUpdate;
        PickEmSharedPreferences.getInstance().saveData(PickEmSharedPreferences.NAME_LAST_DATA_UPDATE, this.lastDataUpdate.getTimeInMillis());
    }

    public List<Highscore> getHighscores() {
        return highscores;
    }

    public void setHighscores(List<Highscore> highscores) {
        this.highscores = highscores;
    }

    public Calendar getAppStart() {
        return appStart;
    }

    public Map<Integer, Integer> getGamesPerWeek() {
        return gamesPerWeek;
    }

    /**
     * Gets the total amount of games played in the current season so far.
     */
    public int getTotalGamesPlayed() {
        int result = 0;
        for (Map.Entry<Integer, Integer> entry : gamesPerWeek.entrySet()) {
            result += entry.getValue();
        }

        return result;
    }

    /**
     * Sets and saves the games per week data
     */
    public void setGamesPerWeek(Map<Integer, Integer> gamesPerWeek) {
        this.gamesPerWeek = gamesPerWeek;
        saveGamesPerWeek();
    }

    /**
     * Returns the amount of games played in a specific week of the season
     */
    public int getGamesCountForWeek(SeasonInfo season) {
        if (gamesPerWeek.size() > 0) {
            if (gamesPerWeek.containsKey(season.getWeek())) {
                return gamesPerWeek.get(season.getWeek());
            }
            return 0;
        }
        return 0;
    }

    /**
     * Gets the highscores for a specific week of this season
     */
    public Highscores getHighscoresForWeek(int week) {
        if (!highscoresPerWeek.isEmpty() && highscoresPerWeek.containsKey(week)) {
            return this.highscoresPerWeek.get(week);
        }
        return new Highscores();
    }

    public Map<Integer, Highscores> getHighscoresPerWeek() {
        return highscoresPerWeek;
    }


    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }

    public String getUserToken() {
        return userToken;
    }

    public List<Game> getGames() {
        return games;
    }

    public Calendar getLastSeasonUpdate() {
        return lastSeasonUpdate;
    }

    public Calendar getLastDataUpdate() {
        return lastDataUpdate;
    }
}
