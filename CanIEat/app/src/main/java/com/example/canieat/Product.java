package com.example.canieat;

public class Product {
    public String name;
    public String imageUrl;
    public String allergy;
    public String nutrition;
    public String barcode;

    public Product(String name, String imageUrl, String allergy, String nutrition, String barcode) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.allergy = allergy;
        this.nutrition = nutrition;
        this.barcode = barcode;
    }
}
