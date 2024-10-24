package com.TheRPGAdventurer.ROTD.objects.items.entity;

import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import com.TheRPGAdventurer.ROTD.objects.items.ItemDragonAmuletNEW;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.Random;

public class EntityDragonAmulet extends EntityItem {
    public EntityDragonAmulet(World level) {
        super(level);
        this.age = -32768;
    }

    public EntityDragonAmulet(World level, Entity original, ItemStack stack) {
        super(level, original.posX, original.posY, original.posZ, stack);
        this.motionX = original.motionX;
        this.motionY = original.motionY;
        this.motionZ = original.motionZ;
        this.age = -32768;
        if (original instanceof EntityItem) {
            EntityItem item = (EntityItem) original;
            this.setPickupDelay(item.pickupDelay);
            this.setThrower(item.getThrower());
            this.setOwner(item.getOwner());
        }
    }

    private boolean containsDragonEntity(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey("breed");
    }

    /**
     * Spawn the dragon when the item gets destroyed
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean isDead = this.isDead;
        super.attackEntityFrom(source, amount);
        if (isDead != this.isDead) {
            ItemStack stack = this.getItem();
            if (stack.getItem() instanceof ItemDragonAmuletNEW && containsDragonEntity(stack)) {
                EntityTameableDragon dragon = new EntityTameableDragon(this.world);
                dragon.readEntityFromNBT(stack.getTagCompound());
                dragon.setPosition(this.posX, this.posY, this.posZ);
                Random random = new Random();
                dragon.rotationYaw=random.nextInt(180);
                dragon.rotationPitch=random.nextInt(180);
                dragon.attackEntityFrom(DamageSource.GENERIC,17);
                world.playSound(posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1F, 1F, false);
                world.spawnEntity(dragon);
            }
        }
        return false;
    }

}
