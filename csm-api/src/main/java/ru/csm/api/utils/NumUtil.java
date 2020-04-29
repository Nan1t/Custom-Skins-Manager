package ru.csm.api.utils;

public final class NumUtil {

    public static byte[] toByteArray(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)(value)
        };
    }

    public static byte[] toByteArray(short value) {
        return new byte[] {
                (byte)(value >> 8),
                (byte)(value)
        };
    }

    public static int intFromBytes(byte[] bytes) {
        return intFromBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    public static int intFromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 255) << 16 | (b3 & 255) << 8 | (b4 & 255);
    }

    public static short shortFromBytes(byte[] bytes) {
        return shortFromBytes(bytes[0], bytes[1]);
    }

    public static short shortFromBytes(byte b1, byte b2) {
        return (short) ((b1 & 255) << 8 | (b2 & 255));
    }
}
