package gtsoffenbach.tourdegts_adminapp;

import android.graphics.Rect;

import java.nio.ByteBuffer;

/**
 * Created by Noli on 31.08.2014.
 */
public class Utils {
    public static String convertByteArrayToHexString(byte[] array) {
        StringBuilder hex = new StringBuilder(array.length * 2);
        for (byte b : array) {
            hex.append(String.format("%02X ", b));
        }
        return hex.toString();
    }

    public static long convertByteArrayToDecimal(byte[] array) {
        ByteBuffer bb = ByteBuffer.wrap(array);
        return bb.getLong();

    }
}
