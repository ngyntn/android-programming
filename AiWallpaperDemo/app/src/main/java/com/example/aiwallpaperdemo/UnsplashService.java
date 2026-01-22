package com.example.aiwallpaperdemo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface UnsplashService {

    // Endpoint: /photos/random
    @GET("photos/random")
    Call<UnsplashPhoto> getRandomPhoto(
            @Header("Authorization") String clientId, // Client-ID YOUR_ACCESS_KEY
            @Query("orientation") String orientation // Lọc theo chiều: landscape, portrait
    );
}
