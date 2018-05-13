package com.mb.smartfridge.entity;

/**
 * @author chenqm on 2017/7/14.
 */

public class DrawerlayoutEntity {

    private int img ;
    private String text;

    public DrawerlayoutEntity(int img, String text) {
        this.img = img;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

}
