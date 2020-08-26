package com.example.c_1;

public class Fruit {
    // Attributes
    private String botname;
    private String climate;
    private String description;
    private String health;
    private String imageurl;
    private String[] othname;
    private String propagation;
    private String soil;
    private String tfvname;

    // Constructors
    public Fruit(String botname, String climate, String description, String health, String imageurl, String[] othname, String propagation, String soil, String tfvname, String uses) {
        this.botname = botname;
        this.climate = climate;
        this.description = description;
        this.health = health;
        this.imageurl = imageurl;
        this.othname = othname;
        this.propagation = propagation;
        this.soil = soil;
        this.tfvname = tfvname;
        this.uses = uses;
    }
    public String getAllOthname() {
        String resposta = "";
        for(int i = 0; i < this.othname.length - 1; i++){
            resposta += this.othname[i] + ",";
        }
        resposta += this.othname[this.othname.length - 1];
//        if(resposta.length() > 27){
//            return resposta + "...";
//        }
        return resposta;
    }

    // Getters ans setters
    public String getBotname() {
        return botname;
    }

    public void setBotname(String botname) {
        this.botname = botname;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String[] getOthname() {
        return othname;
    }

    public void setOthname(String[] othname) {
        this.othname = othname;
    }

    public String getPropagation() {
        return propagation;
    }

    public void setPropagation(String propagation) {
        this.propagation = propagation;
    }

    public String getSoil() {
        return soil;
    }

    public void setSoil(String soil) {
        this.soil = soil;
    }

    public String getTfvname() {
        return tfvname;
    }

    public void setTfvname(String tfvname) {
        this.tfvname = tfvname;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }


    private String uses;
}
