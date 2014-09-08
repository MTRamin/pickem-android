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
package de.mtrstudios.nflpickem.Events.Outgoing;

import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Team;
import de.mtrstudios.nflpickem.UI.Games.GamesListAdapter;

/**
 * Event to send a pick to the api
 */
public class SendPickEvent extends LoadEvent {
    private final Team pick;
    private final Game game;
    private final GamesListAdapter.GameViewHolder viewHolder;

    public SendPickEvent(Team pick, Game game, GamesListAdapter.GameViewHolder viewHolder) {
        this.pick = pick;
        this.game = game;
        this.viewHolder = viewHolder;
    }

    public Team getPick() {
        return pick;
    }

    public Game getGame() {
        return game;
    }

    public GamesListAdapter.GameViewHolder getViewHolder() {
        return viewHolder;
    }
}
