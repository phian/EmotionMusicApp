package com.example.emotionmusicapp.Service;

import com.example.emotionmusicapp.Model.baihat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Dataservice
{
@FormUrlEncoded
@POST("danhsachbaihat.php")
    Call<List<baihat>> getDataBaiHatTheoChude(@Field("id_chude") String id_chude );
}
