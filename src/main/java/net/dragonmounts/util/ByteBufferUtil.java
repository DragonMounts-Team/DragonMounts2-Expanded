package net.dragonmounts.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ByteBufferUtil {
    public static ItemStack readStackSilently(PacketBuffer buffer) {
        try {
            return buffer.readItemStack();
        } catch (IOException e) {
            LogUtil.LOGGER.error("Error reading item stack", e);
        }
        return ItemStack.EMPTY;
    }

    /// @see PacketBuffer#readVarInt()
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

    /// @see PacketBuffer#writeVarInt(int)
    public static void writeVarInt(ByteBuf buffer, int value) {
        while ((value & -128) != 0) {
            buffer.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        buffer.writeByte(value);
    }

    /// @return an array with 8 booleans.
    public static boolean[] readFlags(ByteBuf buffer) {
        boolean[] flags = new boolean[8];
        int data = buffer.readUnsignedByte();
        for (int i = 0, bit = 1; i < 8; ++i, bit <<= 1) {
            flags[i] = (data & bit) == bit;
        }
        return flags;
    }

    /// @param flags 8 booleans at most.
    public static int compressFlags(boolean... flags) {
        int data = 0, bit = 1;
        for (boolean flag : flags) {
            data |= flag ? bit : 0;
            bit <<= 1;
        }
        return data;
    }

    /// @see PacketBuffer#readString(int)
    public static String readString(ByteBuf buffer, int maxLength) {
        int length = readVarInt(buffer);
        if (length > maxLength * 4)
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + length + " > " + maxLength * 4 + ")");
        if (length < 0)
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        String value = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
        buffer.readerIndex(buffer.readerIndex() + length);
        if (value.length() > maxLength)
            throw new DecoderException("The received string length is longer than maximum allowed (" + value + " > " + maxLength + ")");
        return value;
    }

    /// @see PacketBuffer#writeString(String)
    public static void writeString(ByteBuf buffer, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 32767)
            throw new EncoderException("String too big (was " + bytes.length + " bytes encoded, max " + 32767 + ")");
        writeVarInt(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }
}
