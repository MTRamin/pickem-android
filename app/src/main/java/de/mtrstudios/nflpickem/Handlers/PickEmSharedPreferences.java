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

import android.content.Context;
import android.content.SharedPreferences;

import de.mtrstudios.nflpickem.PickEmApplication;

/**
 * Handling Shared Preferences
 */
public class PickEmSharedPreferences {

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

    private static PickEmSharedPreferences mInstance;
    private SharedPreferences sharedPreferences;


    public PickEmSharedPreferences() {
        sharedPreferences = PickEmApplication.getAppContext().getSharedPreferences(PREFS_NAME, 0);

    }

    public static PickEmSharedPreferences getInstance() {
        if (mInstance == null) {
            mInstance = new PickEmSharedPreferences();
        }

        return mInstance;
    }

    /**
     * Converts an object to a JSON string and saves it
     */
    public void saveJson(String key, Object value) {
        String json = GsonHandler.getInstance().getGson().toJson(value);
        saveData(key, json);
    }

    /**
     * Saves a string
     */
    public void saveData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Saves a long
     */
    public void saveData(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Loads a string if the key is available
     */
    public String loadString(String key) {
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getString(key, "null");
        }
        return "null";
    }

    /**
     * Loads a long if the key is available
     */
    public long loadLong(String key) {
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getLong(key, 0);
        }
        return 0;
    }

    /**
     * Removes data from shared preferences
     */
    public void removeData(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
