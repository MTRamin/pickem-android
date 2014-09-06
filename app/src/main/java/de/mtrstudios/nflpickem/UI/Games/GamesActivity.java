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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.mtrstudios.nflpickem.API.Responses.SeasonInfo;
import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.BaseActivity;
import de.mtrstudios.nflpickem.UI.Highscores.HighscoresActivity;
import de.mtrstudios.nflpickem.UI.Login.LoginActivity;
import de.mtrstudios.nflpickem.UI.Settings.SettingsActivity;

/**
 * Main Activity, showing a list of games and picks
 * Uses the GamesFragment to display appData
 * This Activity can get called with an intent with user and season appData to display old weeks/picks
 */
public class GamesActivity extends BaseActivity
        implements GamesFragment.OnFragmentInteractionListener {

    public static final String EXTRA_PLAYER_NAME = "player_name";
    public static final String EXTRA_SEASON_INFO = "season_info";
    public static final String EXTRA_WEEK_SCORE = "week_score";


    /**
     * Find out if a user is already logged in
     * Checks if a user token is saved either in the application class or the shared preferences
     */
    private boolean isUserLoggedIn() {
        return (!appData.getUserToken().equals("null"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        if (isUserLoggedIn()) { // Check if user is logged in already

            String playerName;
            SeasonInfo seasonInfo;
            int score;

            GamesFragment fragment;

            // Get data from intent, if one was sent with data
            // Create Fragment
            Intent intent = getIntent();
            if (intent.getExtras() != null) {
                playerName = intent.getStringExtra(EXTRA_PLAYER_NAME);
                seasonInfo = intent.getParcelableExtra(EXTRA_SEASON_INFO);
                score = intent.getIntExtra(EXTRA_WEEK_SCORE, -1);

                fragment = GamesFragment.newInstance(playerName, seasonInfo, score);
            } else {
                fragment = GamesFragment.newInstance();
            }

            // Set Fragment
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        } else { // Call login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_settings:
                // Launch settings activity
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_show_highscores:
                // Launch Highscore Activity
                intent = new Intent(this, HighscoresActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                appData.signOut();

                // Start Log-In Activity
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
