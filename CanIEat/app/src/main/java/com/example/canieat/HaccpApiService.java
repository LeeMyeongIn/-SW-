package com.example.canieat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HaccpApiService {

    @GET("getCertImgList")
    Call<HaccpResponse> getProductByBarcode(
            @Query("serviceKey") String serviceKey,
            @Query("returnType") String returnType,
            @Query("pageNo") String pageNo,
            @Query("numOfRows") String numOfRows,
            @Query("BAR_CD") String barcode
    );

    @GET("getCertImgList")
    Call<HaccpResponse> getProductByName(
            @Query("serviceKey") String key,
            @Query("type") String type,
            @Query("pageNo") String pageNo,
            @Query("numOfRows") String numOfRows,
            @Query("prdlstNm") String productName
    );
}
