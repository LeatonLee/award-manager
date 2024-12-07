package com.example.service;

public interface SmsService {

    boolean sendSms(String phone);

    String getSmsCode(String phone);
}
