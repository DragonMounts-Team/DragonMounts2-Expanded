package net.dragonmounts.item;

import net.dragonmounts.compat.FixerCompat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

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
        FixerCompat.disableEntityFixers(tag);
        return tag;
    }

    ItemStack saveEntity(T entity);

    @Nullable
    Entity loadEntity(
            WorldServer level,
            ItemStack stack,
            @Nullable EntityPlayer player,
            BlockPos pos
    );

    Class<T> getContentType();

    boolean isEmpty(@Nullable NBTTagCompound tag);

    default void onItemDestroy(EntityItem entity, ItemStack stack) {
        if (!this.isEmpty(stack.getTagCompound())) {
            Entity content = this.loadEntity((WorldServer) entity.world, stack, null, entity.getPosition());
        }
        entity.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1F, 1F, false);
    }

    static void onItemDestroy(EntityItem entity) {
        if (!(entity.world instanceof WorldServer)) return;
        ItemStack stack = entity.getItem();
        Item item = stack.getItem();
        if (item instanceof IEntityContainer<?>) {
            ((IEntityContainer<?>) item).onItemDestroy(entity, stack);
        }
    }
}
