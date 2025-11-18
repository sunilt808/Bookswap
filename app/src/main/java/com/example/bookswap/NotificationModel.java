package com.example.bookswap;

public class NotificationModel {
    private String title;
    private String time;
    private int icon;

    public NotificationModel(String title, String time, int icon) {
        this.title = title;
        this.time = time;
        this.icon = icon;
    }

    public String getTitle() { return title; }
    public String getTime() { return time; }
    public int getIcon() { return icon; }
}
