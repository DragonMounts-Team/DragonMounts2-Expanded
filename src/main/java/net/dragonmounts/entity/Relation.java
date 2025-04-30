package net.dragonmounts.entity;

import net.dragonmounts.util.LogUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

public enum Relation {
    STRANGER(false) {
        @Override
        public void onDeny(EntityPlayer player) {
            player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.untamed"), true);
        }
    },
    UNTRUSTED(false) {
        @Override
        public void onDeny(EntityPlayer player) {
            player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.locked"), true);

        }
    },
    TRUSTED(true) {
        @Override
        public void onDeny(EntityPlayer player) {
            player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.notOwner"), true);
        }
    },
    OWNER(true) {
        @Override
        public void onDeny(EntityPlayer player) {
            LogUtil.LOGGER.warn("Logical Error: {} is consider as owner", player.getName());
        }
    };

    public final boolean isTrusted;

    Relation(boolean isTrusted) {
        this.isTrusted = isTrusted;
    }

    public abstract void onDeny(EntityPlayer player);

    public static Relation checkRelation(TameableDragonEntity dragon, EntityPlayer player) {
        if (!dragon.isTamed() || dragon.isEgg()) return STRANGER;
        if (dragon.isOwner(player)) return OWNER;
        return dragon.allowedOtherPlayers() ? TRUSTED : UNTRUSTED;
    }

    /// @return if the player is denied
    public static boolean denyIfNotOwner(TameableDragonEntity dragon, EntityPlayer player) {
        Relation relation = checkRelation(dragon, player);
        if (OWNER == relation) return false;
        relation.onDeny(player);
        return true;
    }
}
