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

package de.mtrstudios.nflpickem.UI.Highscores;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.Events.Error.ApiErrorEvent;
import de.mtrstudios.nflpickem.Events.Outgoing.LoadHighscoresEvent;
import de.mtrstudios.nflpickem.Events.Return.HighscoresLoadedEvent;
import de.mtrstudios.nflpickem.Events.Return.UserScoresLoadedEvent;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.BaseFragment;
import de.mtrstudios.nflpickem.UI.PlayerStatistics.PlayerStatisticsActivity;

/**
 * Fragment that displays a highscore list.
 * Fragment is always a child of a ViewPager
 */
public class HighscoresFragment extends BaseFragment {

    private static final String BUNDLE_INT = "number";

    private OnFragmentInteractionListener mListener;

    private HighscoresActivity mParent;
    private HighscoresListAdapter mAdapter;

    // Week the fragment displays (0 if overall highscores)
    private int mWeekNumber;

    // Inject all views
    @InjectView(R.id.userstats) View statsView;

    @InjectView(R.id.updateError) View errorView;
    @InjectView(R.id.textPicksDisabled) TextView errorReason;

    @InjectView(R.id.highscoreListView) ListView listView;

    @InjectView(R.id.userRank) TextView viewUserRank;
    @InjectView(R.id.username) TextView viewUserName;
    @InjectView(R.id.userScore) TextView viewUserScore;
    @InjectView(R.id.possibleMaxScore) TextView viewMaxScore;

    @InjectView(R.id.emptyText) TextView backgroundText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HighscoresFragment.
     */
    public static HighscoresFragment newInstance(int number) {
        HighscoresFragment fragment = new HighscoresFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_INT, number);

        fragment.setArguments(args);
        return fragment;
    }

    public HighscoresFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mParent = (HighscoresActivity) getActivity();

        this.mWeekNumber = 0;

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mWeekNumber = bundle.getInt(BUNDLE_INT, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_highscores, container, false);

        // Inject ButterKnife
        ButterKnife.inject(this, rootView);

        // Set up listView and its adapter
        mAdapter = new HighscoresListAdapter(mParent);
        listView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getHighscoreData();
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

    private void getHighscoreData() {
        mBus.post(new LoadHighscoresEvent(mWeekNumber));
    }

    /**
     * Shows an error indicator with a message further explaining the error to the user
     */
    private void showErrorIndicator(boolean isNetworkError) {
        statsView.setVisibility(View.GONE);

        errorView.setVisibility(View.VISIBLE);

        if (isNetworkError) {
            errorReason.setText(getResources().getString(R.string.check_connection));
        } else {
            errorReason.setText(getResources().getString(R.string.server_down));
        }
    }

    /**
     * Hides the error indicator, error was resolved
     */
    private void hideErrorIndicator() {
        statsView.setVisibility(View.VISIBLE);

        errorView.setVisibility(View.GONE);
    }

    /**
     * Receives the loaded user score and populates the UI with it
     */
    @Subscribe
    public void onUserScoresLoaded(final UserScoresLoadedEvent event) {

        viewUserName.setText(event.getPlayerName());
        viewUserScore.setText(String.valueOf(event.getScore()));
        viewMaxScore.setText(String.valueOf(event.getMaxScore()));

        statsView.setVisibility(View.VISIBLE);

        mAdapter.setCurrentPlayer(event.getPlayerName());
        mAdapter.setMaxScore(event.getMaxScore());

        statsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mParent, PlayerStatisticsActivity.class);
                intent.putExtra(PlayerStatisticsActivity.EXTRA_USER_NAME, event.getPlayerName());
                startActivity(intent);
            }
        });
    }

    /**
     * Handle the event and populate the list and other
     * UI components with the received data
     */
    private void handleEvent(HighscoresLoadedEvent event) {
        Log.i("Event Bus", "Found HighscoresLoadedEvent");

        hideErrorIndicator();

        List<Highscore> highscores = event.getHighscores().getSortedHighscores();

        mAdapter.clearData();
        for (Highscore highscore : highscores) {
            mAdapter.addData(highscore);
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.setOverallHighscores(event.isOverallHighscores());
        mAdapter.setSeasonInfo(event.getSeasonInfo());

        if (highscores.size() == 0) {
            backgroundText.setVisibility(View.VISIBLE);
            statsView.setVisibility(View.INVISIBLE);
        } else if (event.getUserRank() == -1) {
            statsView.setVisibility(View.INVISIBLE);
        } else {
            viewUserRank.setText(getString(R.string.rank) + " " + event.getUserRank());
        }

        if (event.isOverallHighscores()) {
            backgroundText.setText(getString(R.string.empty_overall_scores));
        } else {
            backgroundText.setText(getString(R.string.empty_week_scores));
        }
    }

    /**
     * Checks if the received event should be handled by this fragment
     * or if it was destined for another fragment
     */
    private void checkIfEventShouldBeHandled(HighscoresLoadedEvent event) {
        if (event.getSeasonInfo().getWeek() == mWeekNumber) {
            handleEvent(event);
        }
    }

    /**
     * Receives and handles the data to be shown
     */
    @Subscribe
    public void onHighscoresLoaded(HighscoresLoadedEvent event) {
        checkIfEventShouldBeHandled(event);
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
}
