package com.info121.mylimo.api;


import com.info121.mylimo.models.ConfirmJobRes;
import com.info121.mylimo.models.LoginRes;
import com.info121.mylimo.models.RemindLaterRes;
import com.info121.mylimo.models.SaveShowPicRes;
import com.info121.mylimo.models.SaveSignatureRes;
import com.info121.mylimo.models.UpdateDriverDetailRes;
import com.info121.mylimo.models.UpdateDriverLocationRes;
import com.info121.mylimo.models.VersionRes;
import com.info121.mylimo.models.product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by KZHTUN on 7/6/2017.
 */

public interface APIService {

    @GET("validatedriverDetail/{user}")
    Call<LoginRes> getAuthentication(@Path("user") String user);

    //amad,123,android,241jlksfljsaf
    @GET("updatedriverdetail/{user},{deviceId},{deviceType},{fcm_token}")
    Call<UpdateDriverDetailRes> updateDriverDetail(@Path("user") String user, @Path("deviceId") String deviceId,@Path("deviceType") String deviceType, @Path("fcm_token") String fcm_token);

    //amad,1.299654,103.855107,0
    @GET("getdriverlocation/{user},{latitude},{longitude},{status},{address}")
    Call<UpdateDriverLocationRes> updateDriverLocation(@Path("user") String user, @Path("latitude") String latitude,@Path("longitude") String longitude, @Path("status") int status, @Path("address") String addresss);


    @GET("saveshowpic/{user},{job_no},{filename}")
    Call<SaveShowPicRes> saveShowPic(@Path("user") String user, @Path("job_no") String jobNo, @Path("filename") String fileName);

    @GET("confirmjobreminder/{job_no}")
    Call<ConfirmJobRes> confirmJob(@Path("job_no") String jobNo);

    @GET("remindmelater/{job_no}")
    Call<RemindLaterRes> remindLater(@Path("job_no") String jobNo);

    @GET("product")
    Call<List<product>> getProduct();

    @GET("getversion/AndriodV-{versionCode}")
    Call<VersionRes> checkVersion(@Path("versionCode") String versionCode);


    @GET("savesignature/{jobNo},{fileName}")
    Call<SaveSignatureRes> savesignature(@Path("jobNo") String jobNo, @Path("fileName") String fileName);
}
