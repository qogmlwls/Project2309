package com.example.project2309.data;

import java.util.ArrayList;
import java.util.List;

public class Comment {


    public List<Comment> replies;

    public int parent;

    public int comment_id;
    public int user_id;
    public String user_name;
    public String user_profile;


    public String content;

    public int Reply_comment_cnt;

    public boolean isMyComment;


    public String create_date;
    public String update_date;

    public boolean ReplyViewVisibility;

    public boolean more_replyComment;


    public Comment(){
        ReplyViewVisibility = false;
        replies = new ArrayList<>();
        more_replyComment = false;
        parent = -1;
    }
}
