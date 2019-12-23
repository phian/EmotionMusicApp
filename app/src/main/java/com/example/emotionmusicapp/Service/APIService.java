package com.example.emotionmusicapp.Service;

public class APIService {
    private static String base_url ="https://self-governing-buoy.000webhostapp.com/Server/";
    public static Dataservice getService(){
        return APIRetrofitClient.getClient(base_url).create(Dataservice.class );
    }
}
