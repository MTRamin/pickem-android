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

import java.util.Comparator;

import de.mtrstudios.nflpickem.API.Data.Highscore;

/**
 * Comparator to sort Highscores
 */
public class HighscoreComparator implements Comparator<Highscore> {
    @Override
    public int compare(Highscore highscore, Highscore highscore2) {
        return highscore.getCorrect() < highscore2.getCorrect() ? 1 : highscore.getCorrect() == highscore2.getCorrect() ? 0 : -1;
    }
}
