package com.talib.youtuberx.youtubemy;

public class Category {
    private String ad;
    private String sekil;
    private String Button;

    public Category() {
    }

    public Category(String ad, String sekil, String button) {
        this.ad = ad;
        this.sekil = sekil;
        Button = button;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSekil() {
        return sekil;
    }

    public void setSekil(String sekil) {
        this.sekil = sekil;
    }



    public String getButton() {
        return Button;
    }

    public void setButton(String button) {
        Button = button;
    }
}
