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

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import de.mtrstudios.nflpickem.API.Data.Score;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.Games.GamesActivity;

/**
 * ListViewAdapter to show a row of player statistics
 * Uses a ViewHandler to handle the specific views needed to display that data
 */
public class PlayerStatisticsListAdapter extends BaseAdapter {

    private PickEmApplication application;
    private PlayerStatisticsActivity parent;

    private SeasonInfo seasonInfo;

    private String playerName;

    private Map<Integer, Score> scores = new HashMap<Integer, Score>();

    public PlayerStatisticsListAdapter(PlayerStatisticsActivity parent, PickEmApplication application, String playerName) {
        this.parent = parent;
        this.application = application;
        this.seasonInfo = application.getSeasonInfo();
        this.playerName = playerName;
    }

    @Override
    public int getCount() {
        return seasonInfo.getWeek();
    }

    @Override
    public Object getItem(int i) {
        return scores.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ScoresViewHolder viewHolder;

        if (view == null) {
            // Inflate Layout
            LayoutInflater inflater = this.parent.getLayoutInflater();
            view = inflater.inflate(R.layout.row_score, viewGroup, false);

            // Set up ViewHolder
            viewHolder = new ScoresViewHolder(view);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ScoresViewHolder) view.getTag();
        }

        createView(i, viewHolder);
        return view;
    }

    /**
     * Populates the views to display a row of data in the listView
     */
    private void createView(int position, ScoresViewHolder viewHolder) {
        int week = position + 1;
        final SeasonInfo rowSeason = new SeasonInfo(application.getSeasonInfo().getSeason(), week, application.getSeasonInfo().getType());

        if ((scores != null) && (scores.size() != 0)) {
            int score = 0;
            int maxScore = application.getGamesCountForWeek(rowSeason);
            int percentage = 0;

            // Calculate percentage of correct picks
            if (scores.containsKey(week)) {
                Score currentScore = scores.get(week);

                score = currentScore.getScore();

                if (maxScore > 0) {
                    percentage = (int) (((float) score / (float) maxScore) * 100);
                }
            }

            // Set data
            viewHolder.weekNumber.setText(String.valueOf(rowSeason.getWeek()));
            viewHolder.weekPercentage.setText(String.valueOf(percentage) + parent.getString(R.string.percent));

            viewHolder.weekScore.setText(String.valueOf(score));
            viewHolder.weekMaxScore.setText(String.valueOf(maxScore));

            // Set onClickListener to show detailed information about picks via the GamesActivity
            // Override transitions with custom activity animations
            final int finalScore = score;
            viewHolder.weekItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(parent, GamesActivity.class);
                    intent.putExtra(GamesActivity.EXTRA_PLAYER_NAME, playerName);
                    intent.putExtra(GamesActivity.EXTRA_SEASON_INFO, rowSeason);
                    intent.putExtra(GamesActivity.EXTRA_WEEK_SCORE, finalScore);
                    parent.startActivity(intent);
                    parent.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            // Change arrow color and set it to the ImageView
            Drawable arrow = parent.getResources().getDrawable(R.drawable.next);
            arrow.setColorFilter(parent.getResources().getColor(R.color.secondary_base), PorterDuff.Mode.SRC_ATOP);
            viewHolder.weekArrow.setImageDrawable(arrow);
        }
    }

    /**
     * Adds score data and updates it if it was already in there
     */
    public void addData(Score score) {
        if (scores.containsKey(score.getWeek())) {
            scores.remove(score.getWeek());
        }
        scores.put(score.getWeek(), score);
    }

    /**
     * ViewHolder to reference the views used by a row of Player Statistics
     */
    static class ScoresViewHolder {
        protected View weekItem;

        protected ImageView weekArrow;

        protected TextView weekNumber;

        protected TextView weekPercentage;
        protected TextView weekScore;
        protected TextView weekMaxScore;

        ScoresViewHolder(View itemView) {
            this.weekItem = itemView.findViewById(R.id.weekItem);

            this.weekArrow = (ImageView) itemView.findViewById(R.id.imageArrow);

            this.weekNumber = (TextView) itemView.findViewById(R.id.textWeekNumber);

            this.weekPercentage = (TextView) itemView.findViewById(R.id.textWeekPercentage);
            this.weekScore = (TextView) itemView.findViewById(R.id.textWeekScore);
            this.weekMaxScore = (TextView) itemView.findViewById(R.id.textWeekMaxScore);
        }
    }
}
