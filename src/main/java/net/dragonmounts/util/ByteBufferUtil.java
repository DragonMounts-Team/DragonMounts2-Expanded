package net.dragonmounts.util;

import io.netty.buffer.ByteBuf;

public class ByteBufferUtil {
    public static int readVarInt(ByteBuf buffer) {
        int result = 0;
        int size = 0;
        byte value;
        do {
            value = buffer.readByte();
            result |= (value & 127) << size * 7;
            if (++size > 5) throw new RuntimeException("VarInt too big");
        } while ((value & 128) == 128);
        return result;
    }

    public static <T extends ByteBuf> T writeVarInt(T buffer, int value) {
        while ((value & -128) != 0) {
            buffer.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        buffer.writeByte(value);
        return buffer;
    }

    public static <T extends ByteBuf> T writeVarInt(T buffer, int... values) {
        for (int v, i = 0, size = values.length; i < size; ++i) {
            v = values[i];
            while ((v & -128) != 0) {
                buffer.writeByte(v & 127 | 128);
                v >>>= 7;
            }
            buffer.writeByte(v);
        }
        return buffer;
    }

    /**
     * @return an array with 8 booleans.
     */
    public static boolean[] readFlags(ByteBuf buffer) {
        boolean[] flags = new boolean[8];
        int data = buffer.readUnsignedByte();
        for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
            flags[i] = (data & bit) == bit;
        }
        return flags;
    }

    /**
     * @param flags 8 booleans at most.
     */
    public static <T extends ByteBuf> T writeFlags(T buffer, boolean... flags) {
        int data = 0, bit = 1;
        for (boolean flag : flags) {
            data |= flag ? bit : 0;
            bit <<= 1;
        }
        buffer.writeByte(data);
        return buffer;
    }
}
