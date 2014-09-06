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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.PickEmAPI;
import de.mtrstudios.nflpickem.API.Response;
import de.mtrstudios.nflpickem.API.Responses.Games;
import de.mtrstudios.nflpickem.API.Responses.GamesPerWeek;
import de.mtrstudios.nflpickem.API.Responses.Scores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.Events.Error.ApiErrorEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.LoadGamesDataEvent;
import de.mtrstudios.nflpickem.Events.Return.GamesDataLoadedEvent;
import de.mtrstudios.nflpickem.Events.Return.SeasonStatisticsLoadedEvent;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Loader service to load all data necessary to display the
 * games fragment
 */
public class GamesDataLoaderService extends LoaderService {

    public GamesDataLoaderService(PickEmAPI api, Bus bus) {
        super(api, bus);
    }

    /**
     * Listens to the event to load the game and general data
     * Initializes the download loop
     */
    @Subscribe
    public void onLoadGames(LoadGamesDataEvent event) {
        Log.i("GamesDataService", "Event found!");

        if (event.getPlayerName().isEmpty()) {
            event.setPlayerName(mAppData.getUserName());
        }

        // Set up Response event and slowly fill it with data when available
        SeasonStatisticsLoadedEvent response = new SeasonStatisticsLoadedEvent(event.getPlayerName());

        // Clear data on forced update - that way all downloads will trigger
        if (event.isForcedUpdate()) {
            mAppData.clearDataForRefresh();
        }

        if (needSeasonInfoUpdate() || event.isForcedUpdate()) {
            downloadSeasonInfo(event, response);
        } else {
            onSeasonInfoLoaded(mAppData.getSeasonInfo(), event, response);
        }
    }

    /**
     * Checks if season info needs to be updated before populating UI
     */
    public boolean needSeasonInfoUpdate() {
        return mAppData.getSeasonInfo() == null || mAppData.getLastSeasonUpdate().before(mAppData.getAppStart()) || gamesKickedOff();

    }

    /**
     * Checks if player/game appData needs to be updated before populating UI
     */
    private boolean needDataUpdate() {
        return !mAppData.isDataAvailable() || gamesKickedOff() || gamesActive();
    }

