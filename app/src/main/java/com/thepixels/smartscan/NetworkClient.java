package com.thepixels.smartscan;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static Retrofit retrofit;
    private static final String Url = "https://thepixels.herokuapp.com/";
    public static  Retrofit getRetrofit(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(Url).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }
        return retrofit;
    }
}
