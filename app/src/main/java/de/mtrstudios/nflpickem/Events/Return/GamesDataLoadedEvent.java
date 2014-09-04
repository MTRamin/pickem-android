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

/**
 * Event to transmit data abotu games data and picks
 */
public class GamesDataLoadedEvent {
    private final List<Game> games;
    private final boolean pickingEnabled;

    public GamesDataLoadedEvent(List<Game> games, boolean pickingEnabled) {
        this.pickingEnabled = pickingEnabled;
        this.games = games;
    }

    public List<Game> getGames() {
        return games;
    }

    public boolean isPickingEnabled() {
        return pickingEnabled;
    }
}
