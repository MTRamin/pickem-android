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
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.API.Response;
import de.mtrstudios.nflpickem.API.Responses.Highscores;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.Handlers.ApiHandler;
import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.BaseFragment;
import de.mtrstudios.nflpickem.UI.PlayerStatistics.PlayerStatisticsActivity;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Fragment that displays a highscore list.
 * Fragment is always a child of a ViewPager
 */
public class HighscoresFragment extends BaseFragment {

    private static final String BUNDLE_INT = "number";

    private OnFragmentInteractionListener mListener;

    private HighscoresActivity parent;
    private HighscoresListAdapter adapter;

    private View statsView;
    private View errorView;

    private int weekNumber;
    private SeasonInfo seasonInfo;
    private boolean isOverallHighscores;

    private TextView viewUserRank;
    private TextView viewUserName;
    private TextView viewUserScore;
    private TextView viewMaxScore;

    private TextView backgroundText;

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

        this.parent = (HighscoresActivity) this.getActivity();

        this.isOverallHighscores = true;

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.weekNumber = bundle.getInt(BUNDLE_INT, -1);

            if (weekNumber != -1) {
                this.seasonInfo = new SeasonInfo(appData.getSeasonInfo().getSeason(), weekNumber, appData.getSeasonInfo().getType());
                isOverallHighscores = false;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_highscores, container, false);

        int maxScore = (isOverallHighscores) ? appData.getTotalGamesPlayed() : appData.getGamesCountForWeek(this.seasonInfo);

        // Set up listView and its adapter
        ListView listView = (ListView) rootView.findViewById(R.id.highscoreListView);
        adapter = new HighscoresListAdapter(parent, maxScore, appData.getUserName(), this.seasonInfo, isOverallHighscores);
        listView.setAdapter(adapter);

        this.viewUserRank = (TextView) rootView.findViewById(R.id.userRank);
        this.viewUserName = (TextView) rootView.findViewById(R.id.username);
        this.viewUserScore = (TextView) rootView.findViewById(R.id.userScore);
        this.viewMaxScore = (TextView) rootView.findViewById(R.id.possibleMaxScore);

        this.backgroundText = (TextView) rootView.findViewById(R.id.emptyText);
        if (isOverallHighscores) {
            this.backgroundText.setText(getString(R.string.empty_overall_scores));
        } else {
            this.backgroundText.setText(getString(R.string.empty_week_scores));
        }

        // Set up view to display user appData and statistics. OnClick leads to detailed statistics of that user
        this.statsView = rootView.findViewById(R.id.userstats);
        this.statsView.setVisibility(View.GONE);
        this.statsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent, PlayerStatisticsActivity.class);
                intent.putExtra(PlayerStatisticsActivity.EXTRA_USER_NAME, appData.getUserName());
                startActivity(intent);
            }
        });

        this.errorView = rootView.findViewById(R.id.updateError);
        this.errorView.setVisibility(View.GONE);

        // Download appData to display
        updateData();

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
     * Updates and downloads all appData necessary to populate this fragment with appData
     */
    private void updateData() {

        if (isOverallHighscores) {
            if (!appData.getHighscores().isEmpty()) {
                // Use old highscore appData downloaded in the past

                handleUserScores();
                handleHighscores(appData.getHighscores());
            } else {
                // Download new highscore appData

                handleUserScores();
                downloadHighscores();
            }
        } else {
            if (!appData.getHighscoresPerWeek().containsKey(weekNumber)) {
                // Download new scores appData

                handleUserScores();
                downloadWeekScores();
            } else {
                // User old scores appData downloaded in the past

                handleUserScores();
                handleHighscores(appData.getHighscoresForWeek(weekNumber).getSortedHighscores());
            }
        }
    }

    /**
     * Handles highscores and adds them to the adapter to display
     * Checks if highscores are available and displays a message if no highscores for this week are available
     * While adding the highscores to the adapter, determines the logged in users rank
     */
    private void handleHighscores(List<Highscore> highscores) {
        int userRank = -1;

        for (int i = 0; i < highscores.size(); i++) {
            Highscore highscore = highscores.get(i);

            adapter.addData(highscore);

            if (highscore.getUser().equals(this.appData.getUserName())) {
                userRank = i + 1;
            }
        }
        adapter.notifyDataSetChanged();

        if (highscores.size() == 0) {
            this.backgroundText.setVisibility(View.VISIBLE);
            this.statsView.setVisibility(View.INVISIBLE);
        } else if (userRank == -1) {
            this.statsView.setVisibility(View.INVISIBLE);
        } else {
            this.viewUserRank.setText(getString(R.string.rank) + " " + userRank);
        }
    }

    /**
     * Shows an error indicator with a message further explaining the error to the user
     */
    private void showErrorIndicator(boolean isNetworkError) {
        this.statsView.setVisibility(View.GONE);

        this.errorView.setVisibility(View.VISIBLE);

        TextView reason = (TextView) this.errorView.findViewById(R.id.textPicksDisabled);
        if (isNetworkError) {
            reason.setText(getResources().getString(R.string.check_connection));
        } else {
            reason.setText(getResources().getString(R.string.server_down));
        }
    }

    /**
     * Hides the error indicator, error was resolved
     */
    private void hideErrorIndicator() {
        this.statsView.setVisibility(View.VISIBLE);

        this.errorView.setVisibility(View.GONE);
    }

    /**
     * Handles the scores of the logged in user and sets the appData to the UI
     */
    private void handleUserScores() {
        hideErrorIndicator();

        int score = (isOverallHighscores) ? appData.getTotalScore() : appData.getScoreForWeek(this.seasonInfo).getScore();
        int maxScore = (isOverallHighscores) ? appData.getTotalGamesPlayed() : appData.getGamesCountForWeek(this.seasonInfo);

        viewUserName.setText(appData.getUserName());
        viewUserScore.setText(String.valueOf(score));
        viewMaxScore.setText(String.valueOf(maxScore));

        statsView.setVisibility(View.VISIBLE);
    }

    /**
     * Downloads the highscores for the selected week. Adds the highscores to the application appData
     * Shows an error indicator if the download was unsuccessful
     */
    private void downloadWeekScores() {
        ApiHandler.getInstance().getApi().getScoresForWeek(appData.getUserToken(), this.seasonInfo.getSeason(), this.seasonInfo.getWeek(), this.seasonInfo.getType(), new Callback<Response<Highscores>>() {
            @Override
            public void success(Response<Highscores> highscoresResponse, retrofit.client.Response response) {
                Log.i("Retrofit", "Received scores for week");

                appData.addData(weekNumber, highscoresResponse.getData());
                handleHighscores(highscoresResponse.getData().getSortedHighscores());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("GamesFragment", "Error getting Highscores");
                Log.i("GamesFragment", error.toString());

                showErrorIndicator(error.isNetworkError());
            }
        });
    }

    /**
     * Downloads the overall highscores. Adds the highscores to the application appData
     * Shows an error indicator if the download was unsuccessful
     */
    private void downloadHighscores() {
        ApiHandler.getInstance().getApi().getHighscores(appData.getUserToken(), new Callback<Response<Highscores>>() {
            @Override
            public void success(Response<Highscores> highscoresResponse, retrofit.client.Response response) {
                Log.i("Retrofit", "Received highscores");

                appData.setHighscores(highscoresResponse.getData().getSortedHighscores());
                handleHighscores(appData.getHighscores());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("GamesFragment", "Error getting Highscores");
                Log.i("GamesFragment", error.toString());

                showErrorIndicator(error.isNetworkError());
            }
        });
    }

}
