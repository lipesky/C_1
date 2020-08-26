package com.example.c_1;

import android.graphics.Bitmap;
import android.view.View;

public class FruitPreview{
    // Attributes
   private String botname;
    private String imageurl;
    private String[] othname;
    private String tfvname;

    private Bitmap imageBitmap;

    // Constructors
    protected FruitPreview(String botname, String imageurl, String[] othname, String tfvname) {
        this.botname = botname;
        this.imageurl = imageurl;
        this.othname = othname;
        this.tfvname = tfvname;
    }

    protected String getAllOthname() {
        String resposta = "";
        for(int i = 0; i < this.othname.length - 1; i++){
            resposta += this.othname[i] + ",";
        }
        resposta += this.othname[this.othname.length - 1];
        if(resposta.length() > 27){
            return (resposta.substring(0, 26) + "...");
        }
        return resposta;
    }

    // Getters and setters
    protected Bitmap getImageBitmap() {
        return imageBitmap;
    }

    protected void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    protected String getBotname() {
        return botname;
    }

    private void setBotname(String botname) {
        this.botname = botname;
    }

    protected String getImageurl() {
        return imageurl;
    }

    private void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    protected String[] getOthname() {
        return othname;
    }

    private void setOthname(String[] othname) {
        this.othname = othname;
    }

    protected String getTfvname() {
        return tfvname;
    }

    private void setTfvname(String tfvname) {
        this.tfvname = tfvname;
    }
}
