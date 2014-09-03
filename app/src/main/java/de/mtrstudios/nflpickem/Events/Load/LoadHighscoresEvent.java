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
package de.mtrstudios.nflpickem.Events.Load;

import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.Handlers.PickEmDataHandler;

/**
 * Event to load Highscores
 */
public class LoadHighscoresEvent extends LoadEvent {

    private final int weekNumber;
    private final SeasonInfo seasonInfo;
    private final boolean overallHighscores;

    public LoadHighscoresEvent(int weekNumber) {
        this.weekNumber = weekNumber;

        this.overallHighscores = (this.weekNumber == 0);
        this.seasonInfo = new SeasonInfo(PickEmDataHandler.getInstance().getSeasonInfo().getSeason(), weekNumber, PickEmDataHandler.getInstance().getSeasonInfo().getType());
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }

    public boolean isOverallHighscores() {
        return overallHighscores;
    }
}
