package com.qmr777.exchange.model;

import java.util.List;

/**
 * Created by qmr on 2016/4/24.
 */
public class MyBook {

    /**
     * errcode : 0
     * data : [{"title":"20-04-21 07:06:48.0"}]
     */

    private int errcode;
    /**
     * title : 2015全国硕士研究生入学统一考试思想政核心考点
     * medium_img : http://img3.douban.com/mpic/s27573314.jpg
     * isbn13 : 9787548607755
     * datetime : 2016-04-23 00:37:39.0
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
        private String datetime;

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

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }
    }
}
