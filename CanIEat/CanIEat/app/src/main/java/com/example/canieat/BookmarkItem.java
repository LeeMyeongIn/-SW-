package com.example.canieat;

public class BookmarkItem {
    public String barcode;
    public String imageUrl;
    public String name;
    public String allergy;
    public String nutrition;
    public boolean isPlaceholderImage;

    public BookmarkItem() {}

    public BookmarkItem(String barcode, String imageUrl, String name, String allergy, String nutrition) {
        this.barcode = barcode;
        this.imageUrl = imageUrl;
        this.name = name;
        this.allergy = allergy;
        this.nutrition = nutrition;
        this.isPlaceholderImage = false;
    }
}
