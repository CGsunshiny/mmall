package com.mmall;

import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MmallApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    Logger logger = LoggerFactory.getLogger(MmallApplicationTests.class);

    @Test
    public void contextLoads() {

        int i = userMapper.checkEmail("1206640707@qq.com");

        System.out.println(i);

        redisUtil.set("redis","xuekaize",12);

        Object redis = redisUtil.get("redis");
        Object xuekaize = redisUtil.get("xuekaize");
        Object name = redisUtil.get("name");
        User user = (User)redisUtil.get("9f5e8bc6-bbea-4d7a-b4c5-7419efcc7336");

        System.out.println(xuekaize+":"+name+":"+redis);
    }
    public static int[] orderArrary(int[] array){

        for (int i = 0; i <array.length ; i++) {

            for (int j = i+1; j <array.length ; j++) {
                int tmep = 0;
                if (array[i]>array[j]){
                    tmep = array[i];
                    array[i] = array[j];
                    array[j] = tmep;
                }
            }
        }
        return array;
    }
    @Test
    public void test(){

        int[] a = new int[]{2,4,5,7,6,1,5};
        int[] arrary = orderArrary(a);
        for (int i = 0; i < arrary.length; i++) {
            System.out.print(i);
        }
    }

}

