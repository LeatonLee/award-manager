package com.example.service;

public interface SmsService {

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    boolean sendSms(String phone);

}
