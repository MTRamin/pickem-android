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
package de.mtrstudios.nflpickem.Handlers;

import de.mtrstudios.nflpickem.API.PickEmAPI;
import retrofit.RestAdapter;

/**
 * Singleton to hold the Retrofit API reference
 */
public class ApiHandler {

    // URL of the API
    public static final String API_URL = "https://mtrpickem.herokuapp.com";

    private static ApiHandler mInstance;

    // Reference to the Retrofit API
    private PickEmAPI api;

    /**
     * Create the API reference
     */
    public ApiHandler() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();

        this.api = restAdapter.create(PickEmAPI.class);
    }

    public static ApiHandler getInstance() {
        if (mInstance == null) {
            mInstance = new ApiHandler();
        }

        return mInstance;
    }

    public PickEmAPI getApi() {
        return api;
    }
}
