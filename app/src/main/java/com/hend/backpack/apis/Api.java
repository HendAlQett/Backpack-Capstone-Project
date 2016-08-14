package com.hend.backpack.apis;

import com.hend.backpack.models.Landmark;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by hend on 11/8/15.
 */
public interface Api {


    @GET("/landmarks")
    void requestLandmarks(Callback<List<Landmark>> callback);

}
