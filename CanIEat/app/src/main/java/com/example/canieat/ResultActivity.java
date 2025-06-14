package com.example.canieat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private ImageView bookmarkButton;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "Bookmarks";

    private String barcode = "";
    private String name = "", imageUrl = "", allergy = "", nutrition = "";
    private boolean isBookmarked = false;

    private String simplifyNutrition(String original) {
        String preprocessed = original.replaceAll("(\\d),(\\d)", "$1$2");

        String[] parts = preprocessed.split("\\s+");
        StringBuilder sb = new StringBuilder();
        sb.append("[영양정보]<br>");

        List<String> keywords = List.of(
                "열량", "탄수화물", "당류", "단백질",
                "지방", "포화지방", "트랜스지방", "콜레스테롤",
                "나트륨", "칼슘"
        );

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            // 불필요한 문구 필터링
            if (part.contains("영양소기준치") || part.contains("기준치") || part.contains("다를 수")) continue;
            if (part.contains("총") && part.contains("내용량")) continue;
            if (part.contains("1봉지") || part.contains("제공량")) continue;
            if (part.contains("기준이므로") || part.contains("개인의") || part.contains("필요") || part.contains("열량에")) continue;

            if (keywords.contains(part)) {
                // 키워드 다음 항목이 수치일 경우 붙이기
                if (i + 1 < parts.length) {
                    String value = parts[i + 1].trim();
                    sb.append(part).append(" ").append(value).append("<br>");
                    i++;
                } else {
                    sb.append(part).append("<br>");
                }
            }
        }

        return sb.toString().trim().equals("[영양정보]<br>") ? "[영양정보 없음]" : sb.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        barcode = getIntent().getStringExtra("barcode");
        name = getIntent().getStringExtra("name");
        imageUrl = getIntent().getStringExtra("imageUrl");
        allergy = getIntent().getStringExtra("allergy");
        nutrition = getIntent().getStringExtra("nutrition");

        TextView nameText = findViewById(R.id.text_name);
        TextView allergyText = findViewById(R.id.text_allergy);
        TextView nutritionText = findViewById(R.id.text_nutrition);
        TextView errorText = findViewById(R.id.text_error);
        ImageView productImage = findViewById(R.id.image_product);
        Button retryButton = findViewById(R.id.button_retry);
        bookmarkButton = findViewById(R.id.bookmarkButton);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean hasNoData = (name == null || name.isEmpty())
                && (allergy == null || allergy.isEmpty())
                && (nutrition == null || nutrition.isEmpty());

        if (hasNoData) {
            nameText.setVisibility(TextView.GONE);
            allergyText.setVisibility(TextView.GONE);
            nutritionText.setVisibility(TextView.GONE);
            productImage.setVisibility(ImageView.GONE);
            errorText.setVisibility(TextView.VISIBLE);
            errorText.setText("⚠️제품 정보 없음!⚠️");

        } else {
            errorText.setVisibility(TextView.GONE);
            nameText.setVisibility(TextView.VISIBLE);
            allergyText.setVisibility(TextView.VISIBLE);
            nutritionText.setVisibility(TextView.VISIBLE);
            productImage.setVisibility(ImageView.VISIBLE);

            nameText.setText("제품명: " + name);

            if (!allergy.isEmpty()) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                String fullText = "⚠️ 알레르기 주의!⚠️\n" + allergy;
                builder.append(fullText);
                builder.setSpan(new RelativeSizeSpan(1.2f), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                allergyText.setText(builder);
            } else {
                allergyText.setText("⚠️ 알레르기 정보 없음!");
            }

            if (!nutrition.isEmpty()) {
                String simplified = simplifyNutrition(nutrition);
                nutritionText.setText(Html.fromHtml(simplified));
            } else {
                nutritionText.setText("[영양정보 없음]");
            }

            if (!imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.humm)
                        .error(R.drawable.humm)
                        .into(productImage);
            } else {
                Glide.with(this)
                        .load(R.drawable.humm)
                        .into(productImage);
            }
        }

        retryButton.setVisibility(Button.GONE);

        isBookmarked = checkBookmarkStatus(barcode);
        updateBookmarkIcon();

        bookmarkButton.setOnClickListener(v -> {
            if (isBookmarked) {
                removeBookmark(barcode);
            } else {
                saveToBookmarks(barcode, imageUrl, name, allergy, nutrition);
            }
            isBookmarked = !isBookmarked;
            updateBookmarkIcon();
        });

        bookmarkButton.setOnLongClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, BookmarkActivity.class));
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBookmarked = checkBookmarkStatus(barcode);
        updateBookmarkIcon();
    }

    private void updateBookmarkIcon() {
        if (isBookmarked) {
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_selected);
        } else {
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_unselected);
        }
    }

    private boolean checkBookmarkStatus(String barcode) {
        String json = prefs.getString("bookmarks", "[]");
        Type listType = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        List<BookmarkItem> bookmarks = new Gson().fromJson(json, listType);
        for (BookmarkItem item : bookmarks) {
            if (barcode != null && barcode.equals(item.barcode)) {
                return true;
            }
        }
        return false;
    }

    private void saveToBookmarks(String barcode, String imageUrl, String name, String allergy, String nutrition) {
        String json = prefs.getString("bookmarks", "[]");
        Type listType = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        List<BookmarkItem> bookmarks = new Gson().fromJson(json, listType);

        for (BookmarkItem item : bookmarks) {
            if (barcode.equals(item.barcode)) return;
        }

        BookmarkItem newItem = new BookmarkItem(barcode, imageUrl, name, allergy, nutrition);
        newItem.isPlaceholderImage = (imageUrl == null || imageUrl.isEmpty() || imageUrl.toLowerCase().contains("humm"));

        bookmarks.add(newItem);
        prefs.edit().putString("bookmarks", new Gson().toJson(bookmarks)).apply();
    }

    private void removeBookmark(String barcode) {
        String json = prefs.getString("bookmarks", "[]");
        Type listType = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        List<BookmarkItem> bookmarks = new Gson().fromJson(json, listType);

        bookmarks.removeIf(item -> barcode.equals(item.barcode));
        prefs.edit().putString("bookmarks", new Gson().toJson(bookmarks)).apply();
    }
}
