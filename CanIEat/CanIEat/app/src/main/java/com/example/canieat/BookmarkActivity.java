package com.example.canieat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "Bookmarks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = findViewById(R.id.recycler_bookmarks);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<BookmarkItem> bookmarkItems = loadBookmarks();

        adapter = new BookmarkAdapter(bookmarkItems, this::removeBookmark);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            // 북마크 클릭 → ResultActivity로 이동
            Intent intent = new Intent(BookmarkActivity.this, ResultActivity.class);
            intent.putExtra("barcode", item.barcode);
            intent.putExtra("name", item.name);
            intent.putExtra("imageUrl", item.imageUrl);
            intent.putExtra("allergy", item.allergy);
            intent.putExtra("nutrition", item.nutrition);
            startActivity(intent);
        });
    }

    private List<BookmarkItem> loadBookmarks() {
        String json = prefs.getString("bookmarks", "[]");
        Type listType = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    private void removeBookmark(BookmarkItem item) {
        List<BookmarkItem> bookmarks = loadBookmarks();
        bookmarks.removeIf(bookmark -> bookmark.barcode.equals(item.barcode));
        saveBookmarks(bookmarks);
        adapter.updateList(bookmarks);
    }

    private void saveBookmarks(List<BookmarkItem> bookmarks) {
        String json = new Gson().toJson(bookmarks);
        prefs.edit().putString("bookmarks", json).apply();
    }
}
