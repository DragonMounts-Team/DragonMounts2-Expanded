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
import net.dragonmounts.util.math.MathX;
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
    public void setLifeStage(DragonLifeStage stage, boolean reset, boolean sync) {
        this.applyStage(stage);
        if (this.stage == stage) return;
        this.stage = stage;
        if (reset) {
            this.refreshAge();
        }
        this.updateScale();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(DragonVariant.SERIALIZATION_KEY)) {
            this.setVariant(DragonVariant.byName(nbt.getString(DragonVariant.SERIALIZATION_KEY)));
        } else if (nbt.hasKey(DragonType.SERIALIZATION_KEY)) {
            this.overrideType(DragonType.byName(nbt.getString(DragonType.SERIALIZATION_KEY)));
        }
        super.readEntityFromNBT(nbt);
        if (this.firstUpdate) {
            this.variantHelper.onVariantChanged(this.getVariant());
        }
    }

    @Override
    protected void tickAsEgg() {
        this.variantHelper.update();
        if (--this.wobbling > 0) {
            this.amplitudeO = this.amplitude;
            this.amplitude = MathHelper.sin(this.world.getWorldTime() * 0.5F) * Math.min(this.wobbling, 15);
        }
        super.onLivingUpdate();
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
        this.breathHelper.update();
        this.getVariant().type.tickClient(this);
        if (this.isEgg()) {
            this.tickAsEgg();
            return;
        }
        this.animator.update();
        if (!this.isDead) {
            if (this.healingEnderCrystal != null && this.healingEnderCrystal.isDead) {
                this.healingEnderCrystal = null;
            }
            this.findCrystal();
        }
        this.ageUp(1, false);
        super.onLivingUpdate();
        EnumParticleTypes sneeze = this.getVariant().type.sneezeParticle;
        if (sneeze != null && this.stage.isOldEnough(DragonLifeStage.FLEDGLING) && !this.isUsingBreathWeapon() && this.rand.nextInt(700) == 0) {
            Vec3d pos = this.getHeadRelativeOffset(0.0F, 4.0F, 22.0F);
            double x = pos.x, y = pos.y, z = pos.z;
            for (int i = -1; i < 1; ++i) {
                world.spawnParticle(sneeze, x, y + 0.5 * i, z, 0, 0.3, 0);
            }
            world.playSound(null, x, y, z, DMSounds.DRAGON_SNEEZE, SoundCategory.NEUTRAL, 0.8F, 1);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        DragonLifeStage stage = this.stage;
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
            int amount = (int) (4 * this.getAgingScale());
            for (int i = 0; i < amount; i++) {
                spawnBodyParticle(EnumParticleTypes.CLOUD);
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (this.isEgg()) {
            this.playEggCrackEffect();
        }
        super.onDeath(cause);
    }

    public void onWingsDown(float speed) {
        // play wing sounds
        this.playSound(getWingsSound(), 0.8f + (this.getAgingScale() - speed), 1.0F, true);
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
                this.world.playSound(posX, posY, posZ, sound, SoundCategory.NEUTRAL, MathHelper.clamp(this.getAgingScale(), 0.3F, 0.6F), getSoundPitch(), true);
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

    public void applyWobble(int amplitude, int axis, boolean crack) {
        this.wobbling = amplitude;
        this.wobbleAxis = axis;
        // use game time to make amplitude consistent between clients
        float target = MathHelper.sin(this.world.getWorldTime() * 0.5F) * Math.min(amplitude, 15);
        // multiply with a factor to make it smoother
        this.amplitudeO = target * 0.25F;
        this.amplitude = target * 0.75F;
        if (crack) {
            this.playEggCrackEffect();
        }
        this.world.playSound(null, this.getPosition(), DMSounds.DRAGON_EGG_CRACK, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public float getWobbleAxis() {
        return this.wobbleAxis;
    }

    public float getAmplitude(float partialTicks) {
        return this.wobbling > 0 ? MathX.lerp(this.amplitudeO, this.amplitude, partialTicks) : 0;
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

    @Override
    public void setGrowingAge(int age) {
        this.growingAge = age;
        this.updateScale();
    }
}
