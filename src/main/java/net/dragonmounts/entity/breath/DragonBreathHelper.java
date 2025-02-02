package net.dragonmounts.entity.breath;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.client.render.dragon.breathweaponFX.BreathWeaponEmitter;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.sound.SoundController;
import net.dragonmounts.entity.breath.sound.SoundEffectBreathWeapon;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.entity.helper.DragonHelper;
import net.dragonmounts.registry.DragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by TGG on 8/07/2015.
 * Responsible for
 * - retrieving the player's selected target (based on player's input from Dragon Orb item)
 * - synchronising the player-selected target between server AI and client copy - using datawatcher
 * - rendering the breath weapon on the client
 * - performing the effects of the weapon on the server (eg burning blocks, causing damage)
 * The selection of an actual target (typically - based on the player desired target), navigation of dragon to the appropriate range,
 * turning the dragon to face the target, is done by targeting AI.
 * DragonBreathHelper is also responsible for
 * - tracking the current breath state (IDLE, STARTING, SUSTAINED BREATHING, STOPPING)
 * - sound effects
 * - adding delays for jaw open / breathing start
 * - interrupting the beam when the dragon is facing the wrong way / the angle of the beam mismatches the head angle
 * Usage:
 * 1) Create instance, providing the parent dragon entity and a datawatcher index to use for breathing
 * 2) call onLivingUpdate(), onDeath(), onDeathUpdate(), readFromNBT() and writeFromNBT() from the corresponding
 * parent entity methods
 * 3a) The AI task responsible for targeting should call getPlayerSelectedTarget() to find out what the player wants
 * the dragon to target.
 * 3b) Once the target is in range and the dragon is facing the correct direction, the AI should use setBreathingTarget()
 * to commence breathing at the target
 * 4) getCurrentBreathState() and getBreathStateFractionComplete() should be called by animation routines for
 * the dragon during breath weapon (eg jaw opening)
 */
public class DragonBreathHelper extends DragonHelper {

    private final int BREATH_START_DURATION=5; // ticks
    private final int BREATH_STOP_DURATION=5; // ticks
    private BreathState currentBreathState=BreathState.IDLE;
    private int transitionStartTick;
    private int tickCounter=0;
    protected BreathWeaponEmitter breathWeaponEmitter=null;
    public final BreathAffectedArea breathAffectedArea;
    private BreathWeapon weapon;
    private static final Logger L=LogManager.getLogger();

    public DragonBreathHelper(TameableDragonEntity dragon, DataParameter<String> i_dataParamBreathWeaponTarget, DataParameter<Integer> i_dataParamBreathWeaponMode) {
        super(dragon);
        if (dragon.isClient()) {
            breathWeaponEmitter=new BreathWeaponEmitter();
        }
        //dataWatcher.register(dataParamBreathWeaponTarget, "");  //already registered by caller
        breathAffectedArea = new BreathAffectedArea();
    }

    public enum BreathState {IDLE, STARTING, SUSTAIN, STOPPING}

    public BreathState getCurrentBreathState() {
        return currentBreathState;
    }