    /**
     * Checks if games are currently active
     */
    private boolean gamesActive() {
        List<Game> games = mAppData.getGamesForSeason(mAppData.getSeasonInfo());

        for (Game game : games) {
            if (!game.getQuarter().equals("P") && !game.getQuarter().equals("F") && !game.getQuarter().equals("FOT")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if games have kicked off since the last appData update
     */
    public boolean gamesKickedOff() {
        // If a game has started after the last update we need to update all appData
        List<Game> games = mAppData.getGamesForSeason(mAppData.getSeasonInfo());
        Calendar lastUpdate = mAppData.getLastDataUpdate();

        for (Game game : games) {
            if (game.getKickoffTime().after(lastUpdate) && game.getKickoffTime().before(new GregorianCalendar())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Posts an event to return the loaded season info to the fragment
     */
    public void onSeasonInfoLoaded(SeasonInfo seasonInfo, LoadGamesDataEvent event, SeasonStatisticsLoadedEvent response) {
        event.setSeasonInfo(seasonInfo);
        response.setSeasonInfo(seasonInfo);

        loadScores(event, response);
        loadGamesData(event);
    }

    /**
     * Handles the scores data after it was loaded
     * Triggers loading of the next data type
     */
    private void onScoresLoaded(int score, LoadGamesDataEvent event, SeasonStatisticsLoadedEvent response) {
        response.setScore(score);

        loadGamesPerWeek(event, response);
    }

    /**
     * Handles the season statistics data after it was loaded
     * Returns the statistics via the bus
     */
    private void onSeasonStatisticsLoaded(int maxScore, LoadGamesDataEvent event, SeasonStatisticsLoadedEvent response) {
        response.setMaxScore(maxScore);

        mBus.post(response);
    }

    /**
     * Handles the games data and returns it via the bus
     */
    private void onGamesDataLoaded(List<Game> games, boolean pickingEnabled) {
        mBus.post(new GamesDataLoadedEvent(games, pickingEnabled));
    }

    /**
     * Determines which data for games per week will be used
     * If no data is available, it will be downloaded from the API
     */
    private void loadGamesPerWeek(LoadGamesDataEvent event, SeasonStatisticsLoadedEvent response) {
        if (mAppData.getGamesPerWeek().size() > 0) {
            onSeasonStatisticsLoaded(mAppData.getGamesCountForWeek(event.getSeasonInfo()), event, response);
        } else {
            downloadGamesPerWeek(event, response);
        }
    }

    /**
     * Determines which data for scores will be used
     * If no data is available, it will be downloaded from the API
     */
    private void loadScores(LoadGamesDataEvent event, SeasonStatisticsLoadedEvent response) {
        if (mAppData.getScoresByWeek().size() > 0) {
            onScoresLoaded(mAppData.getScoreForWeek(event.getSeasonInfo()).getScore(), event, response);
        } else {
            downloadScores(event, response);
        }
    }

    /**
     * Determines which data for the list of games will be used
     * If no data is available, it will be downloaded from the API
     */
    private void loadGamesData(LoadGamesDataEvent event) {
        if (!event.isCurrentWeek() || event.isForcedUpdate() || needDataUpdate()) {
            downloadGamesData(event);
        } else {
            onGamesDataLoaded(mAppData.getGames(), event.isCurrentWeek());
        }
    }

    /**
     * Downloads games per week data from the API
     * Saves the data to the application data store
     */
    private void downloadGamesPerWeek(final LoadGamesDataEvent event, final SeasonStatisticsLoadedEvent serviceResponse) {
        mApi.getGamesPerWeek(event.getToken(), new Callback<Response<GamesPerWeek>>() {
            @Override
            public void success(Response<GamesPerWeek> gamesPerWeekResponse, retrofit.client.Response response) {
                mAppData.setGamesPerWeek(gamesPerWeekResponse.getData().getGamesPerWeekAsMap());

                int maxScore = gamesPerWeekResponse.getData().getGamesPerWeekAsMap().get(event.getSeasonInfo().getWeek());

                onSeasonStatisticsLoaded(maxScore, event, serviceResponse);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     * Downloads score data from the API
     * Saves the data to the application data store
     */
    private void downloadScores(final LoadGamesDataEvent event, final SeasonStatisticsLoadedEvent serviceResponse) {
        mApi.getScoreForUser(event.getPlayerName(), event.getSeasonInfo().getSeason(), event.getSeasonInfo().getType(), event.getToken(), new Callback<Response<Scores>>() {
            @Override
            public void success(Response<Scores> scoresResponse, retrofit.client.Response response) {
                mAppData.setScoresByWeek(scoresResponse.getData().getScoresAsMap());

                int score = scoresResponse.getData().getScoresAsMap().get(event.getSeasonInfo().getWeek()).getScore();

                onScoresLoaded(score, event, serviceResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }

    /**
     * Downloads season info data from the API
     * Saves the data to the application data store
     */
    private void downloadSeasonInfo(final LoadGamesDataEvent event, final SeasonStatisticsLoadedEvent serviceResponse) {
        mApi.getSeasonInfo(event.getToken(), new Callback<Response<SeasonInfo>>() {
            @Override
            public void success(Response<SeasonInfo> seasonInfoResponse, retrofit.client.Response response) {
                SeasonInfo info = seasonInfoResponse.getData();

                mAppData.setSeasonInfo(info);
                mAppData.setLastSeasonUpdate(new GregorianCalendar());

                event.setSeasonInfo(info);

                onSeasonInfoLoaded(info, event, serviceResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }

    /**
     * Downloads games data from the API
     * Saves the data to the application data store
     */
    private void downloadGamesData(final LoadGamesDataEvent event) {
        mApi.getGames(event.getPlayerName(), event.getSeasonInfo().getSeason(), event.getSeasonInfo().getWeek(), event.getSeasonInfo().getType(), event.getToken(), new Callback<Response<Games>>() {
            @Override
            public void success(Response<Games> gamesResponse, retrofit.client.Response response) {
                List<Game> games = gamesResponse.getData().getSortedGames();

                if (event.isCurrentWeek()) {
                    mAppData.setGames(games);
                    mAppData.saveGames();
                    mAppData.setLastDataUpdate(new GregorianCalendar());
                }

                onGamesDataLoaded(games, event.isCurrentWeek());
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }
}
