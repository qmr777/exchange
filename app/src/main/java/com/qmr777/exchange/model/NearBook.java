package com.qmr777.exchange.model;

import java.util.List;

/**
 * Created by qmr on 2016/4/21.
 */
public class NearBook {

    /**
     * errcode : 0
     * data : [{"title":"设计模式","medium_img":"http://img3.douban.com/mpic/s1074361.jpg","isbn13":"9787111075752"},{"title":"设计模式","medium_img":"http://img3.douban.com/mpic/s1074361.jpg","isbn13":"9787111075752"},{"title":"第一行代码","medium_img":"http://img3.douban.com/mpic/s28351121.jpg","isbn13":"9787115362865"},{"title":"第一行代码","medium_img":"http://img3.douban.com/mpic/s28351121.jpg","isbn13":"9787115362865"},{"title":"Android开发艺术探索","medium_img":"http://img3.douban.com/mpic/s28283341.jpg","isbn13":"9787121269394"},{"title":"Android开发艺术探索","medium_img":"http://img3.douban.com/mpic/s28283341.jpg","isbn13":"9787121269394"},{"title":"C语言程序设计教程","medium_img":"http://img3.douban.com/mpic/s26584205.jpg","isbn13":"9787040199109"},{"title":"C语言程序设计教程","medium_img":"http://img3.douban.com/mpic/s26584205.jpg","isbn13":"9787040199109"}]
     */

    private int errcode;
    /**
     * title : 设计模式
     * medium_img : http://img3.douban.com/mpic/s1074361.jpg
     * isbn13 : 9787111075752
     */

    private List<DataBean> data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String title;
        private String medium_img;
        private String isbn13;

        public String getPublish_id() {
            return publish_id;
        }

        public void setPublish_id(String publish_id) {
            this.publish_id = publish_id;
        }

        private String publish_id;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMedium_img() {
            return medium_img;
        }

        public void setMedium_img(String medium_img) {
            this.medium_img = medium_img;
        }

        public String getIsbn13() {
            return isbn13;
        }

        public void setIsbn13(String isbn13) {
            this.isbn13 = isbn13;
        }
    }
}
