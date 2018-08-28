package com.ldx.conversationbase.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MD5Utility {
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        String MD5str = hexValue.toString();
        return MD5str;
    }

    public static String getSignature(String str) {
        MessageDigest md5 = null;
        if (str == null || str.length() == 0)
        {
            return "";
        }
        try {
            md5 = MessageDigest.getInstance("MD5");

            byte[] charArray = str.getBytes("Unicode");
            byte[] byteArray = new byte[charArray.length - 2];

            // Copy to a new array and remove leading two bytes
            for (int i = 0; i < charArray.length - 2; i++) {
                byteArray[i] = (byte) charArray[i + 2];
            }
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 7; i >=0; i--) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            for (int i = 15; i >=8; i--) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            String signature = hexValue.toString();
            return signature.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static long bytesToLong(byte[] int_bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(int_bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            long my_int = ois.readLong();
            ois.close();
            return my_int;
        } catch (IOException e) {
            return 0L;
        }
    }

    public static String filter(String str) {
        try{
            String regEx = "[`~!@#$%^&*()\\-+={}':;,\\[\\].<>/?￥%…（）_+|【】‘；：”“’。，、？\\s]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        }catch(Exception e){
            return str.trim();
        }
    }
}
