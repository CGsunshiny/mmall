package com.mmall.controller.portal;

import com.mmall.commom.Const;
import com.mmall.commom.ResponseCode;
import com.mmall.commom.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.RedisUtil;
import com.mmall.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    IUserService iUserService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpServletResponse response){
        ServerResponse<User> serverResponse = iUserService.login(username, password);
        if (serverResponse.isSuccess()){
            if (serverResponse.getData().getRole()==Const.Role.ROLE_CUSTOMER){

                String token = UUID.randomUUID().toString();

                Cookie cookie = new Cookie(Const.CURRENT_USER,token);

                response.addCookie(cookie);

                //将用户信息存储到redis中并设置缓存时间

                boolean flag = redisUtil.set(token,serverResponse.getData(), 60 * 30);

                if (!flag){
                    return ServerResponse.createByErrorMessage("用户登录失败");
                }

            }else {
                return ServerResponse.createByErrorMessage("不是用户，不能登录");
            }
        }
        return serverResponse;
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    public ServerResponse<String> logout(HttpServletRequest request){
//        获取请求传过来的cookie
        String token = TokenUtil.getTokenByRequest(request);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("无效操作");
        }
        redisUtil.del(token);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do")
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 用户信息校验
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.GET)
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取登录用户信息
     * @param request
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
    public ServerResponse<User> getUserInfo(HttpServletRequest request){

        String token = TokenUtil.getTokenByRequest(request);

        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("获取用户信息异常");
        }
        User currentUser =(User)redisUtil.get(token);

        if (currentUser!=null){
            return ServerResponse.createBySuccess(currentUser);
        }else {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
    }

    /**
     * 获取找回密码的问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.GET)
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 校验找回密码的问题是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.GET)
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
     * 登录状态下的重置密码
     * @param request
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.GET)
    public ServerResponse<String> resetPassword(HttpServletRequest request,String passwordOld,String passwordNew){
        String token = TokenUtil.getTokenByRequest(request);

        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        User currentUser =(User)redisUtil.get(token);
        if (currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,currentUser);
    }

    /**
     * 跟新个人信息
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(value = "update_user_information.do",method = RequestMethod.GET)
    public ServerResponse<User> updateUserInformation(HttpServletRequest request,User user){
        String token = TokenUtil.getTokenByRequest(request);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("用户未登录,无法跟新用户信息");
        }
        User currentUser =(User)redisUtil.get(token);
        if (currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录,无法跟新用户信息");
        }
        user.setId(currentUser.getId());
        ServerResponse<User> response = iUserService.updateInformation(user);
//        重新把用户信息添加到session中
        if (response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            redisUtil.set(token,response.getData(),60*30);
        }
        return response;
    }
    @RequestMapping(value = "get_user_information.do",method = RequestMethod.GET)
    public ServerResponse<User> getUserInformation(HttpServletRequest request){
        String token = TokenUtil.getTokenByRequest(request);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录需要强制登录");
        }
        User currentUser =(User)redisUtil.get(token);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录需要强制登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
