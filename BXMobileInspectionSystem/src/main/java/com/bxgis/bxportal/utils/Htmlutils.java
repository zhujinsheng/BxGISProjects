package com.bxgis.bxportal.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:zjs
 * @Description: 将字符串转换成网页内容
 * @Created in  2017/11/13
 * @modified By:
 */


public class Htmlutils {
    public static String format(String str) {
        //用正则将img的style置为空
        Pattern pattern = Pattern.compile("style=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(str);
        str = matcher.replaceAll("");

        return "<html>" +
                "<head>" +
                "<meta charset=\"utf-8\">" +
                "<title>Sign in | Score System</title>" +
                "<style type=\"text/css\">\n" +
                "img{margin-top:15px;margin-bottom:15px;}" +
                "body{display:flex;flex-direction:column;justify-content:center;line-height:2;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                str +
                "</body>" +
                "</html>";
    }
}
