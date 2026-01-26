package com.example.wallpaperclient.api;


import okhttp3.ResponseBody;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 1. Tìm kiếm (Search)
    @GET("api/wallpapers/search")
    Call<List<WallpaperResponse>> searchWallpapers(
            @Query("query") String query,
            @Query("page") int page
    );

    // 2. Lấy Random / Popular / Tag
    @GET("api/wallpapers/random")
    Call<List<WallpaperResponse>> getRandomWallpapers(
            @Query("tag") String tag
    );

    // Trả về ResponseBody (Dữ liệu ảnh thô)
    @POST("api/ai/generate")
    Call<ResponseBody> generateAiImage(@Body AIRequest request);

    // API Download ảnh từ Proxy Python
    @GET("api/wallpapers/download")
    Call<ResponseBody> downloadImage(@Query("url") String url);

}