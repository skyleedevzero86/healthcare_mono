package com.sleekydz86.service.usermanagement.global.util;

import com.sleekydz86.service.usermanagement.dto.ApiResultCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommUtils {

    private static Pattern emailPattern = Pattern
            .compile("^([0-9a-zA-Z]?[-_\\.]?)*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,4}$");

    public static String getIpAddress(HttpServletRequest request) {

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static int getTotalPage(int totalCount, int size) {
        if (totalCount < 1 || size < 1)
            return 0;
        return (int) Math.ceil((double) totalCount / size);
    }

    public static boolean isTelNo(String value) {
        value = value.replaceAll("[^0-9]", "");
        if (value.length() < 8)
            return false;
        if (value.length() == 8) {
            return value.matches("^([0-9]{4})([0-9]{4})$");
        } else if (value.length() == 12) {
            return value.matches("^(^[0-9]{4})([0-9]{4})([0-9]{4})$");
        }
        return value.matches("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$");
    }

    public static boolean isEmail(String email) {
        if (StringUtils.isEmpty(email))
            return false;
        if (email.length() > 50)
            return false;
        email = email.trim();
        Matcher m = emailPattern.matcher(email);
        if (!m.matches())
            return false;
        return true;
    }

    public static boolean isDate(String value) {
        value = value.replaceAll("[^0-9]", "");
        if (value.length() < 8)
            return false;
        if (value.length() > 8)
            return isDateTime(value);
        try {
            SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMdd");
            dateFormatParser.setLenient(false);
            dateFormatParser.parse(value);
            return true;
        } catch (java.text.ParseException e) {
            return false;
        }
    }

    public static boolean isDateTime(String value) {
        value = value.replaceAll("[^0-9]", "");
        if (value.length() < 12)
            return false;
        if (value.length() == 12)
            value += "00";
        try {
            SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMddHHmmss");
            dateFormatParser.setLenient(false);
            dateFormatParser.parse(value);
            return true;
        } catch (java.text.ParseException e) {
            return false;
        }
    }

    public static String getErrorMessage(final ApiResultCode key, final Object... args) {
        String value = key.message;
        if (value == null) {
            value = key.message;
        }

        if (args.length < 1) {
            return key.message;
        }

        MessageFormat mf = new MessageFormat(value);
        return mf.format(args, new StringBuffer(), null).toString();
    }

    public static String getErrorMessage(String msg, final Object... args) {

        if (msg == null) {
            msg = "메세지가 없습니다.";
        }

        if (args.length < 1) {
            return msg;
        }

        MessageFormat mf = new MessageFormat(msg);
        return mf.format(args, new StringBuffer(), null).toString();
    }

}