package com.liuwan.mydesign.bean;

/**
 * Created by liuwan on 2016/12/1.
 * 消息集合
 */
public class MessageList {
    // 消息id
    private String id;
    // 消息标题
    private String title;
    // 消息类型
    private String type;
    // 发送时间
    private String sendTime;
    // 是否已阅读
    private String isRead;
    // 是否选中
    private boolean isSelected = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public MessageList() {
        super();
    }

    public MessageList(String id, String title, String type, String sendTime, String isRead, boolean isSelected) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.sendTime = sendTime;
        this.isRead = isRead;
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return "MessageList{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", isRead='" + isRead + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

}
