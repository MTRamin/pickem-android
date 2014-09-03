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

package de.mtrstudios.nflpickem.UI.Games;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Pick;
import de.mtrstudios.nflpickem.API.Data.TeamScore;
import de.mtrstudios.nflpickem.API.Response;
import de.mtrstudios.nflpickem.API.Responses.Games;
import de.mtrstudios.nflpickem.API.Responses.GamesPerWeek;
import de.mtrstudios.nflpickem.API.Responses.Picks;
import de.mtrstudios.nflpickem.API.Responses.Scores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.API.Responses.TeamScores;
import de.mtrstudios.nflpickem.Handlers.ApiHandler;
import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.BaseFragment;
import de.mtrstudios.nflpickem.UI.PlayerStatistics.PlayerStatisticsActivity;
import retrofit.Callback;
import retrofit.RetrofitError;


/**
 * Fragment displaying a week of games and picks
 * Uses a ListView with a custom adapter to display the games
 */
public class GamesFragment extends BaseFragment {

    // Data
    private String playerName;
    private SeasonInfo seasonInfo;
    private int score;

    // true - if the user is allowed to pick in this fragment
    private boolean isPickActivity;

    // UI References
    private SwipeRefreshLayout swipeRefreshLayout;
    private View updateErrorIndicator;
    private ListView listView;
    private ViewGroup rootView;
    private GamesListAdapter adapter;


    // Download counter
    private int finishedDownloads = 0;
    private int updateDownloadCount = 0;

    private PickEmApplication application;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static GamesFragment newInstance() {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create a new Fragment with data
     */
    public static GamesFragment newInstance(String playerName, SeasonInfo seasonInfo, int score) {
        GamesFragment fragment = new GamesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(GamesActivity.EXTRA_PLAYER_NAME, playerName);
        bundle.putParcelable(GamesActivity.EXTRA_SEASON_INFO, seasonInfo);
        bundle.putInt(GamesActivity.EXTRA_WEEK_SCORE, score);
        fragment.setArguments(bundle);
        return fragment;
    }

    public GamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (PickEmApplication) getActivity().getApplication();

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            if (bundle.isEmpty()) {
                this.playerName = mAppData.getUserName();
            } else {
                this.playerName = bundle.getString(GamesActivity.EXTRA_PLAYER_NAME);
                this.seasonInfo = bundle.getParcelable(GamesActivity.EXTRA_SEASON_INFO);
                this.score = bundle.getInt(GamesActivity.EXTRA_WEEK_SCORE);
            }
        }

        // Check if picking should be enabled
        this.isPickActivity = (this.seasonInfo == null) || (this.playerName.equals(mAppData.getUserName()) && this.seasonInfo.equals(mAppData.getSeasonInfo()));
        application.setmPicksEnabled(isPickActivity);

        // Set ActionBar title
        String actionBarString = (isPickActivity) ? getString(R.string.current_week) : getString(R.string.week) + " " + this.seasonInfo.getWeek();
        getActivity().getActionBar().setTitle(actionBarString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_games, container, false);

        // Set up ListView and adapter
        listView = (ListView) rootView.findViewById(R.id.gamesListView);
        adapter = new GamesListAdapter(this.getActivity(), this);
        listView.setAdapter(adapter);
        listView.setVisibility(View.INVISIBLE);

