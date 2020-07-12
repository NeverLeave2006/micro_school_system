package com.sgj.microschoolsystem.model.bo;

public class PageantListAll {

    private String poster;

    private String name;

    private String img;

    private Integer likeCount;

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public String toString() {
        return "PageantListAll{" +
                "name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", likeCount='" + likeCount + '\'' +
                '}';
    }
}