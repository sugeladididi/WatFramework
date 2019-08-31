package com.mvc.controller;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 控制器基类，处理请求和应答
 * @author 李福涛
 *
 */
public abstract class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
 
    public void init(HttpServletRequest request,HttpServletResponse response){
        this.request = request;
        this.response = response;
    }
 
 
    public HttpServletRequest getRequest() {
        return request;
    }
 
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
 
    public HttpServletResponse getResponse() {
        return response;
    }
 
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
