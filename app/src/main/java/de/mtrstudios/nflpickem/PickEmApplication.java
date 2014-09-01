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

package de.mtrstudios.nflpickem;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.mtrstudios.nflpickem.API.Data.Comparators.GamesComparator;
import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.API.Data.Pick;
import de.mtrstudios.nflpickem.API.Data.Score;
import de.mtrstudios.nflpickem.API.Data.TeamScore;
import de.mtrstudios.nflpickem.API.PickEmAPI;
import de.mtrstudios.nflpickem.API.Responses.Highscores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import retrofit.RestAdapter;

/**
 * Custom Application Class to store all data and handle necessary references
 */
public class PickEmApplication extends Application {

    // URL of the API
    public static final String API_URL = "https://mtrpickem.herokuapp.com";

    // Name of the preferences File
    public static final String PREFS_NAME = "PickEmPrefs";

    // Names of the different preferences
    public static final String NAME_TOKEN = "user_token";
    public static final String NAME_USERNAME = "user_name";
    public static final String NAME_SEASON_INFO = "season_info";
    public static final String NAME_LAST_SEASON_UPDATE = "last_season_update";
    public static final String NAME_LAST_DATA_UPDATE = "last_data_update";
    public static final String NAME_GAMES = "games";
    public static final String NAME_PICKS = "picks";
    public static final String NAME_SCORES = "scores";
    public static final String NAME_GAMES_PER_WEEK = "games_per_week";
    public static final String NAME_TEAM_SCORES = "team_scores";
    public static final String NAME_HIGHSCORES = "highscores";

    // Constants
    public static final int DOWNLOADS_NEEDED_LIVE = 5;
    public static final int DOWNLOADS_NEEDED_RECAP = 2;
    public static final int ANIMATION_DURATION = 200;

    // Global
    private boolean picksEnabled = false;

    // References to Gson and the Retrofit API
    private Gson gson;
    private PickEmAPI api;

    // Essential data about the user
    private String userToken = "null";
    private String userName = "null";

    // Dates of last update calls
    private Calendar lastSeasonUpdate = new GregorianCalendar(2000, 0, 1);
    private Calendar lastDataUpdate = new GregorianCalendar(2000, 0, 1);
    private Calendar appStart = new GregorianCalendar();
    private Calendar lastUpdateScores = new GregorianCalendar(2000, 0, 1);

    // App Data
    private SeasonInfo seasonInfo;
    private Map<String, Game> games;
    private Map<Integer, Score> scoresByWeek;
    private List<Highscore> highscores;
    private Map<String, String> picks;
    private Map<Integer, Integer> gamesPerWeek;
    private Map<String, TeamScore> teamScores;
    private Map<Integer, Highscores> highscoresPerWeek;

    @Override
    public void onCreate() {

        this.games = new HashMap<String, Game>();
        this.scoresByWeek = new TreeMap<Integer, Score>();
        this.picks = new HashMap<String, String>();
        this.gamesPerWeek = new TreeMap<Integer, Integer>();
        this.teamScores = new HashMap<String, TeamScore>();
        this.highscores = new ArrayList<Highscore>();
        this.highscoresPerWeek = new HashMap<Integer, Highscores>();

        initializeGson();
        initializeRESTClient();
        initializeSettings();

        loadSharedPreferences();


        super.onCreate();
    }

    /**
     * Initializes the Gson instance
     */
    private void initializeGson() {
        this.gson = new Gson();
    }

    /**
     * Initializes the Retrofit client
     */
    private void initializeRESTClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();

