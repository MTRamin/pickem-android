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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.API.Data.Comparators.HighscoreComparator;

/**
 * Stores data about highscores - received as a response from the server
 */
public class Highscores {

    private List<Highscore> highscores;

    public Highscores() {
        highscores = new ArrayList<Highscore>();
    }

    public List<Highscore> getHighscores() {
        return highscores;
    }

    /**
     * Sorts the list of highscores and returns it
     */
    public List<Highscore> getSortedHighscores() {
        List<Highscore> sortedHighscores = highscores;

        Collections.sort(sortedHighscores, new HighscoreComparator());

        return sortedHighscores;

    }
}
