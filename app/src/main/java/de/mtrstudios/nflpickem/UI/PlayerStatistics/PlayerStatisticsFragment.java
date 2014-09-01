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

package de.mtrstudios.nflpickem.UI.PlayerStatistics;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;

import de.mtrstudios.nflpickem.API.Data.Score;
import de.mtrstudios.nflpickem.API.Response;
import de.mtrstudios.nflpickem.API.Responses.Scores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Fragment showing detailed player statistics using a ListView with a custom adapter
 * Also handles the downloads necessary to display those statistics
 */
public class PlayerStatisticsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private PickEmApplication application;

    private PlayerStatisticsListAdapter adapter;

    private String playerName = "loading...";
    private Map<Integer, Score> scores;

    private TextView viewUsername;
    private TextView viewScore;
    private TextView viewMaxScore;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlayerStatisticsFragment.
     */
    public static PlayerStatisticsFragment newInstance(String userName) {
        PlayerStatisticsFragment fragment = new PlayerStatisticsFragment();

        // Set bundle with username
        Bundle bundle = new Bundle();
        bundle.putString(PlayerStatisticsActivity.EXTRA_USER_NAME, userName);
        fragment.setArguments(bundle);

        return fragment;
    }

    public PlayerStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.playerName = bundle.getString(PlayerStatisticsActivity.EXTRA_USER_NAME);
        }

        this.application = ((PickEmApplication) getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_player_statistics, container, false);

        // Set up ListView and its Adapter with the correct data
        ListView listView = (ListView) rootView.findViewById(R.id.statsListView);
        adapter = new PlayerStatisticsListAdapter((PlayerStatisticsActivity) this.getActivity(), application, playerName);
        listView.setAdapter(adapter);

        viewUsername = (TextView) rootView.findViewById(R.id.username);
        viewScore = (TextView) rootView.findViewById(R.id.userScore);
        viewMaxScore = (TextView) rootView.findViewById(R.id.possibleMaxScore);

        getUserScoresData();

        return rootView;
    }

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
     * Calculates the total score of the provided score data
     */
    private int getTotalScore() {
        int score = 0;

        if (this.scores.size() > 0) {
            for (Integer key : this.scores.keySet()) {
                score += this.scores.get(key).getScore();
            }
        }
        return score;
    }

    /**
     * Adds data to the UI and shows it to the user
     */
    private void applyChangesToUI() {

        // Change ActionBar title to the userName
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(this.playerName);
        }

        viewUsername.setText(this.playerName);
        viewScore.setText(String.valueOf(getTotalScore()));
        viewMaxScore.setText(String.valueOf(application.getTotalGamesPlayed()));
    }

    /**
     * Handles the scores and adds it to the listAdapter
     */
    private void handleScores() {
        for (int key : this.scores.keySet()) {
            adapter.addData(this.scores.get(key));
        }
        adapter.notifyDataSetChanged();

        applyChangesToUI();
    }

    /**
     * Gets the scores data that should be displayed,
     * chooses which scores data should be used or downloaded
     */
    private void getUserScoresData() {

        if (playerName.equals(application.getUserName())) {
            Log.i("PlayerStatistics", "Default user selected");
            this.scores = application.getScoresByWeek();
            handleScores();
        } else {
            Log.i("PlayerStatistics", "Downloading new user scores");
            downloadScores(playerName);
        }
    }

    /**
     * Downloads new scores data and handles it accordingly
     */
    private void downloadScores(String playerName) {
        SeasonInfo current = application.getSeasonInfo();
        application.getApi().getScoreForUser(playerName, current.getSeason(), current.getType(), application.getUserToken(), new Callback<Response<Scores>>() {
            @Override
            public void success(Response<Scores> scoresResponse, retrofit.client.Response response) {
                Log.i("Retrofit", "Received scores successfully");

                scores = scoresResponse.getData().getScoresAsMap();
                handleScores();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("GamesFragment", "Error with Picks");
                Log.e("ERROR", error.toString());
            }
        });
    }

}
