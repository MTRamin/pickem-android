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

import com.squareup.otto.Bus;

import de.mtrstudios.nflpickem.API.PickEmAPI;
import de.mtrstudios.nflpickem.Services.HighscoresService;
import de.mtrstudios.nflpickem.Services.PlayerStatisticsService;

/**
 * Holds all the different Services that listen to LoadEvents
 */
public class ServiceHolder {

    // References to API and Event Bus
    private PickEmAPI mApi = ApiHandler.getInstance().getApi();
    private Bus mBus = BusHandler.getInstance();

    // Services
    private PlayerStatisticsService playerStatisticsService;
    private HighscoresService highscoresService;

    public ServiceHolder() {
        createServices();
        registerServices();
    }

    /**
     * Create all services that should handle events
     */
    private void createServices() {
        playerStatisticsService = new PlayerStatisticsService(mApi, mBus);
        highscoresService = new HighscoresService(mApi, mBus);
    }

    /**
     * Register all services on the event bus
     */
    private void registerServices() {
        mBus.register(playerStatisticsService);
        mBus.register(highscoresService);
    }
}
