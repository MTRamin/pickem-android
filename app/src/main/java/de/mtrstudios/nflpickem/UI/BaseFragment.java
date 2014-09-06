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
package de.mtrstudios.nflpickem.UI;

import android.app.Fragment;
import android.os.Bundle;

import com.squareup.otto.Bus;

import butterknife.ButterKnife;
import de.mtrstudios.nflpickem.Handlers.BusHandler;
import de.mtrstudios.nflpickem.Handlers.PickEmDataHandler;

/**
 * Base Fragment, sets up some variables
 */
public class BaseFragment extends Fragment {

    protected PickEmDataHandler mAppData;
    protected Bus mBus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mAppData = PickEmDataHandler.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        mBus = BusHandler.getInstance();
        mBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mBus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Reset the ButterKnife views
        ButterKnife.reset(this);
    }
}
