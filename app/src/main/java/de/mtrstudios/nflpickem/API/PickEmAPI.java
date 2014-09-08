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

package de.mtrstudios.nflpickem.API;

import de.mtrstudios.nflpickem.API.Data.Pick;
import de.mtrstudios.nflpickem.API.Data.Team;
import de.mtrstudios.nflpickem.API.Responses.Games;
import de.mtrstudios.nflpickem.API.Responses.GamesPerWeek;
import de.mtrstudios.nflpickem.API.Responses.Highscores;
import de.mtrstudios.nflpickem.API.Responses.Scores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.API.Responses.Token;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * API Interface for Retrofit
 * This is used to execute all the API calls
 */
public interface PickEmAPI {

    //  --------------------------------------
    //  Login and Signup
    //  --------------------------------------
    @FormUrlEncoded
    @POST("/api/user/register")
    public void register(@Field("email") String email, @Field("password") String password, Callback<Response<Token>> callback);

    @FormUrlEncoded
    @POST("/api/user/login")
    public void login(@Field("email") String email, @Field("password") String password, Callback<Response<Token>> callback);

    //  --------------------------------------
    //  Games
    //  --------------------------------------
    @FormUrlEncoded
    @POST("/api/games/user")
    public void getGames(@Field("user") String playerName, @Field("season") int season, @Field("week") int week, @Field("type") String type, @Field("token") String token, Callback<Response<Games>> callback);

    @FormUrlEncoded
    @POST("/api/games/seasoninfo")
    public void getSeasonInfo(@Field("token") String token, Callback<Response<SeasonInfo>> callback);

    @FormUrlEncoded
    @POST("/api/games/perweek")
    public void getGamesPerWeek(@Field("token") String token, Callback<Response<GamesPerWeek>> callback);

    //  --------------------------------------
    //  Picks
    //  --------------------------------------
    @FormUrlEncoded
    @POST("/api/picks/pick")
    public void pickGame(@Field("game") String gamekey, @Field("pick") Team pick, @Field("token") String token, Callback<Response<Pick>> callback);

    //  --------------------------------------
    //  Scores
    //  --------------------------------------
    @FormUrlEncoded
    @POST("/api/scores/user")
    public void getScoreForUser(@Field("user") String userName, @Field("season") int season, @Field("type") String type, @Field("token") String token, Callback<Response<Scores>> callback);

    @FormUrlEncoded
    @POST("/api/scores/highscores")
    public void getHighscores(@Field("token") String token, Callback<Response<Highscores>> callback);

    @FormUrlEncoded
    @POST("/api/scores/week")
    public void getScoresForWeek(@Field("token") String token, @Field("season") int season, @Field("week") int week, @Field("type") String type, Callback<Response<Highscores>> callback);

}
