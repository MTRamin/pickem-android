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

import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.Events.Error.ApiErrorEvent;
import de.mtrstudios.nflpickem.Events.Return.GamesDataLoadedEvent;
import de.mtrstudios.nflpickem.Events.Return.PickSentEvent;
import de.mtrstudios.nflpickem.Events.Return.SeasonStatisticsLoadedEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.LoadGamesDataEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.SendPickEvent;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.BaseFragment;
import de.mtrstudios.nflpickem.UI.PlayerStatistics.PlayerStatisticsActivity;


/**
 * Fragment displaying a week of games and picks
 * Uses a ListView with a custom adapter to display the games
 */
public class GamesFragment extends BaseFragment {

    // Data
    private String playerName;
    private SeasonInfo seasonInfo;
    private int score;

    // UI References
    private SwipeRefreshLayout swipeRefreshLayout;
    private View updateErrorIndicator;
    private View seasonInfoView;
    private ListView listView;
    private ViewGroup rootView;
    private GamesListAdapter adapter;

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
            this.playerName = bundle.getString(GamesActivity.EXTRA_PLAYER_NAME, "");
            this.seasonInfo = bundle.getParcelable(GamesActivity.EXTRA_SEASON_INFO);
            this.score = bundle.getInt(GamesActivity.EXTRA_WEEK_SCORE);
        }

        String title = (this.seasonInfo != null) ? R.string.week + " " + this.seasonInfo.getWeek() : getString(R.string.current_week);
        getActivity().getActionBar().setTitle(title);
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

        // Set up player/seasons tatistics
        seasonInfoView = rootView.findViewById(R.id.seasonInfo);
        seasonInfoView.setVisibility(View.GONE);

        updateErrorIndicator = rootView.findViewById(R.id.updateError);
        updateErrorIndicator.setVisibility(View.GONE);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGamesData();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary_lighter, R.color.secondary_darkest, R.color.secondary_lighter, R.color.secondary_darkest);

        scaleIcon((ImageView) rootView.findViewById(R.id.errorIcon));

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

        mBus.post(new LoadGamesDataEvent(this.playerName, this.seasonInfo, this.score, false));
    }

    /**
     * Sends the event to the bus that data was requested
     */
    private void refreshGamesData() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        mBus.post(new LoadGamesDataEvent(this.playerName, this.seasonInfo, this.score, true));
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

        if (adapter.getCount() == 0) {
            rootView.findViewById(R.id.errorBackground).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.errorIcon).setVisibility(View.VISIBLE);
        }
        adapter.setPickingEnabled(false);

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
    public void hideErrorIndicator() {
        rootView.findViewById(R.id.errorBackground).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.errorIcon).setVisibility(View.INVISIBLE);

        if (updateErrorIndicator.getVisibility() == View.VISIBLE) {
            updateErrorIndicator.setVisibility(View.GONE);
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
     * Submits a pick to the server. If submitting was successful alters the appData accordingly
     * Shows error message if download failed
     */
    public void submitPick(final String pick, final Game game, final GamesListAdapter.GameViewHolder viewHolder) {
        mBus.post(new SendPickEvent(pick, game, viewHolder));
    }

    /**
     * Receives and handles the successful pick
     */
    @Subscribe
    public void onPickSent(PickSentEvent event) {
        adapter.addGames(event.getGames());
        adapter.animateChangedPick(event.getPick(), event.getViewHolder());
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

        ((TextView) rootView.findViewById(R.id.textSeason)).setText(String.valueOf(event.getSeasonInfo().getSeasonNice()));
        ((TextView) rootView.findViewById(R.id.textWeek)).setText(getString(R.string.week) + " " + String.valueOf(event.getSeasonInfo().getWeek()));
        ((TextView) rootView.findViewById(R.id.textUserScore)).setText(String.valueOf(event.getScore()));
        ((TextView) rootView.findViewById(R.id.textUserMaxScore)).setText(String.valueOf(event.getMaxScore()));
        ((TextView) rootView.findViewById(R.id.textUserName)).setText(event.getPlayerName());
        seasonInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchStatsActivity(event.getPlayerName());
            }
        });

        animateCurrentWeekIn(rootView.findViewById(R.id.seasonInfo));
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

        adapter.addGames(event.getGames());
        adapter.setPickingEnabled(event.isPickingEnabled());
        adapter.notifyDataSetChanged();
    }

}
