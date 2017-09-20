package com.liuwan.mydesign.bean;

/**
 * Created by liuwan on 2016/12/1.
 * 消息详情
 */
public class MessageDetial {
    // 消息id
    private String id;
    // 消息类型枚举值
    private String type;
    // 消息标题
    private String title;
    // 消息内容
    private String content;
    // 订单id
    private String orderId;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MessageDetial() {
        super();
    }

    public MessageDetial(String id, String type, String title, String content, String orderId) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "MessageDetial{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }

}