        // Set up player/seasonstatistics
        View seasonInfo = rootView.findViewById(R.id.seasonInfo);
        seasonInfo.setVisibility(View.GONE);
        seasonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchStatsActivity(playerName);
            }
        });

        updateErrorIndicator = rootView.findViewById(R.id.updateError);
        updateErrorIndicator.setVisibility(View.GONE);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDownloadCount = 0;
                checkDataValidity(true);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary_lighter, R.color.secondary_darkest, R.color.secondary_lighter, R.color.secondary_darkest);
        swipeRefreshLayout.setEnabled(isPickActivity);

        scaleIcon((ImageView) rootView.findViewById(R.id.errorIcon));

        // Launch appData loading tasks
        handleOldData();
        checkDataValidity(false);

        return rootView;
    }

    /**
     * Scales and positions the background icon
     */
    private void scaleIcon(ImageView target) {
        // Scale Icon View
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        BitmapDrawable bmap = (BitmapDrawable) this.getResources().getDrawable(R.drawable.loginbackgroundicon);
        float bmapWidth = bmap.getBitmap().getWidth();
        float bmapHeight = bmap.getBitmap().getHeight();

        float wRatio = width / bmapWidth;
        float hRatio = height / bmapHeight;

        float ratioMultiplier = wRatio;
        // Untested conditional though I expect this might work for landscape mode
        if (hRatio < wRatio) {
            ratioMultiplier = hRatio;
        }

        int newBmapWidth = (int) (bmapWidth * ratioMultiplier);
        int newBmapHeight = (int) (bmapHeight * ratioMultiplier);

        target.setLayoutParams(new RelativeLayout.LayoutParams(newBmapWidth, newBmapHeight));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Checks what appData is available and whether it needs to be downloaded again
     * Function is called over and over again to check all appData after one another
     * When all appData is available it calls an update to the UI with finishUpdate()
     */
    private void checkDataValidity(boolean isUpdate) {
        if (isUpdate) { // Call is a forced update by the SwipeRefreshLayout
            Log.i("DataValidity", "It's an update");
            if (updateDownloadCount == 0) {
                Log.i("DataValidity", "Updating SeasonInfo");
                downloadSeasonInfo(true);
                updateDownloadCount++;
            } else if (updateDownloadCount == 1) {
                Log.i("DataValidity", "Updating Data");
                updateData(true);
                updateDownloadCount++;
            } else {
                Log.i("DataValidity", "ALL CLEAR");
                finishUpdate();
            }
        } else if (isPickActivity) { // Called by the regular Fragment displaying appData of the logged in user
            Log.i("DataValidity", "We can pick stuff");
            if (needSeasonInfoUpdate()) {
                Log.i("DataValidity", "Need SeasonInfo Update");
                downloadSeasonInfo(false);
            } else if (needDataUpdate()) {
                Log.i("DataValidity", "Need Data Update");
                updateData(false);
            } else {
                Log.i("DataValidity", "ALL CLEAR");
                finishUpdate();
            }
        } else { // Showing old games/picks
            Log.i("DataValidity", "It's a recap");
            if (finishedDownloads == PickEmApplication.DOWNLOADS_NEEDED_RECAP) {
                Log.i("DataValidity", "Displaying appData");
                finishUpdate();
            } else {
                Log.i("DataValidity", "Getting appData");
                updatePastData();
            }
        }
    }

    /**
     * Checks if player/game appData needs to be updated before populating UI
     */
    public boolean needDataUpdate() {
        return !mAppData.isDataAvailable() || gamesKickedOff() || gamesActive();
    }

    /**
     * Checks if season info needs to be updated before populating UI
     */
    public boolean needSeasonInfoUpdate() {
        return mAppData.getSeasonInfo() == null || mAppData.getLastSeasonUpdate().before(mAppData.getAppStart()) || gamesKickedOff();

    }

    /**
     * Checks if games are currently active
     */
    public boolean gamesActive() {
        List<Game> games = mAppData.getGamesForSeason(mAppData.getSeasonInfo());

        for (Game game : games) {
            if (!game.getQuarter().equals("P") && !game.getQuarter().equals("F") && !game.getQuarter().equals("FOT")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if games have kicked off since the last appData update
     */
    public boolean gamesKickedOff() {
        // If a game has started after the last update we need to update all appData
        List<Game> games = mAppData.getGamesForSeason(mAppData.getSeasonInfo());
        Calendar lastUpdate = mAppData.getLastDataUpdate();

        for (Game game : games) {
            if (game.getKickoffTime().after(lastUpdate) && game.getKickoffTime().before(new GregorianCalendar())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Populates the UI with downloaded appData and makes it visible
     */
    public void populateUI() {
        listView.setVisibility(View.VISIBLE);

        ((TextView) rootView.findViewById(R.id.textUserName)).setText(playerName);
        ((TextView) rootView.findViewById(R.id.textUserScore)).setText(String.valueOf(score));
        ((TextView) rootView.findViewById(R.id.textUserMaxScore)).setText(String.valueOf(mAppData.getGamesCountForWeek(seasonInfo)));

        ((TextView) rootView.findViewById(R.id.textSeason)).setText(String.valueOf(seasonInfo.getSeasonNice()));
        ((TextView) rootView.findViewById(R.id.textWeek)).setText("Week " + String.valueOf(seasonInfo.getWeek()));

        View parent = rootView.findViewById(R.id.seasonInfo);
        animateCurrentWeekIn(parent);

        adapter.notifyDataSetChanged();
    }

    /**
     * Changes visibility of the current week / player information
     */
    public void animateCurrentWeekIn(View target) {
        if (target.getVisibility() == View.GONE) {
            target.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows the error label on the screen and specifies the error that occurred
     */
    public void showUpdateError(boolean isNetworkError) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        application.setmPicksEnabled(false);

        if (adapter.getCount() == 0) {
            rootView.findViewById(R.id.errorBackground).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.errorIcon).setVisibility(View.VISIBLE);
        }

        // Set Error Text according to error type (server/client error)
        TextView reason = (TextView) updateErrorIndicator.findViewById(R.id.textPicksDisabled);
        if (isNetworkError) {
            reason.setText(getResources().getString(R.string.check_connection));
        } else {
            reason.setText(getResources().getString(R.string.server_down));
        }

        if (updateErrorIndicator.getVisibility() == View.GONE) {
            updateErrorIndicator.setVisibility(View.VISIBLE);
        }


    }

    /**
     * Hides the error label from the user, error was resolved
     */
    public void hideUpdateError() {
        application.setmPicksEnabled(true);

        rootView.findViewById(R.id.errorBackground).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.errorIcon).setVisibility(View.INVISIBLE);

        if (updateErrorIndicator.getVisibility() == View.VISIBLE) {
            updateErrorIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Handles appData saved in sharedpreferences from past sessions and displays it to the user
     */
    public void handleOldData() {
        if (isPickActivity && mAppData.isDataAvailable()) {
            Log.i("HandleOldData", "Using old Data");

            this.seasonInfo = mAppData.getSeasonInfo();
            this.playerName = mAppData.getUserName();
            this.score = mAppData.getScoreForWeek(seasonInfo).getScore();

            List<Game> games = mAppData.getGamesForSeason(seasonInfo);
            List<Pick> picks = mAppData.getPicksForGames(games);

            for (Game game : games) {
                adapter.addData(game);
            }

            for (Pick pick : picks) {
                adapter.addData(pick);
            }
            adapter.notifyDataSetChanged();

            populateUI();
        }
    }

    /**
     * Disables the 'refresh' animation and calls to populate the UI
     */
    private void finishUpdate() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            hideUpdateError();
        }

        populateUI();
    }

    /**
     * Calls downloads for old appData/picks and for different users
     */
    public void updatePastData() {
        finishedDownloads = 0;

        downloadGames(seasonInfo, mAppData.getUserToken(), false);
        downloadPicks(seasonInfo, mAppData.getUserToken(), false);

    }

    /**
     * Calls downloads for all appData necessary to populate the UI
     * Resets the download date and download counter and enables the refresh animation
     */
    public void updateData(boolean isForcedUpdate) {

        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        Log.i("UpdateData", "Downloading new Data");

        this.finishedDownloads = 0;
        mAppData.clearCurrentData();
        mAppData.setLastDataUpdate(new GregorianCalendar());

        SeasonInfo current = mAppData.getSeasonInfo();
        String token = mAppData.getUserToken();

        downloadGames(current, token, isForcedUpdate);
        downloadPicks(current, token, isForcedUpdate);
        downloadScores(current, token, isForcedUpdate);
        downloadGamesPerWeek(token, isForcedUpdate);
        downloadTeamScores(token, isForcedUpdate);
    }

    /**
     * Checks if all downloads have finished and calls the checkDataValidity loop again if true
     */
    public void checkDownloadsFinished(boolean isForcedUpdate) {

        Log.i("DownloadsFinished", String.valueOf(this.finishedDownloads));
        if ((this.finishedDownloads == PickEmApplication.DOWNLOADS_NEEDED_LIVE) || (!isPickActivity && (this.finishedDownloads == PickEmApplication.DOWNLOADS_NEEDED_RECAP))) {
            Log.i("DownloadsFinished", "Continuing!");
            checkDataValidity(isForcedUpdate);
        }
    }

    /**
     * Saves picks if they downloaded and from the logged in user
     * Adds picks to the ListViewAdapter
     */
    public void handlePicks(List<Pick> picks, boolean isNewData) {
        for (Pick pick : picks) {
            if (isNewData && isPickActivity) {
                mAppData.addData(pick);
            }
            adapter.addData(pick);
        }

        if (isNewData && isPickActivity) {
            mAppData.savePicks();
        }
    }

    /**
     * Saves games if they downloaded and from the logged in user
     * Adds games to the ListViewAdapter
     */
    public void handleGames(List<Game> games, boolean isNewData) {
        for (Game game : games) {
            if (isNewData && isPickActivity) {
                mAppData.addData(game);
            }
            adapter.addData(game);
        }

        if (isNewData && isPickActivity) {
            mAppData.saveGames();
        }
    }

    /**
     * Saves teamscores in the appData stores and shared preferences
     */
    public void handleTeamScores(Map<String, TeamScore> teamScores) {
        mAppData.setTeamScores(teamScores);
    }

    /**
     * Checks if the seasonInfo has changed after an update
     * Removes some appData if seasonInfo has changed
     */
    private void checkSeasonInfoChanged(SeasonInfo current, SeasonInfo old) {
        if ((old == null) || (!current.equals(old))) {
            mAppData.seasonChanged();

            if (old != null) {
                adapter.resetData();
            }
        }
    }

    /**
     * Starts the User Statistics Activity with the users username
     */
    private void launchStatsActivity(String userName) {
        Intent intent = new Intent(this.getActivity(), PlayerStatisticsActivity.class);
        intent.putExtra(PlayerStatisticsActivity.EXTRA_USER_NAME, userName);
        startActivity(intent);
    }


    /**
     * Downloads seasonInfo and saves it's appData on successful download
     * Shows error message if download failed
     */
    private void downloadSeasonInfo(final boolean isForcedUpdate) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        ApiHandler.getInstance().getApi().getSeasonInfo(mAppData.getUserToken(), new Callback<Response<SeasonInfo>>() {
            @Override
            public void success(Response<SeasonInfo> seasonInfoResponse, retrofit.client.Response response) {
                SeasonInfo old = mAppData.getSeasonInfo();

                mAppData.setSeasonInfo(seasonInfoResponse.getData());
                mAppData.setLastSeasonUpdate(new GregorianCalendar());

                seasonInfo = seasonInfoResponse.getData();

                checkSeasonInfoChanged(seasonInfoResponse.getData(), old);
                checkDataValidity(isForcedUpdate);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit", "ERROR");
                Log.i("Error:", error.toString());

                showUpdateError(error.isNetworkError());
            }
        });
    }

    /**
     * Downloads games and saves it's appData on successful download
     * Shows error message if download failed
     */
    public void downloadGames(SeasonInfo current, String token, final boolean isForcedUpdate) {

        ApiHandler.getInstance().getApi().getGames(current.getSeason(), current.getWeek(), current.getType(), token, new Callback<Response<Games>>() {
            @Override
            public void success(Response<Games> gamesResponse, retrofit.client.Response response) {
                Log.i("GamesFragment", "Got Games");

                handleGames(gamesResponse.getData().getSortedGames(), true);

                finishedDownloads++;
                checkDownloadsFinished(isForcedUpdate);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("GamesFragment", "Error getting Games");
                Log.i("GamesFragment", error.toString());

                showUpdateError(error.isNetworkError());
            }
        });
    }

    /**
     * Downloads picks and saves it's appData on successful download
     * Shows error message if download failed
     */
    public void downloadPicks(SeasonInfo current, String token, final boolean isForcedUpdate) {

        ApiHandler.getInstance().getApi().getPicks(this.playerName, current.getSeason(), current.getWeek(), current.getType(), token, new Callback<Response<Picks>>() {
            @Override
            public void success(Response<Picks> pickResponse, retrofit.client.Response response) {
                Log.i("GamesFragment", "Got Picks");

                handlePicks(pickResponse.getData().getPicks(), true);
                finishedDownloads++;


                checkDownloadsFinished(isForcedUpdate);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("GamesFragment", "Error with Picks");
                Log.e("ERROR", error.toString());

                showUpdateError(error.isNetworkError());
            }
        });
    }

    /**
     * Downloads user scores and saves it's appData on successful download
     * Shows error message if download failed
     */
    private void downloadScores(final SeasonInfo current, String token, final boolean isForcedUpdate) {

        ApiHandler.getInstance().getApi().getScoreForUser(mAppData.getUserName(), current.getSeason(), current.getType(), token, new Callback<Response<Scores>>() {
            @Override
            public void success(Response<Scores> scoresResponse, retrofit.client.Response response) {
                Log.i("Retrofit", "Received scores successfully");
                mAppData.setScoresByWeek(scoresResponse.getData().getScoresAsMap());
                score = mAppData.getScoreForWeek(seasonInfo).getScore();
                finishedDownloads++;

                checkDownloadsFinished(isForcedUpdate);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("GamesFragment", "Error getting Scores");
                Log.i("GamesFragment", error.toString());

                showUpdateError(error.isNetworkError());
            }
        });
    }

    /**
     * Downloads games per week and saves it's appData on successful download
     * Shows error message if download failed
     */
    private void downloadGamesPerWeek(String token, final boolean isForcedUpdate) {
        ApiHandler.getInstance().getApi().getGamesPerWeek(token, new Callback<Response<GamesPerWeek>>() {
            @Override
            public void success(Response<GamesPerWeek> gamesPerWeekResponse, retrofit.client.Response response) {
                Log.i("Retrofit", "Got GamesPerWeek");

                if (gamesPerWeekResponse.getData().getGamesPerWeek() != null) {
                    mAppData.setGamesPerWeek(gamesPerWeekResponse.getData().getGamesPerWeekAsMap());
                    mAppData.saveGamesPerWeek();
                }

                finishedDownloads++;
                checkDownloadsFinished(isForcedUpdate);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit", "error");
                Log.i("Error", error.toString());

                showUpdateError(error.isNetworkError());
            }
        });
    }

    /**
     * Downloads the teams scores and saves it's appData on successful download
     * Shows error message if download failed
     */
    private void downloadTeamScores(String token, final boolean isForcedUpdate) {
        ApiHandler.getInstance().getApi().getTeamScores(token, new Callback<Response<TeamScores>>() {
            @Override
            public void success(Response<TeamScores> teamScoresResponse, retrofit.client.Response response) {
                Log.i("Retrofit", "Got TeamScores");

                handleTeamScores(teamScoresResponse.getData().getTeamScoresAsMap());

                finishedDownloads++;
                checkDownloadsFinished(isForcedUpdate);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit", "error");
                Log.i("Error", error.toString());

                showUpdateError(error.isNetworkError());
            }
        });
    }

    /**
     * Submits a pick to the server. If submitting was successful alters the appData accordingly
     * Shows error message if download failed
     */
    public void submitPick(final String pick, final String gamekey, final GamesListAdapter.GameViewHolder viewHolder) {

        ApiHandler.getInstance().getApi().pickGame(gamekey, pick, mAppData.getUserToken(), new Callback<Response<Pick>>() {
            @Override
            public void success(Response<Pick> pickResponse, retrofit.client.Response response) {
                Log.i("Retrofit", "Pick submitted successfully");

                Pick newPick = new Pick(gamekey, pick);

                mAppData.addData(newPick);
                mAppData.savePicks();
                adapter.addData(newPick);
                adapter.animateChangedPick(newPick, viewHolder);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit", "Error submitting pick");

                showUpdateError(error.isNetworkError());
            }
        });
    }
}
