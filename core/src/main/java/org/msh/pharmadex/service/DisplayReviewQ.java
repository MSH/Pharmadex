package org.msh.pharmadex.service;

import java.util.List;

/**
 * Created by utkarsh on 12/4/14.
 */
public class DisplayReviewQ {

    private String header1;

    private List<Header2> header2s;

    public DisplayReviewQ(String header1, List<Header2> header2s) {
        this.header1 = header1;
        this.header2s = header2s;
    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public List<Header2> getHeader2s() {
        return header2s;
    }

    public void setHeader2s(List<Header2> header2s) {
        this.header2s = header2s;
    }


    public class Header2 {
        private String header2;

        private List<DisplayReviewInfo> displayReviewInfos;

        public Header2(String header2, List<DisplayReviewInfo> questions) {
            this.header2 = header2;
            this.displayReviewInfos = questions;
        }

        public String getHeader2() {
            return header2;
        }

        public void setHeader2(String header2) {
            this.header2 = header2;
        }

        public List<DisplayReviewInfo> getDisplayReviewInfos() {
            return displayReviewInfos;
        }

        public void setDisplayReviewInfos(List<DisplayReviewInfo> displayReviewInfos) {
            this.displayReviewInfos = displayReviewInfos;
        }
    }
}
