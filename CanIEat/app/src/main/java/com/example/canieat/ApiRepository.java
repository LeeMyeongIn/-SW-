package com.example.canieat;

import android.content.res.AssetManager;
import android.util.Log;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ApiRepository {

    private static final String TAG = "ApiRepository";
    private static final String HACCP_API_BASE_URL = "https://apis.data.go.kr/B553748/CertImgListServiceV3/";
    private static final String FOOD_API_BASE_URL = "http://openapi.foodsafetykorea.go.kr/api/";
    private static final String HACCP_API_KEY = "2yOhkaGbxj7omjsqahFXQsQq2QLaH6+43gOiCSZR95TIBrbmHJmBFTb3onKdbjfgz+YiKZ4/tOPUVWZHheRjmg==";
    private static final String FOOD_API_KEY = "59f1429b097f4309995b";

    private static final Retrofit retrofitHaccp = new Retrofit.Builder()
            .baseUrl(HACCP_API_BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    private static final Retrofit retrofitFood = new Retrofit.Builder()
            .baseUrl(FOOD_API_BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    private static final HaccpApiService haccpApiService = retrofitHaccp.create(HaccpApiService.class);
    private static final FoodBarcodeApiService foodApiService = retrofitFood.create(FoodBarcodeApiService.class);

    public static void getProductInfo(String barcode, ApiCallback<Product> callback) {
        Log.d(TAG, "1. HACCP API → BAR_CD 검색 시도: " + barcode);

        Call<HaccpResponse> call = haccpApiService.getProductByBarcode(HACCP_API_KEY, "xml", "1", "5", barcode);
        call.enqueue(new Callback<HaccpResponse>() {
            @Override
            public void onResponse(Call<HaccpResponse> call, Response<HaccpResponse> response) {
                Log.d(TAG, "HACCP 응답 수신 완료, 성공 여부: " + response.isSuccessful());

                if (isValidHaccpResponse(response)) {
                    HaccpResponse.Item item = response.body().body.items.get(0);
                    Log.d(TAG, "HACCP 제품 정보 성공: " + item.prdlstNm);
                    callback.onResult(toProduct(item, barcode));
                } else {
                    Log.w(TAG, "HACCP BAR_CD 검색 실패 → 식약처 API로 전환");
                    fallbackToFoodApi(barcode, callback);
                }
            }

            @Override
            public void onFailure(Call<HaccpResponse> call, Throwable t) {
                Log.e(TAG, "HACCP API 연결 실패: " + t.getMessage(), t);
                fallbackToFoodApi(barcode, callback);
            }
        });
    }

    private static void fallbackToFoodApi(String barcode, ApiCallback<Product> callback) {
        Log.w(TAG, "2. 식약처 API로 fallback 시작");

        Call<FoodBarcodeResponse> call = foodApiService.getProductByBarcode(FOOD_API_KEY, "C005", "xml", "1", "5", barcode);
        call.enqueue(new Callback<FoodBarcodeResponse>() {
            @Override
            public void onResponse(Call<FoodBarcodeResponse> call, Response<FoodBarcodeResponse> response) {
                Log.d(TAG, "식약처 응답 수신 완료, 성공 여부: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null &&
                        response.body().body != null &&
                        response.body().body.items != null &&
                        !response.body().body.items.isEmpty()) {

                    String productName = response.body().body.items.get(0).prdlstNm;
                    Log.d(TAG, "식약처 제품명 검색 성공 → HACCP 제품명 검색 시도: " + productName);

                    haccpApiService.getProductByName(HACCP_API_KEY, "xml", "1", "5", productName)
                            .enqueue(new Callback<HaccpResponse>() {
                                @Override
                                public void onResponse(Call<HaccpResponse> call, Response<HaccpResponse> response) {
                                    if (isValidHaccpResponse(response)) {
                                        HaccpResponse.Item item = response.body().body.items.get(0);
                                        Log.d(TAG, "HACCP 제품명 검색 성공");
                                        callback.onResult(toProduct(item, barcode));
                                    } else {
                                        Log.w(TAG, "HACCP 제품명 검색 실패 → 엑셀 조회 시도");
                                        findProductFromExcel(barcode, callback);
                                    }
                                }

                                @Override
                                public void onFailure(Call<HaccpResponse> call, Throwable t) {
                                    Log.e(TAG, "HACCP 제품명 검색 실패 → 엑셀 조회 시도", t);
                                    findProductFromExcel(barcode, callback);
                                }
                            });

                } else {
                    Log.w(TAG, "식약처 응답 실패 또는 결과 없음 → 엑셀 조회 시도");
                    findProductFromExcel(barcode, callback);
                }
            }

            @Override
            public void onFailure(Call<FoodBarcodeResponse> call, Throwable t) {
                Log.e(TAG, "식약처 API 연결 실패 → 엑셀 조회 시도", t);
                findProductFromExcel(barcode, callback);
            }
        });
    }

    private static void findProductFromExcel(String barcode, ApiCallback<Product> callback) {
        try {
            AssetManager assetManager = MyApp.getContext().getAssets();
            InputStream is = assetManager.open("api_데이터_합본.xlsx");
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                for (int colIdx : new int[]{1, 2, 3, 4, 8}) {
                    Cell cell = row.getCell(colIdx);
                    if (cell != null) {
                        String cellValue = getFormattedCellValue(cell);
                        if (barcode.equals(cellValue)) {
                            String name = getCellString(row, 0);
                            String image = getCellString(row, 5);
                            String allergy = getCellString(row, 6);
                            String nutrition = getCellString(row, 7);

                            Log.d(TAG, "엑셀 제품 정보 매칭 완료: " + name);

                            Product product = new Product(name, image, allergy, nutrition, barcode);
                            callback.onResult(product);
                            workbook.close();
                            return;
                        }
                    }
                }
            }

            workbook.close();
            callback.onResult(null);

        } catch (Exception e) {
            Log.e(TAG, "엑셀 대체 검색 실패: " + e.getMessage(), e);
            callback.onResult(null);
        }
    }

    private static String getFormattedCellValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue()).trim();
        } else {
            return cell.toString().trim();
        }
    }

    private static String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell != null) ? cell.toString().trim() : "";
    }

    private static boolean isValidHaccpResponse(Response<HaccpResponse> response) {
        return response.isSuccessful() &&
                response.body() != null &&
                response.body().body != null &&
                response.body().body.items != null &&
                !response.body().body.items.isEmpty();
    }

    private static Product toProduct(HaccpResponse.Item item, String barcode) {
        return new Product(
                item.prdlstNm != null ? item.prdlstNm : "",
                item.imgurl1 != null ? item.imgurl1 : "",
                item.allergy != null ? item.allergy : "",
                "",
                barcode
        );
    }

    public interface ApiCallback<T> {
        void onResult(T result);
    }
}
