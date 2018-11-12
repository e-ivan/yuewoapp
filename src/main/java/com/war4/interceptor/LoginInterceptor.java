package com.war4.interceptor;

import com.war4.base.Response;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserAccessToken;
import com.war4.service.UserAccessTokenService;
import com.war4.service.UserInfoBaseService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created by dly on 2016/11/25.
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserAccessTokenService userAccessTokenService;
    @Autowired
    private UserInfoBaseService userInfoBaseService;

    private List<String> notNeedLoginUrlList;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String url = httpServletRequest.getRequestURL().toString();
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type,token");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        if (!isNotPresent(url, notNeedLoginUrlList)) {
            return true;
        }
        String uId = httpServletRequest.getHeader("tokenUserId");
        Response response = null;
        Boolean resultFlag = true;
        if (uId == null) {
            response = new Response(CommonErrorResult.VERIFY_FAIL, "uId不能为空");
            resultFlag = false;
            httpServletResponse.getWriter().print(JSONObject.fromObject(response));
            return resultFlag;
        }
        String accessToken = httpServletRequest.getHeader("accessToken");

        if (accessToken == null) {
            response = new Response(CommonErrorResult.VERIFY_FAIL, "accessToken不能为空");
            resultFlag = false;
            httpServletResponse.getWriter().print(JSONObject.fromObject(response));
            return resultFlag;
        }
        UserAccessToken userAccessToken = userAccessTokenService.getAccessTokenByUserId(uId);

        if (userAccessToken == null) {
            response = new Response(CommonErrorResult.VERIFY_FAIL, "不存在的accessToken！");
            resultFlag = false;
            httpServletResponse.getWriter().print(JSONObject.fromObject(response));
            return resultFlag;
        }
        if (!accessToken.equals(userAccessToken.getAccessToken())) {//不匹配
            response = new Response(CommonErrorResult.VERIFY_FAIL, "不匹配的accessToken");
            resultFlag = false;
        }
        if (!resultFlag) {
            httpServletResponse.getWriter().print(JSONObject.fromObject(response));
        } else {
            userAccessToken.setUpdateTime(new Date());
            userAccessTokenService.updateAccessToken(userAccessToken);
        }
        return resultFlag;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private static boolean isNotPresent(String source, List<String> list) {
        boolean result = true;
        if (list != null && list.size() > 0) {
            for (String str : list) {
                if (source.contains(str)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }


    public List<String> getNotNeedLoginUrlList() {
        return notNeedLoginUrlList;
    }

    public void setNotNeedLoginUrlList(List<String> notNeedLoginUrlList) {
        this.notNeedLoginUrlList = notNeedLoginUrlList;
    }

}
