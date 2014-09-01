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

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Information about the different teams
 * links to their full names, logos and colors
 */
public class NFLTeams {

    private static final Map<String, Integer> LOGOS;

    // Team icons replaced with dummy's because of licensing
    static {
        LOGOS = new HashMap<String, Integer>();
        LOGOS.put("ARI", R.drawable.dummy_teamicon);
        LOGOS.put("ATL", R.drawable.dummy_teamicon);
        LOGOS.put("BAL", R.drawable.dummy_teamicon);
        LOGOS.put("BUF", R.drawable.dummy_teamicon);
        LOGOS.put("CAR", R.drawable.dummy_teamicon);
        LOGOS.put("CHI", R.drawable.dummy_teamicon);
        LOGOS.put("CIN", R.drawable.dummy_teamicon);
        LOGOS.put("CLE", R.drawable.dummy_teamicon);
        LOGOS.put("DAL", R.drawable.dummy_teamicon);
        LOGOS.put("DEN", R.drawable.dummy_teamicon);
        LOGOS.put("DET", R.drawable.dummy_teamicon);
        LOGOS.put("GB", R.drawable.dummy_teamicon);
        LOGOS.put("HOU", R.drawable.dummy_teamicon);
        LOGOS.put("IND", R.drawable.dummy_teamicon);
        LOGOS.put("JAC", R.drawable.dummy_teamicon);
        LOGOS.put("KC", R.drawable.dummy_teamicon);
        LOGOS.put("MIA", R.drawable.dummy_teamicon);
        LOGOS.put("MIN", R.drawable.dummy_teamicon);
        LOGOS.put("NE", R.drawable.dummy_teamicon);
        LOGOS.put("NO", R.drawable.dummy_teamicon);
        LOGOS.put("NYG", R.drawable.dummy_teamicon);
        LOGOS.put("NYJ", R.drawable.dummy_teamicon);
        LOGOS.put("OAK", R.drawable.dummy_teamicon);
        LOGOS.put("PHI", R.drawable.dummy_teamicon);
        LOGOS.put("PIT", R.drawable.dummy_teamicon);
        LOGOS.put("SD", R.drawable.dummy_teamicon);
        LOGOS.put("SEA", R.drawable.dummy_teamicon);
        LOGOS.put("SF", R.drawable.dummy_teamicon);
        LOGOS.put("STL", R.drawable.dummy_teamicon);
        LOGOS.put("TB", R.drawable.dummy_teamicon);
        LOGOS.put("TEN", R.drawable.dummy_teamicon);
        LOGOS.put("WAS", R.drawable.dummy_teamicon);
    }

    private static final Map<String, Integer> COLORS;

    static {
        COLORS = new HashMap<String, Integer>();
        COLORS.put("ARI", Color.parseColor("#FFFFFF"));
        COLORS.put("ATL", Color.parseColor("#FFFFFF"));
        COLORS.put("BAL", Color.parseColor("#FFFFFF"));
        COLORS.put("BUF", Color.parseColor("#FFFFFF"));
        COLORS.put("CAR", Color.parseColor("#FFFFFF"));
        COLORS.put("CHI", Color.parseColor("#FFFFFF"));
        COLORS.put("CIN", Color.parseColor("#FFFFFF"));
        COLORS.put("CLE", Color.parseColor("#FFFFFF"));
        COLORS.put("DAL", Color.parseColor("#FFFFFF"));
        COLORS.put("DEN", Color.parseColor("#FFFFFF"));
        COLORS.put("DET", Color.parseColor("#FFFFFF"));
        COLORS.put("GB", Color.parseColor("#FFFFFF"));
        COLORS.put("HOU", Color.parseColor("#FFFFFF"));
        COLORS.put("IND", Color.parseColor("#FFFFFF"));
        COLORS.put("JAC", Color.parseColor("#FFFFFF"));
        COLORS.put("KC", Color.parseColor("#FFFFFF"));
        COLORS.put("MIA", Color.parseColor("#FFFFFF"));
        COLORS.put("MIN", Color.parseColor("#FFFFFF"));
        COLORS.put("NE", Color.parseColor("#FFFFFF"));
        COLORS.put("NO", Color.parseColor("#FFFFFF"));
        COLORS.put("NYG", Color.parseColor("#FFFFFF"));
        COLORS.put("NYJ", Color.parseColor("#FFFFFF"));
        COLORS.put("OAK", Color.parseColor("#FFFFFF"));
        COLORS.put("PHI", Color.parseColor("#FFFFFF"));
        COLORS.put("PIT", Color.parseColor("#FFFFFF"));
        COLORS.put("SD", Color.parseColor("#FFFFFF"));
        COLORS.put("SEA", Color.parseColor("#FFFFFF"));
        COLORS.put("SF", Color.parseColor("#FFFFFF"));
        COLORS.put("STL", Color.parseColor("#FFFFFF"));
        COLORS.put("TB", Color.parseColor("#FFFFFF"));
        COLORS.put("TEN", Color.parseColor("#FFFFFF"));
        COLORS.put("WAS", Color.parseColor("#FFFFFF"));
    }

    private static final Map<String, Integer> NAMES;

    static {
        NAMES = new HashMap<String, Integer>();
        NAMES.put("ARI", R.string.ari);
        NAMES.put("ATL", R.string.atl);
        NAMES.put("BAL", R.string.bal);
        NAMES.put("BUF", R.string.buf);
        NAMES.put("CAR", R.string.car);
        NAMES.put("CHI", R.string.chi);
        NAMES.put("CIN", R.string.cin);
        NAMES.put("CLE", R.string.cle);
        NAMES.put("DAL", R.string.dal);
        NAMES.put("DEN", R.string.den);
        NAMES.put("DET", R.string.det);
        NAMES.put("GB", R.string.gb);
        NAMES.put("HOU", R.string.hou);
        NAMES.put("IND", R.string.ind);
        NAMES.put("JAC", R.string.jax);
        NAMES.put("KC", R.string.kc);
        NAMES.put("MIA", R.string.mia);
        NAMES.put("MIN", R.string.min);
        NAMES.put("NE", R.string.ne);
        NAMES.put("NO", R.string.no);
        NAMES.put("NYG", R.string.nyg);
        NAMES.put("NYJ", R.string.nyj);
        NAMES.put("OAK", R.string.oak);
        NAMES.put("PHI", R.string.phi);
        NAMES.put("PIT", R.string.pit);
        NAMES.put("SD", R.string.sd);
        NAMES.put("SEA", R.string.sea);
        NAMES.put("SF", R.string.sf);
        NAMES.put("STL", R.string.stl);
        NAMES.put("TB", R.string.tb);
        NAMES.put("TEN", R.string.ten);
        NAMES.put("WAS", R.string.was);
    }

    private static final int COLOR_DEFAULT = Color.RED;
    private static final int LOGO_DEFAULT = R.drawable.launchericon;
    private static final int NAME_DEFAULT = R.string.undefined;


    public static int getLogoForTeam(String team) {
        if (LOGOS.containsKey(team)) {
            return LOGOS.get(team);
        } else {
            return LOGO_DEFAULT;
        }
    }

    public static int getColorForTeam(String team) {
        if (COLORS.containsKey(team)) {
            return COLORS.get(team);
        } else {
            return COLOR_DEFAULT;
        }
    }

    public static Integer getNameForTeam(String team) {
        if (NAMES.containsKey(team)) {
            return NAMES.get(team);
        } else {
            return NAME_DEFAULT;
        }
    }


}
