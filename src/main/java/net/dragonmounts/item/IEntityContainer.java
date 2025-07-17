package net.dragonmounts.item;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public interface IEntityContainer<T extends Entity> {
    static NBTTagCompound simplifyData(NBTTagCompound tag) {
        tag.removeTag("Air");
        tag.removeTag("DeathTime");
        tag.removeTag("FallDistance");
        tag.removeTag("FallFlying");
        tag.removeTag("Fire");
        tag.removeTag("HurtByTimestamp");
        tag.removeTag("HurtTime");
        tag.removeTag("InLove");
        tag.removeTag("Leash");
        tag.removeTag("Motion");
        tag.removeTag("OnGround");
        tag.removeTag("Passengers");
        tag.removeTag("PortalCooldown");
        tag.removeTag("Pos");
        tag.removeTag("Rotation");
        tag.removeTag("Sitting");
        tag.removeTag("SleepingX");
        tag.removeTag("SleepingY");
        tag.removeTag("SleepingZ");
        return tag;
    }

    ItemStack saveEntity(T entity);

    /**
     * null: denied
     * empty: failed
     */
    @Nullable
    Entity loadEntity(
            World level,
            ItemStack stack,
            @Nullable EntityPlayer player,
            BlockPos pos,
            boolean yOffset,
            @Nullable String feedback
    );

    Class<T> getContentType();

    boolean isEmpty(@Nullable NBTTagCompound tag);
}
