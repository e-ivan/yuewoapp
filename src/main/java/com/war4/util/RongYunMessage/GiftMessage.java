package com.war4.util.RongYunMessage;

import com.google.gson.GsonBuilder;

/**
 * Created by Administrator on 2016/12/12.
 */
public class GiftMessage extends BaseMessage {
    private String name = "";
    private String data = "";
    private transient static final String TYPE = "QNYY:GIFT";

    public GiftMessage(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this, GiftMessage.class);
    }

    /**
     * 获取命令名称，可以自行定义
     *
     * @returnString
     */
    public String getName() {
        return name;
    }

    /**
     * 设置命令名称，可以自行定义
     *
     * @return
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取命令的内容
     *
     * @returnString
     */
    public String getData() {
        return data;
    }

    /**
     * 设置命令的内容
     *
     * @return
     */
    public void setData(String data) {
        this.data = data;
    }

}
