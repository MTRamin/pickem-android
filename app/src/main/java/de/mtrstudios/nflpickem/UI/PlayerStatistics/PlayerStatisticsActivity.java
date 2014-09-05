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
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.mtrstudios.nflpickem.PickEmApplication;
import de.mtrstudios.nflpickem.R;
import de.mtrstudios.nflpickem.UI.BaseActivity;
import de.mtrstudios.nflpickem.UI.Login.LoginActivity;
import de.mtrstudios.nflpickem.UI.Settings.SettingsActivity;

/**
 * Activity that shows detailed appData about a users performance over the weeks
 * Data is shown via the PlayerStstisticsFragment
 */
public class PlayerStatisticsActivity extends BaseActivity implements PlayerStatisticsFragment.OnFragmentInteractionListener {

    public static final String EXTRA_USER_NAME = "user_name";

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        // Receive intent with username information
        Intent intent = getIntent();
        String userName = intent.getStringExtra(EXTRA_USER_NAME);

        // Create Fragment instance and show the fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, PlayerStatisticsFragment.newInstance(userName))
                    .commit();
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
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
}
