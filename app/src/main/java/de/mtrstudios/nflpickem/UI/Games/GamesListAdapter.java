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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.mtrstudios.nflpickem.API.Data.Comparators.GamesComparator;
import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Team;
import de.mtrstudios.nflpickem.NFLTeams;
import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;

/**
 * Adapter for a ListView to display Games and Picks
 * Uses a ViewHolder to reference the Views
 */
public class GamesListAdapter extends BaseAdapter {

    private Context mContext;
    private GamesFragment mFragment;

    // Data
    private List<Game> mGames = new ArrayList<Game>();
    private boolean mPickingEnabled = false;

    public GamesListAdapter(Context context, GamesFragment fragment) {
        this.mContext = context;
        this.mFragment = fragment;
    }

    @Override
    public int getCount() {
        return mGames.size();
    }

    @Override
    public Object getItem(int i) {
        return mGames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GameViewHolder viewHolder;

        if (convertView == null) {
            // Inflate Layout
            LayoutInflater inflater = mFragment.getActivity().getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_game, parent, false);

            // Set up ViewHolder
            viewHolder = new GameViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GameViewHolder) convertView.getTag();
        }

        createView(position, viewHolder);
        return convertView;
    }

    /**
     * Creates and populates the views for one row of appData
     */
    private void createView(int position, final GameViewHolder viewHolder) {
        final Game game = mGames.get(position);

        // Reset some values and hide some elements that will only be shown on certain conditions
        viewHolder.gamePostKickoffIndicator.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.homePickIndicator.setBackground(null);
        viewHolder.awayPickIndicator.setBackground(null);
        viewHolder.homeIcon.setImageBitmap(null);
        viewHolder.awayIcon.setImageBitmap(null);
        viewHolder.homeScore.setVisibility(View.INVISIBLE);
        viewHolder.awayScore.setVisibility(View.INVISIBLE);
        viewHolder.homePickIndicator.setVisibility(View.INVISIBLE);
        viewHolder.awayPickIndicator.setVisibility(View.INVISIBLE);

        // Set Team Names
        viewHolder.homeName.setText(game.getHomeTeam());
        viewHolder.awayName.setText(game.getAwayTeam());

        // Set Game time and vs text
        viewHolder.gameTime.setText(game.getKickoffParsed(mContext));
        viewHolder.vs.setText(mContext.getString(R.string.vs));

        // Load team logos with picasso into the imageViews
        Picasso.with(mContext).load(NFLTeams.getLogoForTeam(game.getHomeTeam())).into(viewHolder.homeIcon);
        Picasso.with(mContext).load(NFLTeams.getLogoForTeam(game.getAwayTeam())).into(viewHolder.awayIcon);

        // Set team scores (w-l-t)
        viewHolder.homeTeamScore.setText(game.getHomeTeamSeasonScore(mContext));
        viewHolder.awayTeamScore.setText(game.getAwayTeamSeasonScore(mContext));

        // Set default color for pick indicators
        viewHolder.homePickIndicator.setBackgroundColor(mContext.getResources().getColor(R.color.third_lighter));
        viewHolder.awayPickIndicator.setBackgroundColor(mContext.getResources().getColor(R.color.third_lighter));

        if (!game.isPreGame()) { // Game has started or is finished
            viewHolder.homeTeam.setClickable(false);
            viewHolder.awayTeam.setClickable(false);

            // Change background of row and show the score of the game
            viewHolder.gamePostKickoffIndicator.setBackgroundColor(mContext.getResources().getColor(R.color.game_post_kickoff));
            viewHolder.vs.setText(mContext.getString(R.string.list_pick_score_divider));
            viewHolder.homeScore.setText(String.valueOf(game.getHomeScore()));
            viewHolder.awayScore.setText(String.valueOf(game.getAwayScore()));
            viewHolder.homeScore.setVisibility(View.VISIBLE);
            viewHolder.awayScore.setVisibility(View.VISIBLE);

            // Set pick indicator
            if (game.getPick() != null) {
                int correctColor = mContext.getResources().getColor(R.color.alternative_lightest);
                int wrongColor = mContext.getResources().getColor(R.color.primary_lightest);

                if (game.isPostGame()) { // Game has ended
                    correctColor = mContext.getResources().getColor(R.color.alternative_base);
                    wrongColor = mContext.getResources().getColor(R.color.primary_base);
                }

                // Show correct / wrong pick indicators and animate them in
                if (game.getPick().isHome()) {
                    if (game.getWinner().isHome()) {
                        viewHolder.homePickIndicator.setBackgroundColor(correctColor);
                    } else {
                        viewHolder.homePickIndicator.setBackgroundColor(wrongColor);
                    }
                }

                if (game.getPick().isAway()) {
                    if (game.getWinner().isHome()) {
                        viewHolder.awayPickIndicator.setBackgroundColor(wrongColor);
                    } else {
                        viewHolder.awayPickIndicator.setBackgroundColor(correctColor);
                    }
                }
            }
        }

        // Animate the correct pick indicator to slowly show it to the user
        if (game.getPick() != null) {
            if (game.getPick().isHome()) {
                pickAnimation(viewHolder.homePickIndicator, true);
            }

            if (game.getPick().isAway()) {
                pickAnimation(viewHolder.awayPickIndicator, false);
            }
        }

        // Set OnClickListeners to pick games
        viewHolder.homeTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick(Team.HOME, game, viewHolder);
            }
        });

        // Set OnClickListeners to pick games
        viewHolder.awayTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick(Team.AWAY, game, viewHolder);
            }
        });
    }

    /**
     * Checks if a pick is actually different from the old pick and if picking is enabled
     * If the new pick needs to be submitted it calls the function doing so
     */
    private void pick(Team pick, Game game, GameViewHolder viewHolder) {
        Team currentPick = game.getPick();

        boolean needToSubmitPick = mPickingEnabled && ((currentPick == null) || (!currentPick.equals(pick))) && (game.isPreGame());

        if (needToSubmitPick) {
            mFragment.submitPick(pick, game, viewHolder);
        }
    }

    /**
     * Prompts two animations to hide the old pick and show the new one
     * Determines which pickIndicators need to be shown/hidden
     */
    public void animateChangedPick(Team pick, GameViewHolder viewHolder) {
        boolean isHome = (pick.isHome());

        View animateIn = (isHome) ? viewHolder.homePickIndicator : viewHolder.awayPickIndicator;
        View animateOut = (isHome) ? viewHolder.awayPickIndicator : viewHolder.homePickIndicator;

        pickAnimation(animateIn, isHome);
        unpickAnimation(animateOut, !isHome);
    }

    /**
     * Animates a pick indicator to show on the UI
     */
    private void pickAnimation(View target, boolean isHome) {

        target.setVisibility(View.VISIBLE);
        int width = target.getWidth();

        // First load has width of targets = 0, no animation on first list load
        if (width != 0) {
            int end = target.getLeft();
            int start = (isHome) ? end - width : end + width;

            ObjectAnimator anim = ObjectAnimator.ofFloat(target, "x", start, end);
            anim.setDuration(PickEmApplication.ANIMATION_DURATION);
            anim.start();
        }
    }

    /**
     * Animates a pick indicator to hide from the UI
     */
    private void unpickAnimation(final View target, boolean isHome) {

        int width = target.getWidth();

        if (width != 0) {
            final int start = target.getLeft();
            final int end = (isHome) ? start - width : start + width;

            ObjectAnimator anim = ObjectAnimator.ofFloat(target, "x", start, end);
            anim.setDuration(PickEmApplication.ANIMATION_DURATION);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    target.setX(start);
                    target.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();
        } else {
            target.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Adds a new Game to the Adapter appData
     * If the game is already in the appData, updates it
     */
    public void addGames(List<Game> newGames) {
        Collections.sort(newGames, new GamesComparator());

        this.mGames.clear();
        this.mGames.addAll(newGames);
    }

    public void setPickingEnabled(boolean pickingEnabled) {
        this.mPickingEnabled = pickingEnabled;
    }

    /**
     * ViewHolder for all the views of a row that displays the game and pick information
     */
    public static class GameViewHolder {
        @InjectView(R.id.textHomeName) TextView homeName;
        @InjectView(R.id.textAwayName) TextView awayName;

        @InjectView(R.id.textHomeScore) TextView homeScore;
        @InjectView(R.id.textAwayScore) TextView awayScore;

        @InjectView(R.id.textHomeTeamScore) TextView homeTeamScore;
        @InjectView(R.id.textAwayTeamScore) TextView awayTeamScore;

        @InjectView(R.id.textvs) TextView vs;
        @InjectView(R.id.textGameTime) TextView gameTime;

        @InjectView(R.id.imageHome) ImageView homeIcon;
        @InjectView(R.id.imageAway) ImageView awayIcon;

        @InjectView(R.id.homeTeam) View homeTeam;
        @InjectView(R.id.awayTeam) View awayTeam;

        @InjectView(R.id.homePickIndicator) View homePickIndicator;
        @InjectView(R.id.awayPickIndicator) View awayPickIndicator;
        @InjectView(R.id.background) ViewGroup gamePostKickoffIndicator;

        GameViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
        }
    }
}
