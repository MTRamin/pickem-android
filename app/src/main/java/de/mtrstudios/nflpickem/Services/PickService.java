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

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import de.mtrstudios.nflpickem.API.Data.Comparators.GamesComparator;
import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Pick;
import de.mtrstudios.nflpickem.API.PickEmAPI;
import de.mtrstudios.nflpickem.API.Response;
import de.mtrstudios.nflpickem.Events.Error.ApiErrorEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.SendPickEvent;
import de.mtrstudios.nflpickem.Events.Return.PickSentEvent;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Service to send picks to the API
 */
public class PickService extends LoaderService {

    public PickService(PickEmAPI api, Bus bus) {
        super(api, bus);
    }

    @Subscribe
    public void onSendPick(final SendPickEvent event) {
        mApi.pickGame(event.getGame().getGamekey(), event.getPick(), event.getToken(), new Callback<Response<Pick>>() {
            @Override
            public void success(Response<Pick> pickResponse, retrofit.client.Response response) {

                onPickSent(event);
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }

    private void onPickSent(SendPickEvent event) {

        List<Game> games = mAppData.getGames();
        Game game = games.get(games.indexOf(event.getGame()));
        games.remove(games.indexOf(event.getGame()));
        game.setPick(event.getPick());
        games.add(game);

        Collections.sort(games, new GamesComparator());

        mAppData.setGames(games);
        mAppData.saveGames();


        mBus.post(new PickSentEvent(event.getViewHolder(), event.getPick(), games));
    }

}
