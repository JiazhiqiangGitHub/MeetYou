package com.zrj.dllo.meetyou.internet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by ${ZhaoXuancheng} on 16/12/6.
 */
public class EncodeUtil {

// 将文字转为%%%%
    public static String encode(String str){
        try {
            return URLEncoder.encode(str,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;


    }
// 将%% 转为文字
    public static String decode(String utf){
        try {
            return URLDecoder.decode(utf,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return utf;
    }

}