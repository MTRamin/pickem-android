package de.mtrstudios.nflpickem.API.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Enum to manage the quarter of a game
 */
public enum Quarter {
    @SerializedName("P")
    PREGAME("P"),

    @SerializedName("1")
    FIRST("1"),

    @SerializedName("2")
    SECOND("2"),

    @SerializedName("H")
    HALFTIME("H"),

    @SerializedName("3")
    THIRD("3"),

    @SerializedName("4")
    FOURTH("4"),

    @SerializedName("F")
    FINAL("F"),

    @SerializedName("FO")
    FINALOVERTIME("FO");

    private final String quarterCode;

    Quarter(String quarterCode) {
        this.quarterCode = quarterCode;
    }

    public String getQuarterCode() {
        return this.quarterCode;
    }
}
