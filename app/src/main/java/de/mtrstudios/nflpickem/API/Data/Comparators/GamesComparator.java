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

package de.mtrstudios.nflpickem.API.Data.Comparators;

import java.util.Calendar;
import java.util.Comparator;

import de.mtrstudios.nflpickem.API.Data.Game;

/**
 * Comparator to sort Games
 */
public class GamesComparator implements Comparator<Game> {

    /**
     * Sorts two games according to their kickoff time
     * If two games kick off at the same time their gamekey is used
     */
    @Override
    public int compare(Game game, Game game2) {
        Calendar gameKickoff = game.getKickoffTime();
        Calendar game2Kickoff = game2.getKickoffTime();

        if (gameKickoff.before(game2Kickoff)) {
            return -1;
        } else if (gameKickoff.after(game2Kickoff)) {
            return 1;
        } else {
            int key1 = Integer.parseInt(game.getGamekey());
            int key2 = Integer.parseInt(game2.getGamekey());

            return key1 < key2 ? -1 : 1;
        }
    }

}
