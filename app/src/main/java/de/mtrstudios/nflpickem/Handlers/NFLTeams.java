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

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

import de.mtrstudios.nflpickem.R;

/**
 * Information about the different teams
 * links to their full names, logos and colors
 */
public class NFLTeams {

    private static final Map<String, Integer> LOGOS;

    static {
        LOGOS = new HashMap<String, Integer>();
        LOGOS.put("ARI", R.drawable.ari);
        LOGOS.put("ATL", R.drawable.atl);
        LOGOS.put("BAL", R.drawable.bal);
        LOGOS.put("BUF", R.drawable.buf);
        LOGOS.put("CAR", R.drawable.car);
        LOGOS.put("CHI", R.drawable.chi);
        LOGOS.put("CIN", R.drawable.cin);
        LOGOS.put("CLE", R.drawable.cle);
        LOGOS.put("DAL", R.drawable.dal);
        LOGOS.put("DEN", R.drawable.den);
        LOGOS.put("DET", R.drawable.det);
        LOGOS.put("GB", R.drawable.gb);
        LOGOS.put("HOU", R.drawable.hou);
        LOGOS.put("IND", R.drawable.ind);
        LOGOS.put("JAC", R.drawable.jac);
        LOGOS.put("KC", R.drawable.kc);
        LOGOS.put("MIA", R.drawable.mia);
        LOGOS.put("MIN", R.drawable.min);
        LOGOS.put("NE", R.drawable.ne);
        LOGOS.put("NO", R.drawable.no);
        LOGOS.put("NYG", R.drawable.nyg);
        LOGOS.put("NYJ", R.drawable.nyj);
        LOGOS.put("OAK", R.drawable.oak);
        LOGOS.put("PHI", R.drawable.phi);
        LOGOS.put("PIT", R.drawable.pit);
        LOGOS.put("SD", R.drawable.sd);
        LOGOS.put("SEA", R.drawable.sea);
        LOGOS.put("SF", R.drawable.sf);
        LOGOS.put("STL", R.drawable.stl);
        LOGOS.put("TB", R.drawable.tb);
        LOGOS.put("TEN", R.drawable.ten);
        LOGOS.put("WAS", R.drawable.was);
    }

    private static final Map<String, Integer> COLORS;

    static {
        COLORS = new HashMap<String, Integer>();
        COLORS.put("ARI", Color.parseColor("#870619"));
        COLORS.put("ATL", Color.parseColor("#BD0D18"));
        COLORS.put("BAL", Color.parseColor("#280353"));
        COLORS.put("BUF", Color.parseColor("#00338D"));
        COLORS.put("CAR", Color.parseColor("#000000"));
        COLORS.put("CHI", Color.parseColor("#03202F"));
        COLORS.put("CIN", Color.parseColor("#000000"));
        COLORS.put("CLE", Color.parseColor("#26201E"));
        COLORS.put("DAL", Color.parseColor("#002244"));
        COLORS.put("DEN", Color.parseColor("#FB4F14"));
        COLORS.put("DET", Color.parseColor("#006DB0"));
        COLORS.put("GB", Color.parseColor("#213D30"));
        COLORS.put("HOU", Color.parseColor("#02253A"));
        COLORS.put("IND", Color.parseColor("#003B7B"));
        COLORS.put("JAC", Color.parseColor("#000000"));
        COLORS.put("KC", Color.parseColor("#B20032"));
        COLORS.put("MIA", Color.parseColor("#008d97"));
        COLORS.put("MIN", Color.parseColor("#4F2682"));
        COLORS.put("NE", Color.parseColor("#0D254C"));
        COLORS.put("NO", Color.parseColor("#D2B887"));
        COLORS.put("NYG", Color.parseColor("#192F6B"));
        COLORS.put("NYJ", Color.parseColor("#0C371D"));
        COLORS.put("OAK", Color.parseColor("#C4C8CB"));
        COLORS.put("PHI", Color.parseColor("#003B48"));
        COLORS.put("PIT", Color.parseColor("#000000"));
        COLORS.put("SD", Color.parseColor("#08214A"));
        COLORS.put("SEA", Color.parseColor("#06192E"));
        COLORS.put("SF", Color.parseColor("#06192E"));
        COLORS.put("STL", Color.parseColor("#13264B"));
        COLORS.put("TB", Color.parseColor("#D60A0B"));
        COLORS.put("TEN", Color.parseColor("#648FCC"));
        COLORS.put("WAS", Color.parseColor("#773141"));
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
