package com.example.heroicorganizer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ComicVineApi {
    @GET("issues/") // Changed endpoint for better accuracy
    Call<ApiResponse> searchComics(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("format") String format, // JSON format
            @Query("limit") int limit,      // Pagination support
            @Query("offset") int offset,    // Pagination offset
            @Query("publisher") String publisher, // Filter by publisher
            @Query("year") int year         // Filter by year
    );
}
