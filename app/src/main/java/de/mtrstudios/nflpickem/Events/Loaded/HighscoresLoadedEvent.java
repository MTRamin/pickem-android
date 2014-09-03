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
package de.mtrstudios.nflpickem.Events.Loaded;

import de.mtrstudios.nflpickem.API.Responses.Highscores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;

/**
 * Event for loaded highscores
 */
public class HighscoresLoadedEvent {
    private final Highscores highscores;
    private final int userRank;
    private final boolean overallHighscores;
    private final SeasonInfo seasonInfo;

    public HighscoresLoadedEvent(Highscores highscores, int userRank, boolean overallHighscores, SeasonInfo seasonInfo) {
        this.highscores = highscores;
        this.userRank = userRank;
        this.overallHighscores = overallHighscores;
        this.seasonInfo = seasonInfo;
    }

    public int getUserRank() {
        return userRank;
    }

    public Highscores getHighscores() {
        return highscores;
    }

    public boolean isOverallHighscores() {
        return overallHighscores;
    }

    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }
}