    public float getBreathStateFractionComplete() {
        switch (currentBreathState) {
            case IDLE:
            case SUSTAIN:
                return 0.0F;
            case STARTING: {
                int ticksSpentStarting=tickCounter - transitionStartTick;
                return MathHelper.clamp(ticksSpentStarting / (float) BREATH_START_DURATION, 0.0F, 1.0F);
            }
            case STOPPING: {
                int ticksSpentStopping=tickCounter - transitionStartTick;
                return MathHelper.clamp(ticksSpentStopping / (float) BREATH_STOP_DURATION, 0.0F, 1.0F);
            }
            default: {
                DragonMounts.loggerLimit.error_once("Unknown currentBreathState:" + currentBreathState);
                return 0.0F;
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        ++tickCounter;
        if (dragon!=null) {
            if (dragon.world.isRemote) {
                onLivingUpdateClient();
            } else {
                onLivingUpdateServer();
            }
        }
    }

    private void onLivingUpdateServer() {
        TameableDragonEntity dragon = this.dragon;
        updateBreathState(dragon.isUsingBreathWeapon());

        if (this.weapon != null && dragon.isUsingBreathWeapon()) {
            Vec3d origin=dragon.getAnimator().getThroatPosition();
            Vec3d lookDirection=dragon.getLook(1.0f);
            Vec3d endOfLook=origin.add(lookDirection.x, lookDirection.y, lookDirection.z);
            BreathNode.Power power=dragon.getLifeStageHelper().getBreathPower();
            if (currentBreathState == BreathState.SUSTAIN) {
                this.breathAffectedArea.continueBreathing(dragon.world, origin, endOfLook, power);
                this.breathAffectedArea.updateTick(dragon.world, this.weapon);
            }
        }
    }

    private void onLivingUpdateClient() {
        TameableDragonEntity dragon = this.dragon;
        if (this.weapon == null) {
            this.currentBreathState = BreathState.IDLE;
            return;
        }
        updateBreathState(dragon.isUsingBreathWeapon());
        if (this.currentBreathState == BreathState.SUSTAIN) {
            this.getEmitter().spawnBreathParticles(
                    dragon.world,
                    dragon.getAnimator().getThroatPosition(),
                    dragon.getLook(1.0f),
                    dragon.getLifeStageHelper().getBreathPower(),
                    this.tickCounter,
                    dragon.getVariant().type
            );
        }

        if (soundEffectBreathWeapon==null) {
            soundEffectBreathWeapon=new SoundEffectBreathWeapon(getSoundController(dragon.getEntityWorld()), weaponInfoLink);
        }
        soundEffectBreathWeapon.performTick(Minecraft.getMinecraft().player, dragon);
    }

    private void updateBreathState(boolean isBreathing) {
        switch (currentBreathState) {
            case IDLE: {
                if (isBreathing) {
                    transitionStartTick=tickCounter;
                    currentBreathState=BreathState.STARTING;
                }
                break;
            }
            case STARTING: {
                int ticksSpentStarting=tickCounter - transitionStartTick;
                if (ticksSpentStarting >= BREATH_START_DURATION) {
                    transitionStartTick=tickCounter;
                    currentBreathState = isBreathing ? BreathState.SUSTAIN : BreathState.STOPPING;
                }
                break;
            }
            case SUSTAIN: {
                if (!isBreathing) {
                    transitionStartTick=tickCounter;
                    currentBreathState=BreathState.STOPPING;
                }
                break;
            }
            case STOPPING: {
                int ticksSpentStopping=tickCounter - transitionStartTick;
                if (ticksSpentStopping >= BREATH_STOP_DURATION) {
                    currentBreathState=BreathState.IDLE;
                }
                break;
            }
            default: {
                DragonMounts.loggerLimit.error_once("Unknown currentBreathState:" + currentBreathState);
                return;
            }
        }
    }

    public SoundController getSoundController(World world) {
        if (!world.isRemote) {
            throw new IllegalArgumentException("getSoundController() only valid for WorldClient");
        }
        if (soundController==null) {
            soundController = new SoundController();
        }

        return soundController;
    }

    private SoundController soundController;
    private SoundEffectBreathWeapon soundEffectBreathWeapon;
    private WeaponInfoLink weaponInfoLink=new WeaponInfoLink();

    // Callback link to provide the Sound generator with state information
    public class WeaponInfoLink implements SoundEffectBreathWeapon.WeaponSoundUpdateLink {

        @Override
        public boolean refreshWeaponSoundInfo(SoundEffectBreathWeapon.WeaponSoundInfo infoToUpdate) {
            Vec3d origin=dragon.getAnimator().getThroatPosition();
            infoToUpdate.dragonHeadLocation=origin;
            infoToUpdate.relativeVolume=dragon.getScale();
            infoToUpdate.lifeStage=dragon.getLifeStageHelper().getLifeStage();
            infoToUpdate.breathingState = DragonBreathHelper.this.weapon != null && dragon.isUsingBreathWeapon() && currentBreathState == BreathState.SUSTAIN
                    ? SoundEffectBreathWeapon.WeaponSoundInfo.State.BREATHING
                    : SoundEffectBreathWeapon.WeaponSoundInfo.State.IDLE;
            return true;
        }
    }

    public BreathWeaponEmitter getEmitter() {
        return breathWeaponEmitter;
    }

    public void onBreedChange(DragonType type) {
        this.weapon = type.createBreathWeapon(this.dragon);
    }

    public boolean hasWeapon() {
        return this.weapon != null;
    }
}