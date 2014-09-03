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

import de.mtrstudios.nflpickem.API.Data.Highscore;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.Games.GamesActivity;
import de.mtrstudios.nflpickem.UI.PlayerStatistics.PlayerStatisticsActivity;

/**
 * ListViewAdapter that shows appData about highscores in a row.
 */
public class HighscoresListAdapter extends BaseAdapter {

    private HighscoresActivity parent;

    private List<Highscore> highscores = new ArrayList<Highscore>();

    private int maxScore;
    private String currentPlayer;
    private SeasonInfo seasonInfo;
    private boolean isOverallHighscores;

    public HighscoresListAdapter(HighscoresActivity parent) {
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return highscores.size();
    }

    @Override
    public Object getItem(int i) {
        return highscores.get(i);
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
            LayoutInflater inflater = this.parent.getLayoutInflater();
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
        final Highscore highscore = highscores.get(position);

        // Set highscore appData, rank and user name
        viewHolder.highscoreRank.setText(String.valueOf(position + 1));
        viewHolder.highscoreUser.setText(highscore.getUser());

        viewHolder.highscoreScore.setText(String.valueOf(highscore.getCorrect()));
        viewHolder.highscorePossible.setText(String.valueOf(maxScore));

        // Calculate and set percentage of correct picks
        int percentage = 0;
        if (maxScore > 0) {
            percentage = (int) (((float) highscore.getCorrect() / (float) maxScore) * 100);
        }
        viewHolder.highscorePercentage.setText(String.valueOf(percentage) + parent.getString(R.string.percent));

        // Show a small indicator that shows the currently logged in user in the list
        if (highscore.getUser().equals(currentPlayer)) {
            viewHolder.highscorePlayerIndicator.setBackgroundColor(parent.getResources().getColor(R.color.secondary_base));
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
                if (isOverallHighscores) {
                    Intent intent = new Intent(parent, PlayerStatisticsActivity.class);
                    intent.putExtra(PlayerStatisticsActivity.EXTRA_USER_NAME, highscore.getUser());
                    parent.startActivity(intent);
                    parent.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent intent = new Intent(parent, GamesActivity.class);
                    intent.putExtra(GamesActivity.EXTRA_PLAYER_NAME, highscore.getUser());
                    intent.putExtra(GamesActivity.EXTRA_SEASON_INFO, seasonInfo);
                    intent.putExtra(GamesActivity.EXTRA_WEEK_SCORE, highscore.getCorrect());
                    parent.startActivity(intent);
                    parent.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        // Change arrow color and set it to the ImageView
        Drawable arrow = parent.getResources().getDrawable(R.drawable.next);
        arrow.setColorFilter(parent.getResources().getColor(R.color.secondary_base), PorterDuff.Mode.SRC_ATOP);
        viewHolder.highscoreArrow.setImageDrawable(arrow);
    }

    public void addData(Highscore highscore) {
        this.highscores.add(highscore);
    }

    public void setSeasonInfo(SeasonInfo seasonInfo) {
        this.seasonInfo = seasonInfo;
    }

    public void setOverallHighscores(boolean overallHighscores) {
        this.isOverallHighscores = overallHighscores;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public void setCurrentPlayer(String playerName) {
        this.currentPlayer = playerName;
    }

    /**
     * Clear the data of the list
     */
    public void clearData() {
        this.highscores.clear();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for the views of a row of the ListView
     */
    static class HighscoreViewHolder {
        protected View highscoreItem;

        protected View highscorePlayerIndicator;

        protected TextView highscoreRank;
        protected TextView highscoreUser;
        protected TextView highscoreScore;
        protected TextView highscorePossible;
        protected TextView highscorePercentage;

        protected ImageView highscoreArrow;

        HighscoreViewHolder(View itemView) {
            this.highscoreItem = itemView.findViewById(R.id.highscoreItem);

            this.highscorePlayerIndicator = itemView.findViewById(R.id.highscorePlayerIndicator);

            this.highscoreRank = (TextView) itemView.findViewById(R.id.highscoreRank);
            this.highscoreUser = (TextView) itemView.findViewById(R.id.highscoreUserName);

            this.highscoreScore = (TextView) itemView.findViewById(R.id.highscoreScore);
            this.highscorePossible = (TextView) itemView.findViewById(R.id.highscorePossible);
            this.highscorePercentage = (TextView) itemView.findViewById(R.id.highscorePercentage);

            this.highscoreArrow = (ImageView) itemView.findViewById(R.id.imageArrow);
        }
    }
}
