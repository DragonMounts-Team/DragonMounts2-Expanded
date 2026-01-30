package net.dragonmounts.init;

import net.dragonmounts.api.IDescribedArmorEffect;
import net.dragonmounts.api.IDescribedArmorEffect.Advanced;
import net.dragonmounts.capability.IArmorEffectManager;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.network.SRiposteEffectPacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

import static net.dragonmounts.DragonMounts.NETWORK_WRAPPER;
import static net.dragonmounts.DragonMounts.makeId;
import static net.dragonmounts.capability.DMCapabilities.ARMOR_EFFECT_MANAGER;
import static net.dragonmounts.util.EntityUtil.addOrMergeEffect;
import static net.dragonmounts.util.EntityUtil.addOrResetEffect;

public class DMArmorEffects {
    public static final String FISHING_LUCK = "tooltip.dragonmounts.armor_effect_fishing_luck";
    public static final float EXP_BONUS_FACTOR = 2.0F * (float) Math.log10(4.0);

    public static final Advanced AETHER_EFFECT = new Advanced(makeId("aether"), 300) {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            World world = player.world;
            if (flag && !world.isRemote && manager.getCooldown(this) <= 0 && player.isSprinting() && addOrMergeEffect(player, MobEffects.SPEED, 100, 1, true, true)) {
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GUARDIAN_HURT, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                manager.setCooldown(this, this.cooldown);
            }
            return flag;
        }
    };

    public static final IDescribedArmorEffect ENCHANTED_EFFECT = new IDescribedArmorEffect() {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            World world = player.world;
            if (world.isRemote) {
                Random random = player.getRNG();
                double x = player.posX;
                double y = player.posY + 1.5;
                double z = player.posZ;
                for (int i = -2; i <= 2; ++i) {
                    for (int j = -2; j <= 2; ++j) {
                        if (i > -2 && i < 2 && j == -1) j = 2;
                        if (random.nextInt(30) == 0) {
                            for (int k = 0; k <= 1; ++k) {
                                world.spawnParticle(
                                        EnumParticleTypes.ENCHANTMENT_TABLE,
                                        x,
                                        y + random.nextFloat(),
                                        z,
                                        i + random.nextFloat() - 0.5D,
                                        k - random.nextFloat() - 1.0F,
                                        j + random.nextFloat() - 0.5D
                                );
                            }
                        }
                    }
                }
            }
            return level > 3;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, "tooltip.armor_effect.dragonmounts.enchanted"));
        }
    };

    public static final Advanced ENDER_EFFECT = new Advanced(makeId("ender"), 1200) {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            if (player.world.isRemote) {
                Random random = player.getRNG();
                player.world.spawnParticle(
                        EnumParticleTypes.PORTAL,
                        player.posX + random.nextFloat() - 0.3,
                        player.posY + random.nextFloat() - 0.3,
                        player.posZ + random.nextFloat() - 0.3,
                        random.nextFloat() * 2 - 0.15,
                        random.nextFloat() * 2 - 0.15,
                        random.nextFloat() * 2 - 0.15
                );
                return level > 3;
            }
            // use `|` instead of `||` to avoid short-circuit evaluation when trying to add both of these two effects
            if (level > 3 && manager.isAvailable(this) && player.getHealth() < 5 && (
                    addOrMergeEffect(player, MobEffects.RESISTANCE, 600, 2, true, true)
                            | addOrMergeEffect(player, MobEffects.STRENGTH, 300, 0, true, true)
            )) {
                player.world.playEvent(2003, player.getPosition(), 0);
                player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 0.047f, 1f);
                manager.setCooldown(this, this.cooldown);
                return true;
            }
            return false;
        }
    };

    public static final Advanced FIRE_EFFECT = new Advanced(makeId("fire"), 900) {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            if (flag && !player.world.isRemote && manager.isAvailable(this) && player.isBurning()) {
                if (addOrMergeEffect(player, MobEffects.FIRE_RESISTANCE, 600, 0, true, true)) {
                    manager.setCooldown(this, this.cooldown);
                }
                player.extinguish();
            }
            return flag;
        }
    };

    public static final Advanced FOREST_EFFECT = new Advanced(makeId("forest"), 900) {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            if (flag && !player.world.isRemote && player.fishEntity != null) {
                addOrResetEffect(player, MobEffects.LUCK, 200, 0, true, true, 21);
            }
            return flag;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, FISHING_LUCK));
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, this.description));
            this.appendCooldownInfo(tooltips);
        }
    };

    public static final Advanced ICE_EFFECT = new Advanced(makeId("ice"), 1200);

    public static final IDescribedArmorEffect MOONLIGHT_EFFECT = new IDescribedArmorEffect() {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            if (flag && !player.world.isRemote) {
                addOrResetEffect(player, MobEffects.NIGHT_VISION, 600, 0, true, true, 201);
            }
            return flag;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, "tooltip.armor_effect.dragonmounts.moonlight"));
        }
    };

    public static final Advanced NETHER_EFFECT = new Advanced(makeId("nether"), 1200);

    public static final IDescribedArmorEffect STORM_EFFECT = new IDescribedArmorEffect() {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            return level > 3;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, "tooltip.armor_effect.dragonmounts.storm"));
        }
    };

    public static final Advanced SUNLIGHT_EFFECT = new Advanced(makeId("sunlight"), 1160) {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            /* original code:
                if (player.getCooldownTracker().getCooldown(this, 0) > 0 && (player.getFoodStats().getFoodLevel() < 10f))
                    return; // check this after because luck should not be a perma effect
                if (!isActive(MobEffects.SATURATION, player) && world.isDaytime()) {
                    player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 200, 1, false, true));
                    player.getCooldownTracker().setCooldown(this, 1160); // you could survive without eating with this one
                }
             */
            if (flag && !player.world.isRemote) {
                if (player.fishEntity != null) {
                    addOrResetEffect(player, MobEffects.LUCK, 200, 0, true, true, 21);
                }
                if (manager.isAvailable(this) && player.getFoodStats().getFoodLevel() < 10 && addOrMergeEffect(player, MobEffects.SATURATION, 200, 0, true, true)) {
                    this.applyCooldown(manager);
                }
            }
            return flag;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, FISHING_LUCK));
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, this.description));
            this.appendCooldownInfo(tooltips);
        }
    };

    public static final IDescribedArmorEffect TERRA_EFFECT = new IDescribedArmorEffect() {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            if (flag && !player.world.isRemote) {
                addOrResetEffect(player, MobEffects.HASTE, 600, 0, true, true, 201);
            }
            return flag;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, "tooltip.armor_effect.dragonmounts.terra"));
        }
    };

    public static final IDescribedArmorEffect WATER_EFFECT = new IDescribedArmorEffect() {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            if (flag && !player.world.isRemote && player.isInWater()) {
                addOrResetEffect(player, MobEffects.WATER_BREATHING, 600, 0, true, true, 201);
            }
            return flag;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, "tooltip.armor_effect.dragonmounts.water"));
        }
    };

    public static final Advanced ZOMBIE_EFFECT = new Advanced(makeId("zombie"), 400) {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            boolean flag = level > 3;
            if (flag && !player.world.isRemote && !player.world.isDaytime() && manager.isAvailable(this) && addOrMergeEffect(player, MobEffects.STRENGTH, 300, 0, true, true)) {
                this.applyCooldown(manager);
            }
            return flag;
        }
    };

    public static final IDescribedArmorEffect DARK_EFFECT = new IDescribedArmorEffect() {
        @Override
        public boolean activate(IArmorEffectManager manager, EntityPlayer player, int level) {
            if (level > 3) {
                addOrResetEffect(player, DMMobEffects.DARK_DRAGONS_GRACE, 600, 0, true, true, 201);
                return true;
            }
            return false;
        }

        @Override
        public void appendHoverText(ItemStack stack, List<String> tooltips, ITooltipFlag flag) {
            tooltips.add("");
            this.appendTriggerInfo(stack, tooltips);
            tooltips.add(ClientUtil.translateToLocal(TextFormatting.RESET, "tooltip.armor_effect.dragonmounts.dark"));
        }
    };

    @SubscribeEvent
    public static void onExpDrop(LivingExperienceDropEvent event) {
        EntityPlayer source = event.getAttackingPlayer();
        if (source == null) return;
        IArmorEffectManager manager = source.getCapability(ARMOR_EFFECT_MANAGER, null);
        if (manager == null || !manager.isActive(ENCHANTED_EFFECT)) return;
        int base = event.getDroppedExperience();
        event.setDroppedExperience(base + Math.round(base * EXP_BONUS_FACTOR));
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        final EntityLivingBase self = event.getEntityLiving();
        //In fact, self.world.isRemote -> false
        if (self.world.isRemote) return;
        Entity source = event.getSource().getTrueSource();
        if (source != null) {
            //noinspection DataFlowIssue
            IArmorEffectManager manager = source.getCapability(ARMOR_EFFECT_MANAGER, null);
            if (manager != null && manager.isActive(STORM_EFFECT) && self.getRNG().nextFloat() < 0.05F) {
                self.world.addWeatherEffect(new EntityLightningBolt(self.world, self.posX, self.posY, self.posZ, false));
            }
        }
        IArmorEffectManager manager = self.getCapability(ARMOR_EFFECT_MANAGER, null);
        if (manager == null) return;
        if (manager.isActive(FOREST_EFFECT) && manager.isAvailable(FOREST_EFFECT) && addOrMergeEffect(self, MobEffects.REGENERATION, 200, 1, true, true)) {
            FOREST_EFFECT.applyCooldown(manager);
        }
        final boolean ice = manager.isActive(ICE_EFFECT) && manager.isAvailable(ICE_EFFECT);
        final boolean nether = manager.isActive(NETHER_EFFECT) && manager.isAvailable(NETHER_EFFECT);
        if (!ice && !nether) return;
        final List<Entity> entities = self.world.getEntitiesInAABBexcluding(self, self.getEntityBoundingBox().grow(5.0D), EntitySelectors.CAN_AI_TARGET);
        if (entities.isEmpty()) return;
        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) entity;
                target.knockBack(entity, 0.4F, 1, 1);
                if (ice) {
                    addOrMergeEffect(target, MobEffects.SLOWNESS, 200, 1, false, true);
                    entity.hurtResistantTime = 0;
                    entity.attackEntityFrom(DamageSource.GENERIC, 1F);
                }
            } else if (ice) {
                entity.hurtResistantTime = 0;
                entity.attackEntityFrom(DamageSource.GENERIC, 1F);
            }
            if (nether) {
                entity.setFire(10);
            }
        }
        if (ice) ICE_EFFECT.applyCooldown(manager);
        if (nether) NETHER_EFFECT.applyCooldown(manager);
        SRiposteEffectPacket packet = new SRiposteEffectPacket(self.getEntityId(), (ice ? 0b01 : 0b00) | (nether ? 0b10 : 0b00));
        NETWORK_WRAPPER.sendToAllTracking(packet, self);
        if (self instanceof EntityPlayerMP) {
            NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) self);
        }
    }
}
