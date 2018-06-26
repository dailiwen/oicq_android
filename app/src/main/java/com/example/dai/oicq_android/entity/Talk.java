package com.example.dai.oicq_android.entity;

/**
 * @author dailiwen
 * @date 2018/06/23
 */
public class Talk {
    /**
     * 消息类型：1为接收到的，2为发送的
     */
    Integer type;
    /**
     * 消息内容
     */
    String message;

    public Talk() {
    }

    public Talk(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
