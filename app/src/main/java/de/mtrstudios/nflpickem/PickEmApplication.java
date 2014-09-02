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

package de.mtrstudios.nflpickem;

import android.app.Application;
import android.preference.PreferenceManager;

/**
 * Custom Application Class to store all appData and handle necessary references
 */
public class PickEmApplication extends Application {

    // Constants
    public static final int DOWNLOADS_NEEDED_LIVE = 5;
    public static final int DOWNLOADS_NEEDED_RECAP = 2;
    public static final int ANIMATION_DURATION = 200;

    // Global
    private boolean picksEnabled = false;


    @Override
    public void onCreate() {
        initializeSettings();

        super.onCreate();
    }

    /**
     * Initializes the settings values
     */
    private void initializeSettings() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public boolean isPicksEnabled() {
        return picksEnabled;
    }

    public void setPicksEnabled(boolean picksEnabled) {
        this.picksEnabled = picksEnabled;
    }

}
