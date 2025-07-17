package net.dragonmounts.entity;

import net.dragonmounts.util.LogUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

public enum Relation {
    STRANGER {
        @Override
        public void onDeny(EntityPlayer player) {
            player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.untamed"), true);
        }
    },
    TRUSTED {
        @Override
        public void onDeny(EntityPlayer player) {
            player.sendStatusMessage(new TextComponentTranslation("message.dragonmounts.dragon.locked"), true);
        }
    },
    OWNER {
        @Override
        public void onDeny(EntityPlayer player) {
            LogUtil.LOGGER.warn("Logical Error: {} is consider as owner", player.getName());
        }
    };

    public abstract void onDeny(EntityPlayer player);

    public static Relation checkRelation(TameableDragonEntity dragon, EntityPlayer player) {
        if (!dragon.isTamed() || dragon.isEgg()) return STRANGER;
        if (dragon.isOwner(player)) return OWNER;
        return dragon.allowedOtherPlayers() ? TRUSTED : STRANGER;
    }
}
