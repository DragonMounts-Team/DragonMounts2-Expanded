package top.dragonmounts.util;

import io.netty.buffer.ByteBuf;

public class VarInt {
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
}
