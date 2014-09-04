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

import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;

/**
 * Event to load data for games Fragment
 */
public class LoadGamesDataEvent extends LoadEvent {
    private final boolean forcedUpdate;
    private final boolean currentWeek;

    private String playerName;
    private SeasonInfo seasonInfo;
    private final int score;

    public LoadGamesDataEvent(String playerName, SeasonInfo seasonInfo, int score, boolean forcedUpdate) {
        this.playerName = playerName;
        this.seasonInfo = seasonInfo;
        this.score = score;
        this.forcedUpdate = forcedUpdate;

        this.currentWeek = (playerName == null || playerName.isEmpty());
    }

    public String getPlayerName() {
        return playerName;
    }

    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }

    public int getScore() {
        return score;
    }

    public boolean isForcedUpdate() {
        return forcedUpdate;
    }

    public boolean isCurrentWeek() {
        return currentWeek;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setSeasonInfo(SeasonInfo seasonInfo) {
        this.seasonInfo = seasonInfo;
    }
}
