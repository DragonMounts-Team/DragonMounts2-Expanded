package net.dragonmounts.client;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.capability.DMCapabilities;
import net.dragonmounts.capability.IHardShears;
import net.dragonmounts.client.breath.impl.ClientBreathHelper;
import net.dragonmounts.client.model.dragon.DragonAnimator;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMKeyBindings;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.init.DragonFoods;
import net.dragonmounts.network.CDragonBreathPacket;
import net.dragonmounts.network.COpenInventoryPacket;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ClientDragonEntity extends TameableDragonEntity {
    public static boolean openInventoryIfAvailable(Minecraft minecraft) {
        if (DMConfig.REDIRECT_INVENTORY.value &&
                minecraft.player != null &&
                minecraft.player.getRidingEntity() instanceof ClientDragonEntity
        ) {
            DragonMounts.NETWORK_WRAPPER.sendToServer(new COpenInventoryPacket());
            return true;
        }
        return false;
    }

    public final DragonAnimator animator = new DragonAnimator(this);
    public boolean isInGui = false;
    public int controlFlags = -1;

    public ClientDragonEntity(World level) {
        super(level);
    }

    @Override
    protected ClientBreathHelper createBreathHelper() {
        return new ClientBreathHelper(this);
    }

    @Override
    public final Vec3d getHeadRelativeOffset(float x, float y, float z) {
        return this.animator.getHeadRelativeOffset(x, y, z);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.lifeStageHelper.readFromNBT(nbt);
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey(DragonVariant.DATA_PARAMETER_KEY)) {
            this.setVariant(DragonVariant.byName(nbt.getString(DragonVariant.DATA_PARAMETER_KEY)));
        } else if (nbt.hasKey(DragonType.DATA_PARAMETER_KEY)) {
            this.setDragonType(DragonType.byName(nbt.getString(DragonType.DATA_PARAMETER_KEY)), null);
        }
        if (this.firstUpdate) {
            this.variantHelper.onVariantChanged(this.getVariant());
        }
    }

    @Override
    public void onLivingUpdate() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && player == this.getRidingEntity()) {
            // sent when dragon is riding on a player
            DragonMounts.NETWORK_WRAPPER.sendToServer(new CDragonBreathPacket(
                    this.getEntityId(),
                    DMKeyBindings.KEY_BREATHE.isKeyDown()
            ));
        }
        this.lifeStageHelper.ageUp(1);
        this.breathHelper.update();
        if (this.isEgg()) {
            this.variantHelper.update();
            this.getVariant().type.tickClient(this);
            super.onLivingUpdate();
            return;
        }
        this.getVariant().type.tickClient(this);
        this.animator.update();
        if (!this.isDead) {
            if (this.healingEnderCrystal != null && this.healingEnderCrystal.isDead) {
                this.healingEnderCrystal = null;
            }
            this.findCrystal();
        }
        EnumParticleTypes sneeze = this.getVariant().type.sneezeParticle;
        if (sneeze != null && !this.isUsingBreathWeapon() && rand.nextInt(700) == 0 && this.lifeStageHelper.isOldEnough(DragonLifeStage.FLEDGLING)) {
            Vec3d pos = this.getHeadRelativeOffset(0.0F, 4.0F, 22.0F);
            double x = pos.x, y = pos.y, z = pos.z;
            for (int i = -1; i < 1; ++i) {
                world.spawnParticle(sneeze, x, y + 0.5 * i, z, 0, 0.3, 0);
            }
            world.playSound(null, x, y, z, DMSounds.DRAGON_SNEEZE, SoundCategory.NEUTRAL, 0.8F, 1);
        }
        super.onLivingUpdate();
    }


    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        DragonLifeStage stage = this.lifeStageHelper.getLifeStage();
        if (DragonLifeStage.EGG == stage) return player.isSneaking();
        // prevent doing any interactions when a hatchling rides you, the hitbox could block the player's raytraceresult for rightclick
        if (player.isPassenger(this)) return false;
        ItemStack stack = player.getHeldItem(hand);
        final Relation relation = Relation.checkRelation(this, player);
        final boolean isChild = stage.isBaby();
        if (!stack.isEmpty()) {
            if (!isChild && this.canShare()) {
                IHardShears shears = stack.getCapability(DMCapabilities.HARD_SHEARS, null);
                if (shears != null && shears.canShear(stack, player, this)) return true;
            }
            if (relation.isTrusted && ((
                    this.onGround && ItemUtil.anyMatches(stack, "stickWood", "bone")
            ) || (!isChild && (
                    this.canCollectBreath() && stack.getItem() == Items.GLASS_BOTTLE
            ) || (
                    !this.isSaddled() && stack.getItem() == Items.SADDLE
            )))) return true;
            if (DragonFoods.getFood(stack).tryFeed(this, player, relation, stack, hand)) return true;
            if (stack.interactWithEntity(player, this, hand)) return true;
        }
        if (relation.isTrusted) {
            if (isChild && !this.isSitting() && player.getPassengers().size() < 2) {
                this.startRiding(player, true);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDeathUpdate() {
        // freeze at place
        motionX = motionY = motionZ = 0;
        rotationYaw = prevRotationYaw;
        rotationYawHead = prevRotationYawHead;

        if (isEgg() || ++deathTime > getMaxDeathTime()) setDead();// actually delete entity after the time is up

        if (deathTime < getMaxDeathTime() - 20) {
            int amount = (int) (4 * this.lifeStageHelper.getScale());
            for (int i = 0; i < amount; i++) {
                spawnBodyParticle(EnumParticleTypes.CLOUD);
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (this.isEgg()) {
            this.lifeStageHelper.playEggCrackEffect();
        }
        super.onDeath(cause);
    }

    public void onWingsDown(float speed) {
        // play wing sounds
        this.playSound(getWingsSound(), 0.8f + (this.lifeStageHelper.getScale() - speed), 1.0F, true);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        switch (id) {
            case DO_ATTACK:
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 0.7F);
                // play attack animation
                this.animator.ticksSinceLastAttack = 0;
                break;
            case DO_ROAR:
                SoundEvent sound = this.getVariant().type.getRoarSound(this);
                if (sound == null) break;
                this.world.playSound(posX, posY, posZ, sound, SoundCategory.NEUTRAL, MathHelper.clamp(this.lifeStageHelper.getScale(), 0.3F, 0.6F), getSoundPitch(), true);
                this.animator.ticksSinceLastRoar = 0;
                break;

            default:
                super.handleStatusUpdate(id);
        }
    }

    public void spawnBodyParticle(EnumParticleTypes type) {
        double ox, oy, oz;
        float s = this.getAdjustedSize() * 1.2f;

        switch (type) {
            case EXPLOSION_NORMAL:
                ox = rand.nextGaussian() * s;
                oy = rand.nextGaussian() * s;
                oz = rand.nextGaussian() * s;
                break;

            case CLOUD:
                ox = (rand.nextDouble() - 0.5) * 0.1;
                oy = rand.nextDouble() * 0.2;
                oz = (rand.nextDouble() - 0.5) * 0.1;
                break;

            case REDSTONE:
                ox = 0.8;
                oy = 0;
                oz = 0.8;
                break;

            default:
                ox = 0;
                oy = 0;
                oz = 0;
        }

        // use generic random box spawning
        double x = this.posX + (rand.nextDouble() - 0.5) * this.width * s;
        double y = this.posY + (rand.nextDouble() - 0.5) * this.height * s;
        double z = this.posZ + (rand.nextDouble() - 0.5) * this.width * s;

        this.world.spawnParticle(type, x, y, z, ox, oy, oz);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().grow(2.0, 1.0, 2.0);
    }

    /// Unsupported Operation
    @Override
    public boolean canMateWith(EntityAnimal mate) {
        return false;
    }

    /// Unsupported Operation
    @Override
    public TameableDragonEntity createChild(EntityAgeable mate) {
        return null;
    }
}
