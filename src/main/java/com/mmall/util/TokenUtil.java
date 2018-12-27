package com.mmall.util;

import com.mmall.commom.Const;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Component
public class TokenUtil {

    public static String getToken(){

        return UUID.randomUUID().toString();

    }

    public static String getTokenByRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies) {
            if (StringUtils.equals(cookie.getName(), Const.CURRENT_USER)){
                return cookie.getValue();
            }
        }
        return null;
    }

}
