package com.queen.musicPaint.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class OutputGetApplicationVersion {

    @SerializedName("VersionNumber")
    public String VersionNumber;

    @SerializedName("VersionText")
    public String VersionText;

    @SerializedName("ForceUpdate")
    public boolean ForceUpdate;

    public String getVersionNumber() {
        return VersionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        VersionNumber = versionNumber;
    }

    public String getVersionText() {
        return VersionText;
    }

    public void setVersionText(String versionText) {
        VersionText = versionText;
    }

    public boolean isForceUpate() {
        return ForceUpdate;
    }

    public void setForceUpate(boolean ForceUpdate) {
        ForceUpdate = ForceUpdate;
    }
}
