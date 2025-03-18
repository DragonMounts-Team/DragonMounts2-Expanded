package net.dragonmounts.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.WorldServer;

public abstract class SPathDebugPacket {
    public static void writePoint(ByteBuf buffer, PathPoint node) {
        buffer.writeInt(node.x).writeInt(node.y).writeInt(node.z)
                .writeFloat(node.distanceFromOrigin)
                .writeFloat(node.cost)
                .writeFloat(node.costMalus)
                .writeBoolean(node.visited)
                .writeInt(node.nodeType.ordinal())
                .writeFloat(node.distanceToTarget);
    }

    public static void broadcast(WorldServer level, Entity entity, Path path, float distance) {
        if (path == null) return;
        PathPoint dest = path.getFinalPathPoint();
        if (dest == null) return;
        int len = path.getCurrentPathLength();
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeInt(entity.getEntityId())
                .writeFloat(distance)
                .writeInt(path.getCurrentPathIndex());
        writePoint(buffer, dest);
        buffer.writeInt(len);
        for (int i = 0; i < len; ++i) {
            writePoint(buffer, path.getPathPointFromIndex(i));
        }
        buffer.writeInt(0).writeInt(0);
        level.getEntityTracker().sendToTracking(entity, new SPacketCustomPayload("MC|DebugPath", buffer));
    }
}
