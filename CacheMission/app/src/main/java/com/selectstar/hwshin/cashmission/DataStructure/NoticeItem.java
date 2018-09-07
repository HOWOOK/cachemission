package com.selectstar.hwshin.cashmission.DataStructure;

public class NoticeItem {

    private String title;
    private String date;
    private String content;

    public String getTitle(){return title;}
    public String getDate(){return date;}
    public String getContent(){return content;}


    public String setTitle(String title) { return this.title = title; }
    public String setDate(String date) { return this.date = date; }
    public String setContent(String content) { return this.content = content ;}
    public NoticeItem(String title, String date, String content){
        this.title = title;
        this.date = date;
        this.content = content;
    }

}