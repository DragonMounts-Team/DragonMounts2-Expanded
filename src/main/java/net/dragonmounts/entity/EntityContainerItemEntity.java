package net.dragonmounts.entity;

import net.dragonmounts.item.IEntityContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class EntityContainerItemEntity extends EntityItem {
    public EntityContainerItemEntity(World level) {
        super(level);
        this.age = -32768;
    }

    public EntityContainerItemEntity(World level, Entity original, ItemStack stack) {
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

    /**
     * Spawn the content when the item gets destroyed
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean isDead = this.isDead;
        super.attackEntityFrom(source, amount);
        if (isDead != this.isDead) {
            ItemStack stack = this.getItem();
            Item item = stack.getItem();
            if (item instanceof IEntityContainer<?>) {
                IEntityContainer<?> container = (IEntityContainer<?>) item;
                if (!container.isEmpty(stack.getTagCompound())) {
                    Entity entity = container.loadEntity(this.world, stack, null, this.getPosition(), false, null);
                    if (entity != null) {
                        entity.setPosition(this.posX, this.posY, this.posZ);
                        entity.rotationYaw = this.rand.nextInt(180);
                        entity.rotationPitch = this.rand.nextInt(180);
                    }
                }
                this.world.playSound(posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1F, 1F, false);
            }
        }
        return false;
    }
}
