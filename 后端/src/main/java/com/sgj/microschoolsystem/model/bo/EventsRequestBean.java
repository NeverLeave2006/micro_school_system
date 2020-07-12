package com.sgj.microschoolsystem.model.bo;

public class EventsRequestBean {


    private String name;

    private String posterid;

    private String content;

    private String url;

    private String imgData;

    private String schoolName;

    private String dept;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPosterid() {
        return posterid;
    }

    public void setPosterid(String posterid) {
        this.posterid = posterid == null ? null : posterid.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName == null ? null : schoolName.trim();
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    @Override
    public String toString() {
        return "EventsRequestBean{" +
                "name='" + name + '\'' +
                ", posterid='" + posterid + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", imgData='" + imgData + '\'' +
                ", schoolName='" + schoolName + '\'' +
                '}';
    }
}