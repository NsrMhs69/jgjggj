package com.queen.musicPaint.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class OutputServiceModel<T> {

    @SerializedName("Status")
    public Status Status;

    @SerializedName("Body")
    public T Body;

    public Status getStatus() {
        return Status;
    }
    public void setStatus(Status status) {
        Status = status;
    }

    public T getBody() {
        return Body;
    }
    public void setBody(T body) {
        Body = body;
    }

}
