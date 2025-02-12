package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.client.render.dragon.breathweaponFX.BreathWeaponEmitter;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.dragonmounts.entity.breath.sound.SoundController;
import net.dragonmounts.entity.breath.sound.SoundEffectBreathWeapon;
import net.minecraft.client.Minecraft;

public class ClientBreathHelper extends DragonBreathHelper implements SoundEffectBreathWeapon.WeaponSoundUpdateLink {
    protected final BreathWeaponEmitter breathWeaponEmitter = new BreathWeaponEmitter();
    private SoundController soundController;
    private SoundEffectBreathWeapon soundEffectBreathWeapon;

    public ClientBreathHelper(TameableDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void onLivingUpdate() {
        ++tickCounter;
        TameableDragonEntity dragon = this.dragon;
        if (this.breath == null) {
            this.currentBreathState = BreathState.IDLE;
            return;
        }
        updateBreathState(dragon.isUsingBreathWeapon());
        if (this.currentBreathState == BreathState.SUSTAIN) {
            this.breathWeaponEmitter.spawnBreathParticles(
                    dragon.world,
                    dragon.getAnimator().getThroatPosition(),
                    dragon.getLook(1.0F),
                    dragon.getLifeStageHelper().getLifeStage().power,
                    this.tickCounter,
                    dragon.getVariant().type
            );
        }
        if (this.soundEffectBreathWeapon == null) {
            this.soundEffectBreathWeapon = new SoundEffectBreathWeapon(new SoundController(), this);
        }
        this.soundEffectBreathWeapon.performTick(Minecraft.getMinecraft().player, dragon);
    }

    @Override
    public boolean refreshWeaponSoundInfo(SoundEffectBreathWeapon.WeaponSoundInfo infoToUpdate) {
        infoToUpdate.dragonHeadLocation = dragon.getAnimator().getThroatPosition();
        infoToUpdate.relativeVolume = dragon.getScale();
        infoToUpdate.lifeStage = dragon.getLifeStageHelper().getLifeStage();
        infoToUpdate.breathingState = this.breath != null && dragon.isUsingBreathWeapon() && currentBreathState == BreathState.SUSTAIN
                ? SoundEffectBreathWeapon.WeaponSoundInfo.State.BREATHING
                : SoundEffectBreathWeapon.WeaponSoundInfo.State.IDLE;
        return true;
    }
}
