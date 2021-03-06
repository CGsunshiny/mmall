package com.mmall.service.impl;


import com.mmall.commom.Const;
import com.mmall.commom.ServerResponse;
import com.mmall.commom.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;


    @Override
    public ServerResponse<User> login(String username, String password) {
        if (userMapper.checkUsername(username)==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
//       md5 加密
        String passwordMD5 = MD5Util.MD5EncodeUtf8(password);
        User user =null;
        user = userMapper.selectLogin(username, passwordMD5);
        if (user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
//        检验
        ServerResponse response = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!response.isSuccess()){
            return response;
        }
         response = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!response.isSuccess()){
            return response;
        }
        response = this.checkValid(user.getPhone(),Const.PHONE);
        if (!response.isSuccess()){
            return response;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        String passwordMD5 = MD5Util.MD5EncodeUtf8(user.getPassword());
        user.setPassword(passwordMD5);
        int resultCount = userMapper.insert(user);
        if(resultCount>0){
            return ServerResponse.createBySuccessMessage("注册成功");
        }else {
            return ServerResponse.createByErrorMessage("注册失败");
        }
    }
    @Override
    public ServerResponse<String> checkValid(String str,String type){
        if (StringUtils.isNotBlank(type)){
//            开始校验
            if (Const.USERNAME.equals(type)){
                if (userMapper.checkUsername(str)>0){
                    return ServerResponse.createByErrorMessage("用户已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                if (userMapper.checkEmail(str)>0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
            if (Const.PHONE.equals(type)){
                if (userMapper.checkPhone(str)>0){
                    return ServerResponse.createByErrorMessage("电话号码已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("检验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username){
        if (userMapper.checkUsername(username)==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }else {
            return ServerResponse.createByErrorMessage("找回密码得到问题是空的");
        }
    }
    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount>0){
//            说明这个问题和答案是这个用户的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }
    @Override
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
//        检验token是否为空
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，Token需要传递");
        }
//        校验用户名是否存在
        ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
//        检验token是否正确
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者一过期");
        }
        if (StringUtils.equals(token,forgetToken)){
//            新密码做md5加密
            String passwordNewMD5 = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, passwordNewMD5);
            if (resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取修改密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }
    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,User user){
//        检验用户旧密码是否正确
        int resultCount = userMapper.checkPasswordOldByUserId(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateUserSelective(user);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createBySuccessMessage("重置密码失败");
    }
    @Override
    public ServerResponse<User> updateInformation(User user){
//        校验email是否存在除了当前用户
        if(StringUtils.isNotBlank(user.getEmail())){
            int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
            if (resultCount>0){
                return ServerResponse.createByErrorMessage("email已存在请重新输入");
            }
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int resultCount = userMapper.updateUserSelective(updateUser);
        if (resultCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }
    @Override
    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectUserInformationByUserId(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
