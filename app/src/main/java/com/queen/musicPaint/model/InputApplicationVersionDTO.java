package com.queen.musicPaint.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class InputApplicationVersionDTO {

    @SerializedName("ApplicationName")
    public String ApplicationName;

    @SerializedName("MarketName")
    public String MarketName;

    public String getApplicationName() {
        return ApplicationName;
    }

    public void setApplicationName(String applicationName) {
        ApplicationName = applicationName;
    }

    public String getMarketName() {
        return MarketName;
    }

    public void setMarketName(String marketName) {
        MarketName = marketName;
    }
}
