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
 * Stores data about a score entry
 */
public class Score {
    private int week;
    private int season;
    private String type;

    private int correct;
    private int wrong;
    private String user;

    public Score(int correct, int wrong) {
        this.correct = correct;
        this.wrong = wrong;
    }

    public int getWeek() {
        return week;
    }

    public int getScore() {
        return correct;
    }

    public int getWrong() {
        return wrong;
    }

    public int getSeason() {
        return season;
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }
}
