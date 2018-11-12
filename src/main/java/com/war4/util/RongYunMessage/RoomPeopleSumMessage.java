package com.war4.util.RongYunMessage;

import com.google.gson.GsonBuilder;

/**
 * Created by 805801525@qq.com on 2017/3/3.
 */
public class RoomPeopleSumMessage extends BaseMessage {
    private String name = "";
    private String data = "";
    private transient static final String TYPE = "QNYY:PEOPLE";

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String toString() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this, RoomPeopleSumMessage.class);
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
