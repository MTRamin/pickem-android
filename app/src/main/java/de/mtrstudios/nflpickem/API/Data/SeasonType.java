package de.mtrstudios.nflpickem.API.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Enum to manage the type of the season
 */
public enum SeasonType {
    @SerializedName("PRE")
    PRE("PRE"),

    @SerializedName("REG")
    REGULAR("REG"),

    @SerializedName("POST")
    POST("POST");

    private final String seasonTypeCode;

    SeasonType(String code) {
        this.seasonTypeCode = code;
    }

    public String getSeasonTypeCode() {
        return seasonTypeCode;
    }
}
