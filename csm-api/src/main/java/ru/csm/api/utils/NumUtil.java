/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
