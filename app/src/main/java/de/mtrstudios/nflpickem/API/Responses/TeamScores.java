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

import de.mtrstudios.nflpickem.API.Data.TeamScore;

/**
 * Stores appData about teams scores - received as a response from the server
 */
public class TeamScores {
    private List<TeamScore> teamScores;

    /**
     * Compiles all the teams scores into a map
     * the teams name acts as a key and references to the score of said team
     */
    public Map<String, TeamScore> getTeamScoresAsMap() {
        Map<String, TeamScore> results = new HashMap<String, TeamScore>();

        for (TeamScore score : teamScores) {
            results.put(score.getTeam(), score);
        }

        return results;
    }
}
