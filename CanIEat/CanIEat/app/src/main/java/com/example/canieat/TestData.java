package com.example.canieat;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TestData {

    private static final String PREFS_NAME = "Bookmarks";

    public static void insertSampleBookmarks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString("bookmarks", "[]");

        Type listType = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        List<BookmarkItem> bookmarks = new Gson().fromJson(json, listType);

        List<BookmarkItem> sample = getSampleBookmarks();

        // 중복 방지
        for (BookmarkItem item : sample) {
            boolean exists = false;
            for (BookmarkItem saved : bookmarks) {
                if (saved.barcode.equals(item.barcode)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                bookmarks.add(item);
            }
        }

        prefs.edit().putString("bookmarks", new Gson().toJson(bookmarks)).apply();
    }

    public static List<BookmarkItem> getSampleBookmarks() {
        List<BookmarkItem> bookmarks = new ArrayList<>();

        bookmarks.add(new BookmarkItem(
                "8809581907492210",
                "http://fresh.haccp.or.kr/prdimg/2017/20170275045106/20170275045106-1.jpg",
                "한끼초밥불초밥",
                "쇠고기,돼지고기,대두,우유,밀,토마토,조개류(굴)",
                "열량 285kcal, 탄수화물 40g, 단백질 9g, 지방 8g, 포화지방 2g, 나트륨 820mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8801128280020",
                "http://fresh.haccp.or.kr/prdimg/2016/2016063409563/2016063409563-1.jpg",
                "그랜드 젤리",
                "우유,대두,돼지고기",
                "열량 120kcal, 탄수화물 26g, 단백질 1g, 지방 0.5g, 나트륨 50mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8801117108915",
                "http://fresh.haccp.or.kr/prdimg/1987/19870415003205/19870415003205-1.jpg",
                "미쯔블랙",
                "밀, 우유, 대두, 계란 함유",
                "열량 208kcal, 탄수화물 31g, 당류 14g, 단백질 3g, 지방 8g, 포화지방 4g, 나트륨 120mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8801117760304",
                "http://fresh.haccp.or.kr/prdimg/1987/19870415003246/19870415003246-1.jpg",
                "포카칩어니언맛",
                "우유,대두,밀 함유",
                "열량 377kcal, 탄수화물 35g, 당류 1g, 단백질 3g, 지방 25g, 포화지방 8g, 나트륨 250mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8809260942172",
                "http://fresh.haccp.or.kr/prdimg/2014/2014033246814/2014033246814-1.jpg",
                "미니 프레첼 볶음양념맛",
                "밀, 쇠고기, 새우, 오징어, 우유, 대두",
                "열량 133kcal, 탄수화물 19g, 당류 1g, 단백질 3g, 지방 5g, 포화지방 2.2g, 나트륨 340mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8801043033787",
                "http://fresh.haccp.or.kr/prdimg/1986/19860360007322/19860360007322-1.jpg",
                "맛짬뽕",
                "밀,대두,계란,우유,게,새우,돼지고기,닭고기,쇠고기,오징어,조개류(굴,전복,홍합 포함) 함유",
                "열량 575kcal, 탄수화물 87g, 당류 6g, 단백질 9g, 지방 21g, 포화지방 9g, 나트륨 1780mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8801019308130",
                "http://fresh.haccp.or.kr/prdimg/1984/1984022100896/1984022100896-1.jpg",
                "홈런볼초코",
                "우유,땅콩,계란,밀,쇠고기,대두 함유",
                "열량 290kcal, 탄수화물 26g, 당류 17g, 단백질 4g, 지방 19g, 포화지방 7g, 나트륨 75mg"
        ));

        bookmarks.add(new BookmarkItem(
                "880117279905",
                "http://fresh.haccp.or.kr/prdimg/1987/19870415003118/19870415003118-1.jpg",
                "고소미",
                "밀, 우유, 조개류(굴), 쇠고기 함유",
                "열량 220kcal, 탄수화물 25g, 당류 10g, 단백질 3g, 지방 12g, 포화지방 5g, 나트륨 120mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8809189922200",
                "http://fresh.haccp.or.kr/prdimg/2014/20140383299106/20140383299106-1.jpg",
                "담백한 통감자",
                "밀,대두,우유,쇠고기,새우,오징어함유",
                "열량 536kcal, 탄수화물 64g, 당류 2g, 단백질 2g, 지방 30g, 포화지방 15g, 나트륨 703mg"
        ));

        bookmarks.add(new BookmarkItem(
                "8801128280020",
                "http://fresh.haccp.or.kr/prdimg/2016/2016063409563/2016063409563-1.jpg",
                "그랜드 젤리",
                "우유,대두,돼지고기",
                "열량 120kcal, 탄수화물 26g, 단백질 1g, 지방 0.5g, 나트륨 50mg"
        ));

        return bookmarks;
    }
}
