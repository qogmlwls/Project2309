package com.example.project2309.data;

import java.text.DecimalFormat;

public class TagPost {


    public int tagPostId;
    int count;

    public String postCount;

    public String tagName;

    public TagPost(){

    }

    public void setCount(int count) {

        this.count = count;
        // 숫자 포맷 지정 (천 단위마다 쉼표 추가하는 패턴)
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        // int 값을 문자열로 변환한 후 숫자 포맷 적용하여 문자열로 변환
        postCount = "게시글 "+decimalFormat.format(count) + "개";
    }

    public int getCount() {
        return count;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = "#"+tagName;
    }

}
