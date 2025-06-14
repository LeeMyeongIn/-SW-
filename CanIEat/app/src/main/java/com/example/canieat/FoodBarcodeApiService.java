package com.example.canieat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FoodBarcodeApiService {
    @GET("C005/xml")
    Call<FoodBarcodeResponse> getProductByBarcode(
            @Query("serviceKey") String serviceKey,
            @Query("serviceId") String serviceId,
            @Query("type") String type,
            @Query("pageNo") String pageNo,
            @Query("numOfRows") String numOfRows,
            @Query("bar_code") String barcode
    );
}

