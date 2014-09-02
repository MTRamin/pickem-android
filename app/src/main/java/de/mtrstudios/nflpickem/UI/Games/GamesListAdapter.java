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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mtrstudios.nflpickem.API.Data.Game;
import de.mtrstudios.nflpickem.API.Data.Pick;
import de.mtrstudios.nflpickem.Handlers.PickEmDataHandler;
import de.mtrstudios.nflpickem.NFLTeams;
import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;

/**
 * Adapter for a ListView to display Games and Picks
 * Uses a ViewHolder to reference the Views
 */
public class GamesListAdapter extends BaseAdapter {

    private Context context;
    private GamesFragment fragment;

    // Data
    private List<String> gamekeys = new ArrayList<String>();
    private Map<String, Game> games = new HashMap<String, Game>();
    private Map<String, String> picks = new HashMap<String, String>();

    public GamesListAdapter(Context context, GamesFragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return gamekeys.size();
    }

    @Override
    public Object getItem(int i) {
        return games.get(gamekeys.get(i));
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
            LayoutInflater inflater = ((GamesActivity) this.context).getLayoutInflater();
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
        final String gamekey = gamekeys.get(position);
        final Game game = games.get(gamekey);
        final String pick = picks.get(gamekey);

        if (game != null) {
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
            viewHolder.gameTime.setText(game.getKickoffParsed(context));
            viewHolder.vs.setText(context.getString(R.string.vs));

            // Load team logos with picasso into the imageViews
            Picasso.with(context).load(NFLTeams.getLogoForTeam(game.getHomeTeam())).into(viewHolder.homeIcon);
            Picasso.with(context).load(NFLTeams.getLogoForTeam(game.getAwayTeam())).into(viewHolder.awayIcon);

            // Set team scores (w-l-t)
            viewHolder.homeTeamScore.setText(PickEmDataHandler.getInstance(context).getScoreForTeam(game.getHomeTeam()));
            viewHolder.awayTeamScore.setText(PickEmDataHandler.getInstance(context).getScoreForTeam(game.getAwayTeam()));

            // Set default color for pick indicators
            viewHolder.homePickIndicator.setBackgroundColor(context.getResources().getColor(R.color.third_lighter));
            viewHolder.awayPickIndicator.setBackgroundColor(context.getResources().getColor(R.color.third_lighter));

            if (!game.isPreGame()) { // Game has started or is finished
                viewHolder.homeTeam.setClickable(false);
                viewHolder.awayTeam.setClickable(false);

                // Change background of row and show the score of the game
                viewHolder.gamePostKickoffIndicator.setBackgroundColor(context.getResources().getColor(R.color.game_post_kickoff));
                viewHolder.vs.setText(context.getString(R.string.list_pick_score_divider));
                viewHolder.homeScore.setText(String.valueOf(game.getHomeScore()));
                viewHolder.awayScore.setText(String.valueOf(game.getAwayScore()));
                viewHolder.homeScore.setVisibility(View.VISIBLE);
                viewHolder.awayScore.setVisibility(View.VISIBLE);

                // Set pick indicator
                if (pick != null) {
                    if (game.isPostGame()) { // Game has ended
                        // Show correct / wrong pick indicators and animate them in
                        if (pick.equals("HOME")) {
                            if (game.getWinner().equals("HOME")) {
                                viewHolder.homePickIndicator.setBackgroundColor(context.getResources().getColor(R.color.alternative_base));
                            } else {
                                viewHolder.homePickIndicator.setBackgroundColor(context.getResources().getColor(R.color.primary_lighter));
                            }
                        }

                        if (pick.equals("AWAY")) {
                            if (game.getWinner().equals("HOME")) {
                                viewHolder.awayPickIndicator.setBackgroundColor(context.getResources().getColor(R.color.primary_lighter));
                            } else {
                                viewHolder.awayPickIndicator.setBackgroundColor(context.getResources().getColor(R.color.alternative_base));
                            }
                        }
                    }
                }
            }

            // Animate the correct pick indicator to slowly show it to the user
            if (pick != null) {
                if (pick.equals("HOME")) {
                    pickAnimation(viewHolder.homePickIndicator, true);
                }

                if (pick.equals("AWAY")) {
                    pickAnimation(viewHolder.awayPickIndicator, false);
                }
            }

            // Set OnClickListeners to pick games
            viewHolder.homeTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pick("HOME", game, viewHolder);
                }
            });

            // Set OnClickListeners to pick games
            viewHolder.awayTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pick("AWAY", game, viewHolder);
                }
            });
        }
    }

    /**
     * Checks if a pick is actually different from the old pick and if picking is enabled
     * If the new pick needs to be submitted it calls the function doing so
     */
    private void pick(String pick, Game game, GameViewHolder viewHolder) {
        String currentPick = picks.get(game.getGamekey());
        boolean pickEnabled = ((PickEmApplication) fragment.getActivity().getApplication()).isPicksEnabled();

        boolean needToSubmitPick = pickEnabled && ((currentPick == null) || (!currentPick.equals(pick))) && (game.getQuarter().equals("P"));

        if (needToSubmitPick) {
            fragment.submitPick(pick, game.getGamekey(), viewHolder);
        }
    }

    /**
     * Prompts two animations to hide the old pick and show the new one
     * Determines which pickIndicators need to be shown/hidden
     */
    public void animateChangedPick(Pick pick, GameViewHolder viewHolder) {
        boolean isHome = (pick.getPick().equals("HOME"));

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
     * Resets all appData for this ListView Adapter to fill it with new appData
     */
    public void resetData() {
        this.gamekeys.clear();
        this.games.clear();
        this.picks.clear();
        notifyDataSetChanged();
    }

    /**
     * Adds a new Game to the Adapter appData
     * If the game is already in the appData, updates it
     */
    public void addData(Game game) {
        this.gamekeys.remove(game.getGamekey());

        this.gamekeys.add(game.getGamekey());
        this.games.put(game.getGamekey(), game);

    }

    /**
     * Adds a pick to the adapter appData
     * If the pick is alread in the appData, updates it
     */
    public void addData(Pick pick) {
        this.picks.put(pick.getGamekey(), pick.getPick());
    }

    /**
     * ViewHolder for all the views of a row that displays the game and pick information
     */
    static class GameViewHolder {
        protected TextView homeName;
        protected TextView awayName;

        protected TextView homeScore;
        protected TextView awayScore;

        protected TextView homeTeamScore;
        protected TextView awayTeamScore;

        protected TextView vs;
        protected TextView gameTime;

        protected ImageView homeIcon;
        protected ImageView awayIcon;

        protected View homeTeam;
        protected View awayTeam;

        protected View homePickIndicator;
        protected View awayPickIndicator;
        protected ViewGroup gamePostKickoffIndicator;

        GameViewHolder(View itemView) {
            this.homeName = (TextView) itemView.findViewById(R.id.textHomeName);
            this.awayName = (TextView) itemView.findViewById(R.id.textAwayName);

            this.homeScore = (TextView) itemView.findViewById(R.id.textHomeScore);
            this.awayScore = (TextView) itemView.findViewById(R.id.textAwayScore);

            this.homeTeamScore = (TextView) itemView.findViewById(R.id.textHomeTeamScore);
            this.awayTeamScore = (TextView) itemView.findViewById(R.id.textAwayTeamScore);

            this.vs = (TextView) itemView.findViewById(R.id.textvs);
            this.gameTime = (TextView) itemView.findViewById(R.id.textGameTime);

            this.homeIcon = (ImageView) itemView.findViewById(R.id.imageHome);
            this.awayIcon = (ImageView) itemView.findViewById(R.id.imageAway);

            this.homeTeam = itemView.findViewById(R.id.homeTeam);
            this.awayTeam = itemView.findViewById(R.id.awayTeam);

            this.homePickIndicator = itemView.findViewById(R.id.homePickIndicator);
            this.awayPickIndicator = itemView.findViewById(R.id.awayPickIndicator);
            this.gamePostKickoffIndicator = (ViewGroup) itemView.findViewById(R.id.background);
        }
    }
}
