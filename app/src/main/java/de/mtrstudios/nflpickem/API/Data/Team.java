package de.mtrstudios.nflpickem.API.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Enum to describe a pick
 */
public enum Team {
    @SerializedName("HOME")
    HOME("HOME"),

    @SerializedName("AWAY")
    AWAY("AWAY");

    private final String pickCode;

    Team(String code) {
        this.pickCode = code;
    }

    public String getPickCode() {
        return pickCode;
    }

    public boolean isHome() {
        return (this == HOME);
    }

    public boolean isAway() {
        return (this == AWAY);
    }
}
