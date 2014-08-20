package com.nail.core.utils;

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
}