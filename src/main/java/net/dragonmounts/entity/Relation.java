package net.dragonmounts.entity;

import net.dragonmounts.util.LogUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

public enum Relation {
    STRANGER(false, "message.dragonmounts.dragon.untamed"),
    UNTRUSTED(false, "message.dragonmounts.dragon.locked"),
    TRUSTED(true, "message.dragonmounts.dragon.notOwner"),
    OWNER(true, null);

    public final boolean isTrusted;
    private final ITextComponent reason;

    Relation(boolean isTrusted, @Nullable String reason) {
        this.isTrusted = isTrusted;
        this.reason = reason == null ? null : new TextComponentTranslation(reason);
    }

    public final void onDeny(EntityPlayer player) {
        if (this.reason == null) {
            LogUtil.LOGGER.warn("Logical Error: {} should not be denied!", player.getName());
        } else {
            player.sendStatusMessage(this.reason, true);
        }
    }

    public static Relation checkRelation(TameableDragonEntity dragon, EntityPlayer player) {
        if (!dragon.isTamed() || dragon.isEgg()) return STRANGER;
        if (player.getUniqueID().equals(dragon.getOwnerId())) return OWNER;
        return dragon.allowedOtherPlayers() ? TRUSTED : UNTRUSTED;
    }

    /// @return if the player is denied
    public static boolean denyIfNotOwner(TameableDragonEntity dragon, EntityPlayer player) {
        Relation relation = checkRelation(dragon, player);
        if (OWNER == relation) return false;
        relation.onDeny(player);
        return true;
    }

    /// @return if the player is denied
    public static boolean denyIfNotOwner(NBTTagCompound entity, @Nullable EntityPlayer player) {
        if (player == null) return false;
        String owner = entity.getString("OwnerUUID");
        if (owner.isEmpty() || owner.equalsIgnoreCase(player.getUniqueID().toString())) return false;
        player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.notOwner"), true);
        return true;
    }
}
