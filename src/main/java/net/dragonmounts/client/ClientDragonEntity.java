package net.dragonmounts.client;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.capability.DMCapabilities;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.capability.IHardShears;
import net.dragonmounts.client.model.dragon.DragonAnimator;
import net.dragonmounts.entity.Relation;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.impl.ClientBreathHelper;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMKeyBindings;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.network.CDragonBreathPacket;
import net.dragonmounts.util.ItemUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClientDragonEntity extends TameableDragonEntity {
    public final DragonAnimator animator = new DragonAnimator(this);
    public boolean isInGui = false;

    public ClientDragonEntity(World level) {
        super(level);
    }

    @Override
    protected ClientBreathHelper createBreathHelper() {
        return new ClientBreathHelper(this);
    }

    @Override
    public Vec3d getThroatPosition() {
        return this.animator.getThroatPosition();
    }

    @Override
    public void onLivingUpdate() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && player == this.getRidingEntity()) {
            DragonMounts.NETWORK_WRAPPER.sendToServer(new CDragonBreathPacket(
                    this.getEntityId(),
                    DMKeyBindings.KEY_BREATH.isKeyDown()
            ));
        }
        this.variantHelper.update();
        this.lifeStageHelper.ageUp(1);
        this.breathHelper.update();
        this.getVariant().type.tick(this);
        this.animator.update();
        if (!this.isDead) {
            if (this.healingEnderCrystal != null && this.healingEnderCrystal.isDead) {
                this.healingEnderCrystal = null;
            }
            if (this.rand.nextInt(10) == 0) {
                EntityEnderCrystal target = null;
                double min = Double.MAX_VALUE;
                for (EntityEnderCrystal crystal : this.world.getEntitiesWithinAABB(
                        EntityEnderCrystal.class,
                        this.getEntityBoundingBox().grow(32.0D)
                )) {
                    double distance = crystal.getDistanceSq(this);
                    if (distance < min) {
                        min = distance;
                        target = crystal;
                    }
                }
                this.healingEnderCrystal = target;
            }
        }
        EnumParticleTypes sneeze = this.getVariant().type.sneezeParticle;
        if (sneeze != null && rand.nextInt(700) == 0 && !this.isUsingBreathWeapon() && this.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE)) {
            Vec3d throatPos = this.getThroatPosition();
            double throatPosX = throatPos.x;
            double throatPosY = throatPos.y;
            double throatPosZ = throatPos.z;
            int floorY = MathHelper.floor(throatPosY);
            MutableBlockPosEx pos = new MutableBlockPosEx(MathHelper.floor(throatPosX), floorY, MathHelper.floor(throatPosZ));
            for (int i = -1; i < 2; ++i) {
                world.spawnParticle(sneeze, throatPosX, throatPosY + i, throatPosZ, 0, 0.3, 0);
                world.playSound(null, pos.withY(floorY + i), DMSounds.DRAGON_SNEEZE, SoundCategory.NEUTRAL, 0.8F, 1);
            }
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
        final boolean isTrusted = relation != Relation.STRANGER;
        if (!stack.isEmpty()) {
            final boolean oldEnough = !stage.isBaby();
            if (oldEnough && this.canShare()) {
                IHardShears shears = stack.getCapability(DMCapabilities.HARD_SHEARS, null);
                if (shears != null && shears.canShear(stack, player, this)) return true;
            }
            if (isTrusted && ((
                    this.onGround && ItemUtil.anyMatches(stack, "stickWood", "bone")
            ) || (oldEnough && (
                    this.canCollectBreath() && stack.getItem() == Items.GLASS_BOTTLE
            ) || (
                    !this.isSaddled() && stack.getItem() == Items.SADDLE
            )))) return true;
            IDragonFood food = stack.getCapability(DMCapabilities.DRAGON_FOOD, null);
            if (food != null && food.tryFeed(this, player, relation, stack, hand)) return true;
        }
        return stack.interactWithEntity(player, this, hand) || isTrusted;
    }

    @Override
    protected void onDeathUpdate() {
        // freeze at place
        motionX = motionY = motionZ = 0;
        rotationYaw = prevRotationYaw;
        rotationYawHead = prevRotationYawHead;

        if (isEgg() || ++deathTime > getMaxDeathTime()) setDead();// actually delete entity after the time is up

        if (deathTime < getMaxDeathTime() - 20) {
            int amount = (int) (4 * this.getScale());
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
                float value = getScale();
                this.world.playSound(posX, posY, posZ, sound, SoundCategory.NEUTRAL, MathHelper.clamp(value, 0.3F, 0.6F), getSoundPitch(), true);
                this.animator.ticksSinceLastRoar = 0;
                break;

            default:
                super.handleStatusUpdate(id);
        }
    }

    public void spawnBodyParticle(EnumParticleTypes type) {
        double ox, oy, oz;
        float s = this.getScale() * 1.2f;

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
