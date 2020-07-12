package com.sgj.microschoolsystem.model.bo;

public class PyqRequest {
    private String poster;
    private String msg;
    private String imgData;

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    @Override
    public String toString() {
        return "PyqRequest{" +
                "poster='" + poster + '\'' +
                ", msg='" + msg + '\'' +
                ", imgData='" + imgData + '\'' +
                '}';
    }
}