        this.api = restAdapter.create(PickEmAPI.class);
    }

    /**
     * Initializes the settings values
     */
    private void initializeSettings() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    /**
     * Pulls data from the shared preferences
     * This data is then stored in the data variables
     * As data from collections is stored as a JSON String, it needs to be deserialized
     */
    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PickEmApplication.PREFS_NAME, 0);

        // User Name and Access-Token
        this.userToken = sharedPreferences.getString(PickEmApplication.NAME_TOKEN, "null");
        this.userName = sharedPreferences.getString(PickEmApplication.NAME_USERNAME, "null");

        // Last Season Info update
        long lastUpdateMs = sharedPreferences.getLong(NAME_LAST_SEASON_UPDATE, 0);
        if (lastUpdateMs != 0) {
            this.lastSeasonUpdate.setTimeInMillis(lastUpdateMs);
        }

        // Last data update
        long lastDataUpdateMs = sharedPreferences.getLong(NAME_LAST_DATA_UPDATE, 0);
        if (lastDataUpdateMs != 0) {
            this.lastDataUpdate.setTimeInMillis(lastUpdateMs);
        }

        // Season Info
        String jsonSeasonInfo = sharedPreferences.getString(NAME_SEASON_INFO, "null");
        if (!jsonSeasonInfo.equals("null")) {
            seasonInfo = gson.fromJson(jsonSeasonInfo, SeasonInfo.class);
        }

        // Games
        String jsonGames = sharedPreferences.getString(NAME_GAMES, "null");
        if (!jsonGames.equals("null")) {
            TypeToken<HashMap<String, Game>> typeToken = new TypeToken<HashMap<String, Game>>() {
            };
            games = gson.fromJson(jsonGames, typeToken.getType());
        }

        // Picks
        String jsonPicks = sharedPreferences.getString(NAME_PICKS, "null");
        if (!jsonPicks.equals("null")) {
            TypeToken<HashMap<String, String>> typeToken = new TypeToken<HashMap<String, String>>() {
            };
            picks = gson.fromJson(jsonPicks, typeToken.getType());
        }

        // Scores
        String jsonScores = sharedPreferences.getString(NAME_SCORES, "null");
        if (!jsonScores.equals("null")) {
            TypeToken<HashMap<Integer, Score>> typeToken = new TypeToken<HashMap<Integer, Score>>() {
            };
            scoresByWeek = gson.fromJson(jsonScores, typeToken.getType());
        }

        // Games/Week
        String jsonGamesPerWeek = sharedPreferences.getString(NAME_GAMES_PER_WEEK, "null");
        if (!jsonGamesPerWeek.equals("null")) {
            TypeToken<HashMap<Integer, Integer>> typeToken = new TypeToken<HashMap<Integer, Integer>>() {
            };
            gamesPerWeek = gson.fromJson(jsonGamesPerWeek, typeToken.getType());
        }

        // TeamScores
        String jsonTeamScores = sharedPreferences.getString(NAME_TEAM_SCORES, "null");
        if (!jsonTeamScores.equals("null")) {
            TypeToken<HashMap<String, TeamScore>> typeToken = new TypeToken<HashMap<String, TeamScore>>() {
            };
            teamScores = gson.fromJson(jsonTeamScores, typeToken.getType());
        }

        // HighScores
        String jsonHighscores = sharedPreferences.getString(NAME_HIGHSCORES, "null");
        if (!jsonHighscores.equals("null")) {
            TypeToken<List<Highscore>> typeToken = new TypeToken<List<Highscore>>() {
            };
            highscores = gson.fromJson(jsonHighscores, typeToken.getType());
        }
    }

    /**
     * Clears the current data that is shown to the user from the variables and the shared preferences
     */
    public void clearCurrentData() {
        this.games.clear();
        this.picks.clear();
        this.highscores.clear();

        SharedPreferences.Editor editor = getSharedPreferences(PickEmApplication.PREFS_NAME, 0).edit();
        editor.remove(NAME_GAMES);
        editor.remove(NAME_PICKS);
        editor.remove(NAME_HIGHSCORES);

        editor.apply();
    }

    /**
     * If the season information has changed, this data will be removed to reset the client
     */
    public void seasonChanged() {
        this.games.clear();
        this.picks.clear();
        this.gamesPerWeek.clear();
        this.scoresByWeek.clear();
        this.highscores.clear();
        this.teamScores.clear();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(NAME_SCORES);
        editor.remove(NAME_HIGHSCORES);
        editor.remove(NAME_GAMES);
        editor.remove(NAME_PICKS);
        editor.remove(NAME_GAMES_PER_WEEK);
        editor.remove(NAME_TEAM_SCORES);

        editor.apply();
    }

    /**
     * Removes all data the app has stored including the user data
     */
    public void signOut() {
        this.userName = "null";
        this.userToken = "null";
        this.seasonInfo = null;
        this.picks.clear();
        this.games.clear();
        this.scoresByWeek.clear();
        this.gamesPerWeek.clear();
        this.teamScores.clear();
        this.highscores.clear();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(NAME_USERNAME);
        editor.remove(NAME_TOKEN);
        editor.remove(NAME_SEASON_INFO);
        editor.remove(NAME_LAST_SEASON_UPDATE);
        editor.remove(NAME_LAST_DATA_UPDATE);
        editor.remove(NAME_GAMES);
        editor.remove(NAME_PICKS);
        editor.remove(NAME_SCORES);
        editor.remove(NAME_GAMES_PER_WEEK);
        editor.remove(NAME_TEAM_SCORES);

        editor.apply();

        this.lastSeasonUpdate = new GregorianCalendar(2000, 0, 1);
        this.lastDataUpdate = new GregorianCalendar(2000, 0, 1);
        this.appStart = new GregorianCalendar();
    }

    /**
     * Checks if data about games is available
     */
    public boolean isDataAvailable() {
        return ((this.seasonInfo != null) && (this.games.size() != 0));
    }

    public PickEmAPI getApi() {
        return api;
    }

    public String getUserToken() {
        return userToken;
    }

    /**
     * Sets the user token and saves it in the shared preferences
     */
    public void setUserToken(String userToken) {
        SharedPreferences sharedPreferences = getSharedPreferences(PickEmApplication.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME_TOKEN, userToken);
        editor.apply();

        this.userToken = userToken;
    }

    /**
     * Adds a game to the app data, updates data about a game if it already exists
     */
    public void addData(Game game) {
        if (games.containsKey(game.getGamekey())) {
            games.remove(game.getGamekey());
        }
        games.put(game.getGamekey(), game);
    }

    /**
     * Saves data about games in the shared preferences
     * The Collection is serialized into JSON format and stored as a string
     */
    public void saveGames() {
        String jsonGames = gson.toJson(this.games);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();

        editor.putString(NAME_GAMES, jsonGames);
        editor.apply();
    }

    /**
     * Saves data about picks in the shared preferences
     * The Collection is serialized into JSON format and stored as a string
     */
    public void savePicks() {
        String jsonPicks = gson.toJson(this.picks);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();

        editor.putString(NAME_PICKS, jsonPicks);
        editor.apply();

    }

    /**
     * Saves data about games in a week into JSON format and stored as a string
     */
    public void saveGamesPerWeek() {
        String jsonGamesPerWeek = gson.toJson(this.gamesPerWeek);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();

        editor.putString(NAME_GAMES_PER_WEEK, jsonGamesPerWeek);
        editor.apply();

    }

    /**
     * Saves data about the teams scores in the shared preferences
     * The Collection is serialized into JSON format and stored as a string
     */
    public void saveTeamScores() {
        String jsonTeamScores = gson.toJson(this.teamScores);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();

        editor.putString(NAME_TEAM_SCORES, jsonTeamScores);
        editor.apply();

    }

    /**
     * Adds a pick to the apps data
     * If that pick already existed it will be updated
     */
    public void addData(Pick pick) {
        if (picks.containsKey(pick.getGamekey())) {
            picks.remove(pick.getGamekey());
        }
        picks.put(pick.getGamekey(), pick.getPick());
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
     * If no score data is available it returns a safe value
     */
    public Score getScoreForWeek(SeasonInfo season) {
        Score score = scoresByWeek.get(season.getWeek());

        if (score == null) {
            score = new Score(0, 0);
        }

        return score;
    }

    /**
     * Sets the scores per week for the current user
     * Saves this data in the shared preference as a JSON String (Gson serialization)
     */
    public void setScoresByWeek(Map<Integer, Score> scoresByWeek) {
        this.scoresByWeek = scoresByWeek;

        String jsonScores = gson.toJson(this.scoresByWeek);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString(NAME_SCORES, jsonScores);

        editor.apply();
    }

    /**
     * Gets the name of the currently logged in user if it is available
     */
    public String getUserName() {
        return (userName.equals("null")) ? "No Username :(" : userName;
    }

    /**
     * Sets and stores the username
     * Saves it into the shared preferences
     */
    public void setUserName(String userName) {
        SharedPreferences sharedPreferences = getSharedPreferences(PickEmApplication.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME_USERNAME, userName);
        editor.apply();

        this.userName = userName;
    }

    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }

    /**
     * Sets and stores the season info
     */
    public void setSeasonInfo(SeasonInfo seasonInfo) {
        this.seasonInfo = seasonInfo;

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString(NAME_SEASON_INFO, gson.toJson(this.seasonInfo));

        editor.apply();
    }

    public Map<String, Game> getGames() {
        return games;
    }

    /**
     * Gets the total amount of games played in a specific week
     */
    public List<Game> getGamesForSeason(SeasonInfo season) {
        List<Game> results = new ArrayList<Game>();

        for (Map.Entry<String, Game> entry : games.entrySet()) {
            if ((entry.getValue().getSeason() == season.getSeason()) && (entry.getValue().getWeek() == season.getWeek()) && (entry.getValue().getType().equals(season.getType()))) {
                results.add(entry.getValue());
            }
        }
        Collections.sort(results, new GamesComparator());

        return results;
    }

    public Calendar getLastSeasonUpdate() {
        return lastSeasonUpdate;
    }

    /**
     * Sets the update date for the last seasoninfo update
     * Saves into the shared preferences
     */
    public void setLastSeasonUpdate(Calendar lastUpdate) {
        this.lastSeasonUpdate = lastUpdate;

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putLong(NAME_LAST_SEASON_UPDATE, this.lastSeasonUpdate.getTimeInMillis());

        editor.apply();
    }

    public Calendar getLastDataUpdate() {
        return lastDataUpdate;
    }

    /**
     * Sets the update date for the last app data update
     * Saves into the shared preferences
     */
    public void setLastDataUpdate(Calendar lastUpdate) {
        this.lastDataUpdate = lastUpdate;

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putLong(NAME_LAST_DATA_UPDATE, this.lastDataUpdate.getTimeInMillis());

        editor.apply();
    }

    public Map<String, String> getPicks() {
        return picks;
    }

    /**
     * Gets the pick for a specific game indicated by its gamekey
     */
    public Pick getPickForGame(String gamekey) {
        return new Pick(gamekey, picks.get(gamekey));
    }

    /**
     * Gets the picks for a list of Games
     */
    public List<Pick> getPicksForGames(List<Game> games) {
        List<Pick> results = new ArrayList<Pick>();

        for (Game game : games) {
            results.add(getPickForGame(game.getGamekey()));
        }

        return results;
    }

    public List<Highscore> getHighscores() {
        return highscores;
    }

    public void setHighscores(List<Highscore> highscores) {
        this.highscores = highscores;
    }

    public Calendar getLastUpdateScores() {
        return lastUpdateScores;
    }

    public void setLastUpdateScores(Calendar lastUpdateScores) {
        this.lastUpdateScores = lastUpdateScores;
    }

    public Calendar getAppStart() {
        return appStart;
    }

    public boolean isPicksEnabled() {
        return picksEnabled;
    }

    public void setPicksEnabled(boolean picksEnabled) {
        this.picksEnabled = picksEnabled;
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

    public void setGamesPerWeek(Map<Integer, Integer> gamesPerWeek) {
        this.gamesPerWeek = gamesPerWeek;
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

    public Map<String, TeamScore> getTeamScores() {
        return teamScores;
    }

    public void setTeamScores(Map<String, TeamScore> teamScores) {
        this.teamScores = teamScores;
    }

    /**
     * Gets the score for a specific team as a string
     */
    public String getScoreForTeam(String team) {
        if (teamScores.containsKey(team)) {
            return teamScores.get(team).getScoreNice();
        } else {
            return TeamScore.getScoreEmpty();
        }

    }

    /**
     * Adds a highscore to the app data
     * If it already existed the data will be updated
     */
    public void addHighscoresForWeek(int week, Highscores scores) {
        if (this.highscoresPerWeek.containsKey(week)) {
            this.highscoresPerWeek.remove(week);
        }
        this.highscoresPerWeek.put(week, scores);
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
}
