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

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Team;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.Events.Error.ApiErrorEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.LoadGamesDataEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.SendPickEvent;
import de.mtrstudios.nflpickem.Events.Return.GamesDataLoadedEvent;
import de.mtrstudios.nflpickem.Events.Return.PickSentEvent;
import de.mtrstudios.nflpickem.Events.Return.SeasonStatisticsLoadedEvent;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.BaseFragment;
import de.mtrstudios.nflpickem.UI.PlayerStatistics.PlayerStatisticsActivity;


/**
 * Fragment displaying a week of games and picks
 * Uses a ListView with a custom adapter to display the games
 */
public class GamesFragment extends BaseFragment {

    // Data
    private String mPlayerName;
    private SeasonInfo mSeasonInfo;
    private int mScore;

    // UI References
    @InjectView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.updateError) View updateErrorIndicator;
    @InjectView(R.id.seasonInfo) View seasonInfoView;
    @InjectView(R.id.gamesListView) ListView listView;
    @InjectView(R.id.errorIcon) ImageView errorIcon;
    @InjectView(R.id.errorBackground) View errorBackground;
    @InjectView(R.id.textPicksDisabled) TextView errorReason;

    @InjectView(R.id.textSeason) TextView textSeason;
    @InjectView(R.id.textWeek) TextView textWeek;
    @InjectView(R.id.textUserScore) TextView textUserScore;
    @InjectView(R.id.textUserMaxScore) TextView textUserMaxScore;
    @InjectView(R.id.textUserName) TextView textUserName;

    private GamesListAdapter mAdapter;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mPlayerName = bundle.getString(GamesActivity.EXTRA_PLAYER_NAME, "");
            mSeasonInfo = bundle.getParcelable(GamesActivity.EXTRA_SEASON_INFO);
            mScore = bundle.getInt(GamesActivity.EXTRA_WEEK_SCORE);
        }

        String title = (mSeasonInfo != null) ? getString(R.string.week) + " " + mSeasonInfo.getWeek() : getString(R.string.current_week);
        getActivity().getActionBar().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        // Inject views
        ButterKnife.inject(this, rootView);

        // Set up ListView and adapter
        mAdapter = new GamesListAdapter(this.getActivity(), this);
        listView.setAdapter(mAdapter);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGamesData();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary_lighter, R.color.secondary_darkest, R.color.secondary_lighter, R.color.secondary_darkest);

        scaleIcon(ButterKnife.findById(rootView, R.id.errorIcon));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getGamesData();
    }

    /**
     * Scales and positions the background icon
     */
    private void scaleIcon(View target) {
        // Scale Icon View
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        BitmapDrawable bmap = (BitmapDrawable) getResources().getDrawable(R.drawable.loginbackgroundicon);
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
     * Sends the event to the bus that data was requested
     */
    private void getGamesData() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        mBus.post(new LoadGamesDataEvent(mPlayerName, mSeasonInfo, mScore, false));
    }

    /**
     * Sends the event to the bus that data was requested
     */
    private void refreshGamesData() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        mBus.post(new LoadGamesDataEvent(mPlayerName, mSeasonInfo, mScore, true));
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
    public void showErrorIndicator(boolean isNetworkError) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        if (mAdapter.getCount() == 0) {
            errorBackground.setVisibility(View.VISIBLE);
            errorIcon.setVisibility(View.VISIBLE);
        }
        mAdapter.setPickingEnabled(false);

        // Set Error Text according to error type (server/client error)
        if (isNetworkError) {
            errorReason.setText(getResources().getString(R.string.check_connection));
        } else {
            errorReason.setText(getResources().getString(R.string.server_down));
        }

        if (updateErrorIndicator.getVisibility() == View.GONE) {
            updateErrorIndicator.setVisibility(View.VISIBLE);
        }


    }

    /**
     * Hides the error label from the user, error was resolved
     */
    public void hideErrorIndicator() {
        errorBackground.setVisibility(View.INVISIBLE);
        errorIcon.setVisibility(View.INVISIBLE);

        if (updateErrorIndicator.getVisibility() == View.VISIBLE) {
            updateErrorIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * Starts the User Statistics Activity with the users username
     */
    public void launchStatsActivity(String userName) {
        Intent intent = new Intent(getActivity(), PlayerStatisticsActivity.class);
        intent.putExtra(PlayerStatisticsActivity.EXTRA_USER_NAME, userName);
        startActivity(intent);
    }

    /**
     * Submits a pick to the server. If submitting was successful alters the appData accordingly
     * Shows error message if download failed
     */
    public void submitPick(final Team pick, final Game game, final GamesListAdapter.GameViewHolder viewHolder) {
        mBus.post(new SendPickEvent(pick, game, viewHolder));
    }

    /**
     * Receives and handles the successful pick
     */
    @Subscribe
    public void onPickSent(PickSentEvent event) {
        mAdapter.addGames(event.getGames());
        mAdapter.animateChangedPick(event.getPick(), event.getViewHolder());
    }

    /**
     * Receives and handles an API error event
     * Shows the Error indicator label in response to the error
     */
    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        Log.i("GamesFragment", "Error getting Highscores");
        Log.i("GamesFragment", event.getError().toString());

        showErrorIndicator(event.getError().isNetworkError());
    }

    /**
     * Receives and handles the season info data
     * Shows the user statistics
     */
    @Subscribe
    public void onSeasonStatisticsLoaded(final SeasonStatisticsLoadedEvent event) {

        textSeason.setText(String.valueOf(event.getSeasonInfo().getSeasonNice()));
        textWeek.setText(getString(R.string.week) + " " + String.valueOf(event.getSeasonInfo().getWeek()));
        textUserScore.setText(String.valueOf(event.getScore()));
        textUserMaxScore.setText(String.valueOf(event.getMaxScore()));
        textUserName.setText(event.getPlayerName());

        seasonInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchStatsActivity(event.getPlayerName());
            }
        });

        animateCurrentWeekIn(seasonInfoView);
    }

    /**
     * Receives data for the listview about the games and picks
     * Updates the adapter accordingly
     */
    @Subscribe
    public void onGamesDataLoaded(GamesDataLoadedEvent event) {
        hideErrorIndicator();
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(event.isPickingEnabled());

        mAdapter.addGames(event.getGames());
        mAdapter.setPickingEnabled(event.isPickingEnabled());
        mAdapter.notifyDataSetChanged();
    }

}
