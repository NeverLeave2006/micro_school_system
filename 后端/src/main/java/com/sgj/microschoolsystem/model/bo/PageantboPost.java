package com.sgj.microschoolsystem.model.bo;

public class PageantboPost {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pageant.poster
     *
     * @mbg.generated Mon May 13 21:35:53 CST 2019
     */
    private String poster;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pageant.img
     *
     * @mbg.generated Mon May 13 21:35:53 CST 2019
     */
    private String img;

    private String schoolName;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pageant.poster
     *
     * @return the value of pageant.poster
     *
     * @mbg.generated Mon May 13 21:35:53 CST 2019
     */
    public String getPoster() {
        return poster;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pageant.poster
     *
     * @param poster the value for pageant.poster
     *
     * @mbg.generated Mon May 13 21:35:53 CST 2019
     */
    public void setPoster(String poster) {
        this.poster = poster == null ? null : poster.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pageant.img
     *
     * @return the value of pageant.img
     *
     * @mbg.generated Mon May 13 21:35:53 CST 2019
     */
    public String getImg() {
        return img;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pageant.img
     *
     * @param img the value for pageant.img
     *
     * @mbg.generated Mon May 13 21:35:53 CST 2019
     */
    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public String toString() {
        return "PageantboPost{" +
                "poster='" + poster + '\'' +
                ", img='" + img + '\'' +
                ", schoolName='" + schoolName + '\'' +
                '}';
    }
}