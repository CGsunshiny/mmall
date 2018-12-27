package com.mmall.commom;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class TokenCache {

   private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

   public static final String TOKEN_PREFIX = "token_";

   private static LoadingCache<String,String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
           .build(new CacheLoader<String, String>() {
//               默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，则调用该方法进行加载
               @Override
               public String load(String s) throws Exception {
                   return "null";
               }
           });

   public static void setKey(String key,String value){
       loadingCache.put(key,value);
   }
    public static String getKey(String key){

       String value = null;

        try {
            value = loadingCache.get(key);
            if (value.equals(null)){
                return null;
            }else {
                return value;
            }

        } catch (ExecutionException e) {

            logger.error("loadCache get error",e);
            e.printStackTrace();
        }

        return null;
    }

}
