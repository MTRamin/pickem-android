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

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.Games.GamesActivity;
import de.mtrstudios.nflpickem.UI.PlayerStatistics.PlayerStatisticsActivity;

/**
 * ListViewAdapter that shows appData about highscores in a row.
 */
public class HighscoresListAdapter extends BaseAdapter {

    private HighscoresActivity mParent;

    private List<Highscore> mHighscores = new ArrayList<Highscore>();

    private String mCurrentPlayer;
    private int mMaxScore;
    private SeasonInfo mSeasonInfo;
    private boolean mOverallHighscore;

    public HighscoresListAdapter(HighscoresActivity parent) {
        mParent = parent;
    }

    @Override
    public int getCount() {
        return mHighscores.size();
    }

    @Override
    public Object getItem(int i) {
        return mHighscores.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HighscoreViewHolder viewHolder;

        if (view == null) {
            // Inflate Layout
            LayoutInflater inflater = mParent.getLayoutInflater();
            view = inflater.inflate(R.layout.row_highscore, viewGroup, false);

            // Set up ViewHolder
            viewHolder = new HighscoreViewHolder(view);
            view.setTag(viewHolder);

        } else {
            viewHolder = (HighscoreViewHolder) view.getTag();
        }

        createView(i, viewHolder);
        return view;
    }

    /**
     * Populates the views with appData for the current row
     */
    private void createView(int position, HighscoreViewHolder viewHolder) {
        final Highscore highscore = mHighscores.get(position);
        int rank = position + 1;

        // Set highscore appData, rank and user name
        viewHolder.highscoreRank.setText(String.valueOf(rank));
        viewHolder.highscoreUser.setText(highscore.getUser());

        viewHolder.highscoreScore.setText(String.valueOf(highscore.getCorrect()));
        viewHolder.highscorePossible.setText(String.valueOf(mMaxScore));

        // Calculate and set percentage of correct picks
        int percentage = 0;
        if (mMaxScore > 0) {
            percentage = (int) (((float) highscore.getCorrect() / (float) mMaxScore) * 100);
        }
        viewHolder.highscorePercentage.setText(String.valueOf(percentage) + mParent.getString(R.string.percent));

        // Show a small indicator that shows the currently logged in user in the list
        if (highscore.getUser().equals(mCurrentPlayer)) {
            viewHolder.highscorePlayerIndicator.setBackgroundColor(mParent.getResources().getColor(R.color.secondary_base));
            viewHolder.highscorePlayerIndicator.setVisibility(View.VISIBLE);
        } else {
            viewHolder.highscorePlayerIndicator.setVisibility(View.INVISIBLE);
        }

        // Set onClickListeners that show more detailed appData about the user and his picks
        // Either the playerStatistics are shown, or a specific week (depends on if overall or week view was clicked)
        // Override transitions with custom activity animations
        viewHolder.highscoreItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOverallHighscore) {
                    Intent intent = new Intent(mParent, PlayerStatisticsActivity.class);
                    intent.putExtra(PlayerStatisticsActivity.EXTRA_USER_NAME, highscore.getUser());
                    mParent.startActivity(intent);
                    mParent.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent intent = new Intent(mParent, GamesActivity.class);
                    intent.putExtra(GamesActivity.EXTRA_PLAYER_NAME, highscore.getUser());
                    intent.putExtra(GamesActivity.EXTRA_SEASON_INFO, mSeasonInfo);
                    intent.putExtra(GamesActivity.EXTRA_WEEK_SCORE, highscore.getCorrect());
                    mParent.startActivity(intent);
                    mParent.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        // Change arrow color and set it to the ImageView
        Drawable arrow = mParent.getResources().getDrawable(R.drawable.next);
        arrow.setColorFilter(mParent.getResources().getColor(R.color.secondary_base), PorterDuff.Mode.SRC_ATOP);
        viewHolder.highscoreArrow.setImageDrawable(arrow);
    }

    public void addData(Highscore highscore) {
        mHighscores.add(highscore);
    }

    public void setSeasonInfo(SeasonInfo seasonInfo) {
        mSeasonInfo = seasonInfo;
    }

    public void setOverallHighscores(boolean overallHighscores) {
        mOverallHighscore = overallHighscores;
    }

    public void setMaxScore(int maxScore) {
        mMaxScore = maxScore;
    }

    public void setCurrentPlayer(String playerName) {
        mCurrentPlayer = playerName;
    }

    /**
     * Clear the data of the list
     */
    public void clearData() {
        mHighscores.clear();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for the views of a row of the ListView
     */
    static class HighscoreViewHolder {
        @InjectView(R.id.highscoreItem) View highscoreItem;

        @InjectView(R.id.highscorePlayerIndicator) View highscorePlayerIndicator;

        @InjectView(R.id.highscoreRank) TextView highscoreRank;
        @InjectView(R.id.highscoreUserName) TextView highscoreUser;
        @InjectView(R.id.highscoreScore) TextView highscoreScore;
        @InjectView(R.id.highscorePossible) TextView highscorePossible;
        @InjectView(R.id.highscorePercentage) TextView highscorePercentage;

        @InjectView(R.id.imageArrow) ImageView highscoreArrow;

        HighscoreViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
        }
    }
}
