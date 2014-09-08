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

package de.mtrstudios.nflpickem.API.Data;

/**
 * Stores appData about how many games are played in a week
 */
public class GamesInWeek {
    private int season;
    private int week;
    private SeasonType type;
    private int games;

    public int getSeason() {
        return season;
    }

    public int getWeek() {
        return week;
    }

    public SeasonType getType() {
        return type;
    }

    public int getGames() {
        return games;
    }
}
