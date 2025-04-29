package com.example.heroicorganizer.api; // Adjust based on your folder structure

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.example.heroicorganizer.model.ApiResponse;

public interface ComicVineApi {
    @GET("issues/")
    Call<ApiResponse> searchComics(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
