package com.war4.util;

/**
 * 用户状态类，记录用户在平台使用系统中所有的状态。
 */
public class BitStateUtil {
    public final static Long OP_BIND_PHONE = 1L << 0; // 用户绑定手机状态码
    public final static Long OP_BIND_EMAIL = 1L << 1; // 用户绑定邮箱
    public final static Long OP_HAS_AUCTION_PROCESS = 1L << 2; // 用户是否有竞拍在进行
    public final static Long OP_HAS_VIDEO_UPLOAD = 1L << 3; //用户是否上传个人视频
    //    public final static Long OP_HAS_VIDEO_PASS = 1L <<4; //用户是否通过个人视频
    public final static Long OP_HAS_CASH_PROCESS = 1L << 5; //用户是否有提现在进行
    public final static Long OP_BIND_CASH_ACCOUNT = 1L << 6; //用户是否绑定提现账户
    public final static Long OP_HAS_AUTHENTICATION_SUBMIT = 1L << 7; //用户是否提交实名认证
    public final static Long OP_HAS_AUTHENTICATION_PASS = 1L << 8; //用户是否通过实名认证
    public final static Long OP_HAS_INTERNETSTAR_PASS = 1L << 9; //用户是否通过成为网红
    public final static Long OP_HAS_PAY_PASSWORD = 1L << 10; //用户是否有支付密码
    public final static Long OP_HAS_STAR_REVIEW = 1L << 11; //网红名人审核中
    public final static Long OP_HAS_VIDEO_CHAT_REVIEW = 1L << 12; //视频聊天审核中
    public final static Long OP_HAS_VIDEO_CHAT_PASS = 1L << 13; //用户是否通过视频聊天
    public final static Long OP_HAS_OPEN_VIDEO_CHAT = 1L << 14; //用户是否打开视频聊天功能
    public final static Long OP_HAS_VIDEO_CHAT_ONCE = 1L << 15; //用户是否已经首次视频聊天
    public final static Long OP_HAS_CELEBRITY_PASS = 1L << 16; //用户是否通过成为名人
    public final static Long THIRD_ADMIN = 1L << 17; //是否为第三方管理人员


    /**
     * @param states 所有状态值
     * @param value  需要判断状态值
     * @return 是否存在
     */
    public static boolean hasState(long states, long value) {
        return (states & value) != 0;
    }

    /**
     * @param states 已有状态值
     * @param value  需要添加状态值
     * @return 新的状态值
     */
    public static long addState(long states, long value) {
        if (hasState(states, value)) {
            return states;
        }
        return (states | value);
    }

    /**
     * @param states 已有状态值
     * @param value  需要删除状态值
     * @return 新的状态值
     */
    public static long removeState(long states, long value) {
        if (!hasState(states, value)) {
            return states;
        }
        return states ^ value;
    }
}
