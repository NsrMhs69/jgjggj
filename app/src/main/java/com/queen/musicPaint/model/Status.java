package com.queen.musicPaint.model;


import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Status {

    @SerializedName("Code")
    public int Code;

    @SerializedName("Message")
    public String Message;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
