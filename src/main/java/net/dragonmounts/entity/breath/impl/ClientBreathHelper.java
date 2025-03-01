package net.dragonmounts.entity.breath.impl;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.render.dragon.breathweaponFX.BreathWeaponEmitter;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.dragonmounts.entity.breath.sound.SoundController;
import net.dragonmounts.entity.breath.sound.SoundEffectBreathWeapon;
import net.minecraft.client.Minecraft;

public class ClientBreathHelper extends DragonBreathHelper<ClientDragonEntity> implements SoundEffectBreathWeapon.WeaponSoundUpdateLink {
    protected final BreathWeaponEmitter breathWeaponEmitter = new BreathWeaponEmitter();
    private SoundController soundController;
    private SoundEffectBreathWeapon soundEffectBreathWeapon;

    public ClientBreathHelper(ClientDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void update() {
        ++tickCounter;
        ClientDragonEntity dragon = this.dragon;
        if (this.breath == null) {
            this.currentBreathState = BreathState.IDLE;
            return;
        }
        updateBreathState(dragon.isUsingBreathWeapon());
        if (this.currentBreathState == BreathState.SUSTAIN) {
            this.breathWeaponEmitter.spawnBreathParticles(
                    dragon.world,
                    dragon.getThroatPosition(),
                    dragon.getLook(1.0F),
                    dragon.lifeStageHelper.getLifeStage().power,
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
        infoToUpdate.dragonHeadLocation = dragon.getThroatPosition();
        infoToUpdate.relativeVolume = dragon.getScale();
        infoToUpdate.lifeStage = dragon.lifeStageHelper.getLifeStage();
        infoToUpdate.breathingState = this.breath != null && dragon.isUsingBreathWeapon() && currentBreathState == BreathState.SUSTAIN
                ? SoundEffectBreathWeapon.WeaponSoundInfo.State.BREATHING
                : SoundEffectBreathWeapon.WeaponSoundInfo.State.IDLE;
        return true;
    }
}
