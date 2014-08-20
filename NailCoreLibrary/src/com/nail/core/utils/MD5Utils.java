package com.nail.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static MessageDigest mMessageDigest;

    static {
        try {
            mMessageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getMD5(String str) {
        if (str != null) {
            return getMD5(str.getBytes());
        }
        return null;
    }

    public static String getMD5(byte[] bytes) {
        if (mMessageDigest != null && bytes != null) {
            synchronized (MD5Utils.class) {
                mMessageDigest.update(bytes);
                return bytesToHexString(mMessageDigest.digest());
            }
        }
        return null;
    }

    private static String bytesToHexString(byte[] bytes) {
        if (bytes != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                int v = bytes[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(hv);
            }
            return stringBuffer.toString();
        }
        return null;
    }
}