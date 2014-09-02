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

package de.mtrstudios.nflpickem.API.Responses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mtrstudios.nflpickem.API.Data.GamesInWeek;

/**
 * Stores appData about games in a week - received as a response from the server
 */
public class GamesPerWeek {

    private List<GamesInWeek> gamesInWeek;

    public List<GamesInWeek> getGamesPerWeek() {
        return gamesInWeek;
    }

    /**
     * Returns the appData as a Map
     * The week number acts as the key and references to the number of games played in that week
     */
    public Map<Integer, Integer> getGamesPerWeekAsMap() {
        Map<Integer, Integer> results = new HashMap<Integer, Integer>();

        for (GamesInWeek week : gamesInWeek) {
            results.put(week.getWeek(), week.getGames());
        }

        return results;
    }
}
