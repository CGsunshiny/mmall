package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);

    int updateUserSelective(User user);

    User selectUserInformationByUserId(Integer userId);

    int checkUsername(String username);

    int checkEmailByUserId(@Param(value = "email")String email,@Param(value = "userId")Integer userId);

    int checkEmail(String email);

    int checkPhone(String Phone);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param(value = "username") String username,@Param(value = "question") String question,@Param(value = "answer") String answer);

    int updatePasswordByUsername(@Param(value = "username")String username,@Param(value = "passwordNew")String passwordNew);

    int checkPasswordOldByUserId(@Param(value = "passwordOld")String passwordOld,@Param(value = "userId")Integer userId);

    User selectLogin(@Param(value = "username") String username,@Param(value = "password") String password);

}