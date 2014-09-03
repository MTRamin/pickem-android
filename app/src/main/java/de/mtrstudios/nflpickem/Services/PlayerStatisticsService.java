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

import android.database.DatabaseErrorHandler;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mtrstudios.nflpickem.API.Data.Score;
import de.mtrstudios.nflpickem.API.PickEmAPI;
import de.mtrstudios.nflpickem.API.Response;
import de.mtrstudios.nflpickem.API.Responses.Scores;
import de.mtrstudios.nflpickem.Events.Error.ApiErrorEvent;
import de.mtrstudios.nflpickem.Events.Load.LoadPlayerScoresEvent;
import de.mtrstudios.nflpickem.Events.Loaded.PlayerScoresLoadedEvent;
import de.mtrstudios.nflpickem.Handlers.PickEmDataHandler;
import retrofit.RetrofitError;

/**
 * Service to load player statistics
 */
public class PlayerStatisticsService extends LoaderService {

    public PlayerStatisticsService(PickEmAPI api, Bus bus) {
        super(api, bus);
    }

    /**
     * Receives the event to download player statistics
     * Determines which players statistics will be shown
     * and gets them from the app data if possible.
     * Otherwise downloads statistics from the API.
     */
    @Subscribe
    public void onLoadPlayerStatistics(final LoadPlayerScoresEvent event) {
        Log.i("PlayerStatisticsService", "Found event!");

        if (event.getPlayerName().equals(mAppData.getUserName())) {
            // Statistics for logged in user
            List<Score> scores = new ArrayList<Score>();
            Map<Integer, Score> savedScores = mAppData.getScoresByWeek();

            for (Integer week : savedScores.keySet()) {
                scores.add(savedScores.get(week));
            }

            mBus.post(new PlayerScoresLoadedEvent(mAppData.getTotalGamesPlayed(), event.getPlayerName(), new Scores(scores), PickEmDataHandler.getInstance().getSeasonInfo()));

        } else {
            // Statistics for another user
            mApi.getScoreForUser(event.getPlayerName(), mAppData.getSeasonInfo().getSeason(), mAppData.getSeasonInfo().getType(), event.getToken(), new retrofit.Callback<Response<Scores>>() {
                @Override
                public void success(Response<Scores> scoresResponse, retrofit.client.Response response) {
                    mBus.post(new PlayerScoresLoadedEvent(mAppData.getTotalGamesPlayed(), event.getPlayerName(), scoresResponse.getData(), PickEmDataHandler.getInstance().getSeasonInfo()));
                }

                @Override
                public void failure(RetrofitError error) {
                    mBus.post(new ApiErrorEvent(error));
                }
            });

        }

    }

}
