package com.example.romanpc.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRequests {

    @GET("/get_pits_by_region_name.php")
    Call<ResponseBody> getPitsByRegionName(@Query("region") String regionName);
}
