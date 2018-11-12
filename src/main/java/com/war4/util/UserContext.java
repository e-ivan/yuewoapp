package com.war4.util;

import com.war4.enums.UserRoleType;
import com.war4.pojo.UserInfoBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户上下文
 * Created by hh on 2017.6.21 0021.
 */
public class UserContext {
    public static final String USER_IN_SESSION= "USER_IN_SESSION";
    public static final String USER_IN_REQUEST= "USER_IN_REQUEST";

    public static Map<String,HttpSession> sessionMap = new HashMap<>();

    private static HttpSession getSession() {
        return getCurrentRequest().getSession();
    }

    public static HttpServletRequest getCurrentRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取用户id
     * @return
     */
    public static String getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader("tokenUserId");
    }

    public static UserInfoBase getCurrentUser() {
        return (UserInfoBase) getSession().getAttribute(USER_IN_SESSION);
    }

    /**
     * 查询当前用户是否为管理员
     */
    public static boolean isSuperManager(){
        UserInfoBase currentUser = getCurrentUser();
        return currentUser != null && StringUtils.equals(currentUser.getRoleType(), UserRoleType.SUPERMANAGER.getCode());
    }

    public static void logoutCurrentUser(){
        getSession().removeAttribute(USER_IN_SESSION);
    }
}
