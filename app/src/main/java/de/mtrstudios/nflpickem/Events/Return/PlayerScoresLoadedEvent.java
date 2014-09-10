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

import java.util.Map;

import de.mtrstudios.nflpickem.API.Responses.Scores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;

/**
 * Event sending Player Scores over event bus
 */
public class PlayerScoresLoadedEvent {
    private final Scores scores;
    private final String playerName;
    private final int totalGamesPlayed;
    private final SeasonInfo seasonInfo;
    private final Map<Integer, Integer> gamesPerWeek;

    public PlayerScoresLoadedEvent(int totalGamesPlayed, String playerName, Scores scores, SeasonInfo seasonInfo, Map<Integer, Integer> gamesPerWeek) {
        this.totalGamesPlayed = totalGamesPlayed;
        this.playerName = playerName;
        this.scores = scores;
        this.seasonInfo = seasonInfo;
        this.gamesPerWeek = gamesPerWeek;
    }

    public Map<Integer, Integer> getGamesPerWeek() {
        return gamesPerWeek;
    }

    public Scores getScores() {
        return scores;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }
}
