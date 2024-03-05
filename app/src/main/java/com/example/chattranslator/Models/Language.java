package com.example.chattranslator.Models;

import android.graphics.Bitmap;

public class Language {
    private String name;
    private String code;
    private Bitmap flag;


    public Language()
    {

    }
    public Language(String name)
    {
        this.name = name;
    }

    public Bitmap getFlag() {
        return flag;
    }

    public void setFlag(Bitmap flag) {
        this.flag = flag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
