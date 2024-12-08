package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathNode;
import net.dragonmounts.entity.breath.effects.WitherBreathFX;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.entity.breath.weapons.BreathWeaponWither;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

public class WitherType extends DragonType {
    public WitherType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        World level = dragon.world;
        if (level instanceof WorldServer && !dragon.isDead && dragon.isUsingBreathWeapon() && !dragon.isEgg()) {
            ((WorldServer) level).spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    dragon.posX,
                    dragon.posY + dragon.getEyeHeight(),
                    dragon.posZ,
                    1,
                    0.5,
                    0.25,
                    0.5,
                    0.0
            );
        }
    }

    @Nullable
    @Override
    public BreathWeapon createBreathWeapon(TameableDragonEntity dragon) {
        return new BreathWeaponWither(dragon);
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isBaby() ? DMSounds.ENTITY_DRAGON_HATCHLING_GROWL : DMSounds.ENTITY_NETHER_DRAGON_GROWL;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new WitherBreathFX(world, position, direction, power, partialTicks));
    }
}
