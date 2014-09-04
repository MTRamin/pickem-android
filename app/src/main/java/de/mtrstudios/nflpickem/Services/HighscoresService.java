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
package de.mtrstudios.nflpickem.Services;

import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.API.PickEmAPI;
import de.mtrstudios.nflpickem.API.Response;
import de.mtrstudios.nflpickem.API.Responses.Highscores;
import de.mtrstudios.nflpickem.Events.Error.ApiErrorEvent;
import de.mtrstudios.nflpickem.Events.Return.HighscoresLoadedEvent;
import de.mtrstudios.nflpickem.Events.Return.UserScoresLoadedEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.LoadHighscoresEvent;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Service to load highscores
 */
public class HighscoresService extends LoaderService {

    public HighscoresService(PickEmAPI api, Bus bus) {
        super(api, bus);
    }

    /**
     * Receives event to load highscores
     */
    @Subscribe
    public void onLoadHighscores(LoadHighscoresEvent event) {
        Log.i("HighscoreService", "Found event!");

        loadUserScores(event);

        if (event.isOverallHighscores()) {
            loadHighscores(event);
        } else {
            loadWeekScores(event);
        }
    }

    /**
     * Creates the event to send the retrieved data
     */
    public void onHighscoresLoaded(List<Highscore> highscores, LoadHighscoresEvent event) {
        int userRank = findUserRankInHighscores(highscores);

        mBus.post(new HighscoresLoadedEvent(new Highscores(highscores), userRank, event.isOverallHighscores(), event.getSeasonInfo()));
    }

    /**
     * Loads the users scores from the application data
     */
    public void loadUserScores(LoadHighscoresEvent event) {
        String playerName = mAppData.getUserName();

        int score = 0;
        int maxScore = 0;

        if (event.isOverallHighscores()) {
            score = mAppData.getTotalScore();
            maxScore = mAppData.getTotalGamesPlayed();
        } else {
            score = mAppData.getScoreForWeek(event.getSeasonInfo()).getScore();
            maxScore = mAppData.getGamesCountForWeek(event.getSeasonInfo());
        }

        mBus.post(new UserScoresLoadedEvent(playerName, score, maxScore));
    }

    /**
     * Load the highscores either from the application data
     * or call for a download of the scores
     */
    public void loadHighscores(LoadHighscoresEvent event) {
        if (mAppData.getHighscores().isEmpty()) {
            downloadHighscores(event);
        } else {
            onHighscoresLoaded(mAppData.getHighscores(), event);
        }
    }

    /**
     * Load highscores for a specific week. Checks if available in
     * application data or downloads new scores if necessary
     */
    public void loadWeekScores(LoadHighscoresEvent event) {
        if (mAppData.getHighscoresPerWeek().containsKey(event.getSeasonInfo().getWeek())) {
            onHighscoresLoaded(mAppData.getHighscoresForWeek(event.getSeasonInfo().getWeek()).getSortedHighscores(), event);
        } else {
            downloadWeekScores(event);
        }
    }

    /**
     * Downloads the current highscores from the API and saves them
     * in tha application data before posting the return event
     */
    public void downloadHighscores(final LoadHighscoresEvent event) {
        mApi.getHighscores(event.getToken(), new Callback<Response<Highscores>>() {
            @Override
            public void success(Response<Highscores> highscoresResponse, retrofit.client.Response response) {
                mAppData.setHighscores(highscoresResponse.getData().getSortedHighscores());

                onHighscoresLoaded(highscoresResponse.getData().getSortedHighscores(), event);
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }

    /**
     * Downloads the highscores for a specific week from the API and saves them
     * in tha application data before posting the return event
     */
    public void downloadWeekScores(final LoadHighscoresEvent event) {
        mApi.getScoresForWeek(event.getToken(), event.getSeasonInfo().getSeason(), event.getSeasonInfo().getWeek(), event.getSeasonInfo().getType(), new Callback<Response<Highscores>>() {
            @Override
            public void success(Response<Highscores> highscoresResponse, retrofit.client.Response response) {
                mAppData.addData(event.getSeasonInfo().getWeek(), highscoresResponse.getData());

                onHighscoresLoaded(highscoresResponse.getData().getSortedHighscores(), event);
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }

    /**
     * Finds the users rank in a list of highscores
     */
    private int findUserRankInHighscores(List<Highscore> highscores) {
        for (int i = 0; i < highscores.size(); i++) {
            if (highscores.get(i).getUser().equals(mAppData.getUserName())) {
                return (i + 1);
            }
        }
        return -1;
    }
}
