package com.example.bookswap;

public class BookModel {

    private String title;
    private String author;
    private String imageUri;        // store real image URI
    private String category;
    private String phone;
    private String email;
    private int id;  // add this

    public BookModel(String title, String author, String imageUri, String category, String phone, String email) {
        this.title = title;
        this.author = author;
        this.imageUri = imageUri;
        this.category = category;
        this.phone = phone;
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getCategory() {
        return category;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
