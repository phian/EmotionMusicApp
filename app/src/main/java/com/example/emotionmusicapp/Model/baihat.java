package com.example.emotionmusicapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class baihat {

    @SerializedName("ID_baihat")
    @Expose
    private String iDBaihat;
    @SerializedName("ID_chude")
    @Expose
    private String iDChude;
    @SerializedName("Tenbaihat")
    @Expose
    private String tenbaihat;
    @SerializedName("Casi")
    @Expose
    private String casi;
    @SerializedName("Linkbaihat")
    @Expose
    private String linkbaihat;

    public String getIDBaihat() {
        return iDBaihat;
    }

    public void setIDBaihat(String iDBaihat) {
        this.iDBaihat = iDBaihat;
    }

    public String getIDChude() {
        return iDChude;
    }

    public String getiDBaihat() {
        return iDBaihat;
    }

    public void setiDBaihat(String iDBaihat) {
        this.iDBaihat = iDBaihat;
    }

    public String getiDChude() {
        return iDChude;
    }

    public void setiDChude(String iDChude) {
        this.iDChude = iDChude;
    }

    public void setIDChude(String iDChude) {
        this.iDChude = iDChude;
    }

    public String getTenbaihat() {
        return tenbaihat;
    }

    public void setTenbaihat(String tenbaihat) {
        this.tenbaihat = tenbaihat;
    }

    public String getCasi() {
        return casi;
    }

    public void setCasi(String casi) {
        this.casi = casi;
    }

    public String getLinkbaihat() {
        return linkbaihat;
    }

    public void setLinkbaihat(String linkbaihat) {
        this.linkbaihat = linkbaihat;
    }
}
