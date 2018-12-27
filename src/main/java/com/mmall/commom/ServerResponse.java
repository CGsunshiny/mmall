package com.mmall.commom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * 高复用响应对象
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候,如果是null的对象,key也会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //使之不在json序列化结果当中
    public Boolean isSuccess(){

        return this.status==ResponseCode.SUCCESS.getCode();

    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> ServerResponse<T> createBySuccess(){

        return new <T> ServerResponse<T>(ResponseCode.SUCCESS.getCode());

    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){

        return new <T> ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);

    }
    public static <T> ServerResponse<T> createBySuccess(T data){

        return new <T> ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);

    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){

        return new <T> ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);

    }

    public static <T> ServerResponse<T> createByError(){

        return new <T> ServerResponse<T>(ResponseCode.ERROR.getCode());

    }
    public static <T> ServerResponse<T> createByErrorMessage(String msg){

        return new <T> ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);

    }
    public static <T> ServerResponse<T> createByError(T data){

        return new <T> ServerResponse<T>(ResponseCode.ERROR.getCode(),data);

    }
    public static <T> ServerResponse<T> createByError(String msg,T data){

        return new <T> ServerResponse<T>(ResponseCode.ERROR.getCode(),msg,data);

    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){

        return new ServerResponse<T>(errorCode,errorMessage);

    }

}
