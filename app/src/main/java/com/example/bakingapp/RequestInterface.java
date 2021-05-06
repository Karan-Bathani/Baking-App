package com.example.bakingapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {
    @GET(AppUtils.JSON_LOC)
    Call<List<Recipe>> getJSON();

}
