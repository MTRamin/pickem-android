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
package de.mtrstudios.nflpickem.Events.Return;

import java.util.List;

import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Team;
import de.mtrstudios.nflpickem.UI.Games.GamesListAdapter;

/**
 * Event sending successful pick information
 */
public class PickSentEvent {
    private final List<Game> games;
    private final Team pick;
    private final GamesListAdapter.GameViewHolder viewHolder;

    public PickSentEvent(GamesListAdapter.GameViewHolder viewHolder, Team pick, List<Game> games) {
        this.viewHolder = viewHolder;
        this.pick = pick;
        this.games = games;
    }

    public List<Game> getGames() {
        return games;
    }

    public Team getPick() {
        return pick;
    }

    public GamesListAdapter.GameViewHolder getViewHolder() {
        return viewHolder;
    }
}
