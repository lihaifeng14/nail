package com.nail.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import android.text.TextUtils;

public class CommonUtils {
    public static int getSampleSize(int sizePixels, int maxNumOfPixels) {
        int initialSize = (int) Math.ceil(Math.sqrt(sizePixels / maxNumOfPixels));
        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    public static boolean writeStringToFile(File file, String content, boolean isAppend) {
        boolean isWriteOk = false;
        char[] buffer = null;
        int count = 0;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            if (file == null) {
                return isWriteOk;
            }

            br = new BufferedReader(new StringReader(content));
            bw = new BufferedWriter(new FileWriter(file, isAppend));
            buffer = new char[DEFAULT_BUFFER_SIZE];
            int len = 0;
            while ((len = br.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                bw.write(buffer, 0, len);
                count += len;
            }
            bw.flush();
            isWriteOk = content.length() == count;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                    bw = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
                buffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isWriteOk;
    }

    public static String getStringFromFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        StringBuilder builder = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String readerStr = builder.toString();
            return readerStr;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (builder != null) {
                int size = builder.length();
                if (size > 0) {
                    builder.delete(0, size);
                }
                builder = null;
            }
        }
        return null;
    }
}