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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.mtrstudios.nflpickem.API.Data.Score;
import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.Handlers.PickEmDataHandler;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.Games.GamesActivity;

/**
 * ListViewAdapter to show a row of player statistics
 * Uses a ViewHandler to handle the specific views needed to display that appData
 */
public class PlayerStatisticsListAdapter extends BaseAdapter {

    private PlayerStatisticsActivity mParent;

    private SeasonInfo mSeasonInfo;
    private String mPlayerName;
    private int mMaxScore;

    private Map<Integer, Score> mScores = new HashMap<Integer, Score>();

    public PlayerStatisticsListAdapter(PlayerStatisticsActivity parent) {
        mParent = parent;
    }

    @Override
    public int getCount() {
        if (mSeasonInfo != null) {
            return mSeasonInfo.getWeek();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return mScores.get(i);
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
            LayoutInflater inflater = mParent.getLayoutInflater();
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
     * Populates the views to display a row of appData in the listView
     */
    private void createView(int position, ScoresViewHolder viewHolder) {
        int week = position + 1;
        final SeasonInfo rowSeason = new SeasonInfo(mSeasonInfo.getSeason(), week, mSeasonInfo.getType());

        if ((mScores != null) && (mScores.size() != 0)) {
            final int score = mScores.get(week).getScore();
            final int maxScore = mMaxScore;
            final int percentage = (int) (((float) score / (float) maxScore) * 100);

            // Set appData
            viewHolder.weekNumber.setText(String.valueOf(rowSeason.getWeek()));
            viewHolder.weekPercentage.setText(String.valueOf(percentage) + mParent.getString(R.string.percent));

            viewHolder.weekScore.setText(String.valueOf(score));
            viewHolder.weekMaxScore.setText(String.valueOf(maxScore));

            // Set onClickListener to show detailed information about picks via the GamesActivity
            // Override transitions with custom activity animations
            viewHolder.weekItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mParent, GamesActivity.class);
                    intent.putExtra(GamesActivity.EXTRA_PLAYER_NAME, mPlayerName);
                    intent.putExtra(GamesActivity.EXTRA_SEASON_INFO, rowSeason);
                    intent.putExtra(GamesActivity.EXTRA_WEEK_SCORE, score);
                    mParent.startActivity(intent);
                    mParent.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            // Change arrow color and set it to the ImageView
            Drawable arrow = mParent.getResources().getDrawable(R.drawable.next);
            arrow.setColorFilter(mParent.getResources().getColor(R.color.secondary_base), PorterDuff.Mode.SRC_ATOP);
            viewHolder.weekArrow.setImageDrawable(arrow);
        }
    }

    public void setScores(Map<Integer, Score> scores) {
        mScores.clear();
        mScores.putAll(scores);
        notifyDataSetChanged();
    }

    public void setPlayerName(String playerName) {
        mPlayerName = playerName;
    }

    public void setSeasonInfo(SeasonInfo seasonInfo) {
        mSeasonInfo = seasonInfo;
    }

    public void setMaxScore(int maxScore) {
        mMaxScore = maxScore;
    }

    /**
     * ViewHolder to reference the views used by a row of Player Statistics
     */
    static class ScoresViewHolder {
        @InjectView(R.id.weekItem) View weekItem;

        @InjectView(R.id.imageArrow) ImageView weekArrow;

        @InjectView(R.id.textWeekNumber) TextView weekNumber;
        @InjectView(R.id.textWeekPercentage) TextView weekPercentage;
        @InjectView(R.id.textWeekScore) TextView weekScore;
        @InjectView(R.id.textWeekMaxScore) TextView weekMaxScore;

        ScoresViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
        }
    }
}
