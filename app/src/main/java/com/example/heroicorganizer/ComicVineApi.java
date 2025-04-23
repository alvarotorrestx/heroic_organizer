package com.example.heroicorganizer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ComicVineApi {
    @GET("search/")
    Call<ApiResponse> searchComics(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("format") String format
    );
}
