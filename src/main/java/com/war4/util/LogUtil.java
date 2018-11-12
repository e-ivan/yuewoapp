package com.war4.util;


import com.war4.base.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统日志
 * Created by hh on 2017.7.3 0003.
 */
public class LogUtil {
    private static final Logger logger = Logger.getLogger(LogUtil.class);
    public void thirdLog(JoinPoint point,BusinessException ex){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("x-real-ip");//获取反向代理的真实地址
        if (ip == null){
            ip = request.getRemoteAddr();
        }
        Object[] args = point.getArgs();
        StringBuilder sb = new StringBuilder("[").append(StringUtils.join(args, ",")).append("]");
        StringBuilder msg = new StringBuilder(500);
        msg.append("ip:").append(ip).append("; target:").append(point.getSignature()).append("; args:").append(sb.toString()).append("; msg:").append(ex.getExMessage());
        logger.error(msg);
    }
}
