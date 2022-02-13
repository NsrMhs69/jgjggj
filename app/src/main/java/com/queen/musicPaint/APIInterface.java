package com.queen.musicPaint;


import com.queen.musicPaint.model.InputApplicationVersionDTO;
import com.queen.musicPaint.model.OutputGetApplicationVersion;
import com.queen.musicPaint.model.OutputServiceModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("GetApplicationVersion")
    Call<OutputServiceModel<OutputGetApplicationVersion>> getApplicationVersionCode(@Body InputApplicationVersionDTO inputApplicationVersionDTO);

}

