package com.example.wallpaperclient.api;

import java.util.concurrent.TimeUnit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public class ApiClient {
    // LƯU Ý:
    // - Dùng "http://10.0.2.2:8000/" nếu chạy trên Android Emulator.
    // - Dùng "http://192.168.1.X:8000/" nếu chạy trên điện thoại thật (thay X bằng IP máy tính).
    private static final String BASE_URL = "http://13.250.50.115:8000/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS) // Thời gian chờ kết nối server
                    .readTimeout(60, TimeUnit.SECONDS)    // Thời gian chờ server trả dữ liệu (quan trọng cho AI)
                    .writeTimeout(60, TimeUnit.SECONDS)   // Thời gian chờ gửi dữ liệu lên
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}