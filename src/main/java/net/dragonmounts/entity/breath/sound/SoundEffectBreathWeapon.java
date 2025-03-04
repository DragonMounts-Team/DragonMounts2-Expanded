package net.dragonmounts.entity.breath.sound;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.util.LogUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by TheGreyGhost on 8/10/14.
 *
 * Used to create sound effects for the breath weapon tool - start up, sustained loop, and wind-down
 * The sound made by the dragon's head
 *   1) initial startup
 *   2) looping while breathing
 *   3) stopping when done
 *  Sometimes the sound doesn't layer properly on the first try.  I don't know why.  I have implemented a preload
 *    which seems to help.
 *
 * The SoundEffectBreathWeapon corresponds to the breath weapon of a single dragon.  Typical usage is:
 * 1) create an instance, and provide a callback function (WeaponSoundUpdateLink)
 * 2) startPlaying(), startPlayingIfNotAlreadyPlaying(), stopPlaying() to start or stop the sounds completely
 * 3) once per tick, call performTick().
 *   3a) performTick() will call the WeaponSoundUpdateLink.refreshWeaponSoundInfo(), which should return the
 *       current data relevant to the sound (eg whether the dragon is breathing, and the location of the beam)
 *
 * Is intended to be subclassed for future different breath weapons.
 *
 */
public class SoundEffectBreathWeapon {

  SoundContext.State perviousState = SoundContext.State.IDLE;

  private ComponentSoundSettings headSoundSettings = new ComponentSoundSettings(1.0F);

  private BreathWeaponSound headStartupSound;
  private BreathWeaponSound headLoopSound;
  private BreathWeaponSound headStoppingSound;

  private final float HEAD_MIN_VOLUME = 0.006F;

  private void stopAllHeadSounds() {
    SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
    if (headStartupSound != null) {
      handler.stopSound(headStartupSound);
      headStartupSound = null;
    }
    if (headLoopSound != null) {
      handler.stopSound(headLoopSound);
      headLoopSound = null;
    }
    if (headStoppingSound != null) {
      handler.stopSound(headStoppingSound);
      headStoppingSound = null;
    }
  }


  public void setAllStopFlags() {
    if (headStartupSound != null) { headStartupSound.setDonePlaying();}
    if (headLoopSound != null) { headLoopSound.setDonePlaying();}
    if (headStoppingSound != null) { headStoppingSound.setDonePlaying();}
  }

  /**
   * Updates all the component sounds according to the state of the breath weapon.
   */
  public void performTick(EntityPlayerSP entityPlayerSP, TameableDragonEntity dragon, SoundContext weaponSoundInfo) {
    checkNotNull(weaponSoundInfo.dragonHeadLocation);
    headSoundSettings.playing = true;
    headSoundSettings.masterVolume = weaponSoundInfo.relativeVolume;
    headSoundSettings.soundEpicentre = weaponSoundInfo.dragonHeadLocation;
    headSoundSettings.playerDistanceToEpicentre =
            (float) weaponSoundInfo.dragonHeadLocation.distanceTo(entityPlayerSP.getPositionVector());

    final int HEAD_STARTUP_TICKS = 40;
    final int HEAD_STOPPING_TICKS = 60;

    // if state has changed, stop and start component sounds appropriately
    SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
    if (weaponSoundInfo.breathingState != perviousState) {
      switch (weaponSoundInfo.breathingState) {
        case IDLE: {
          stopAllHeadSounds();
          headStoppingSound = new BreathWeaponSound(weaponSoundInfo.breath.getStopSound(weaponSoundInfo.lifeStage).getSoundName(),
                  HEAD_MIN_VOLUME, RepeatType.NO_REPEAT,
                  headSoundSettings);
          headStoppingSound.setPlayCountdown(HEAD_STOPPING_TICKS);

          handler.playSound(headStoppingSound);
          break;
        }
        case BREATHING: {
          stopAllHeadSounds();
          BreathWeaponSound preloadLoop = new BreathWeaponSound(weaponSoundInfo.breath.getLoopSound(weaponSoundInfo.lifeStage).getSoundName(),
                  Mode.PRELOAD);
          handler.playSound(preloadLoop);
          BreathWeaponSound preLoadStop = new BreathWeaponSound(weaponSoundInfo.breath.getStopSound(weaponSoundInfo.lifeStage).getSoundName(),
                  Mode.PRELOAD);
          handler.playSound(preLoadStop);
          headStartupSound = new BreathWeaponSound(weaponSoundInfo.breath.getStartSound(weaponSoundInfo.lifeStage).getSoundName(),
                  HEAD_MIN_VOLUME, RepeatType.NO_REPEAT,
                  headSoundSettings);
          headStartupSound.setPlayCountdown(HEAD_STARTUP_TICKS);
          handler.playSound(headStartupSound);
          break;
        }
        default: {
          LogUtil.once(Level.ERROR,
                  "Illegal weaponSoundInfo.breathingState:" + weaponSoundInfo.breathingState + " in " + this
                          .getClass());
        }
      }
      perviousState = weaponSoundInfo.breathingState;
    }

    // update component sound settings based on weapon info and elapsed time

    switch (perviousState /* current state */) {
      case BREATHING: {
        if (headStartupSound != null && headStartupSound.getPlayCountdown() <= 0) {
          stopAllHeadSounds();
          headLoopSound = new BreathWeaponSound(weaponSoundInfo.breath.getLoopSound(weaponSoundInfo.lifeStage).getSoundName(),
                  HEAD_MIN_VOLUME, RepeatType.REPEAT, headSoundSettings);
          handler.playSound(headLoopSound);
        }

        break;
      }
      case IDLE: {
        if (headStoppingSound != null) {
          if (headStoppingSound.getPlayCountdown() <= 0) {   //|| !soundController.isSoundPlaying(headStoppingSound)) {  causes strange bug "channel null in method 'stop'"
            handler.stopSound(headStoppingSound);
            headStoppingSound = null;
          }
        }
        break;
      }
      default: {
        LogUtil.once(Level.ERROR, "Unknown currentWeaponState:" + perviousState);
      }
    }
  }

  public static class SoundContext {
    public enum State {IDLE, BREATHING}

    public SoundContext.State breathingState = SoundContext.State.IDLE;
    public Collection<Vec3d> pointsWithinBeam;
    public Vec3d dragonHeadLocation;
    public float relativeVolume; // 0 to 1
    public DragonLifeStage lifeStage;
    public DragonBreath breath;
  }

  // settings for each component sound
  private static class ComponentSoundSettings {
    public ComponentSoundSettings(float i_volume) {
      masterVolume = i_volume;
    }
    public float masterVolume;  // multiplier for the volume = 0 .. 1
    public Vec3d soundEpicentre;
    public float playerDistanceToEpicentre;
    public boolean playing;
  }

  public enum RepeatType {REPEAT, NO_REPEAT}
  public enum Mode {PRELOAD, PLAY}

  private static class BreathWeaponSound extends PositionedSound implements ITickableSound {
    public BreathWeaponSound(ResourceLocation i_resourceLocation, float i_volume, RepeatType i_repeat,
                             ComponentSoundSettings i_soundSettings) {
      super(i_resourceLocation, SoundCategory.HOSTILE);
      repeat = (i_repeat == RepeatType.REPEAT);
      volume = i_volume;
      attenuationType = AttenuationType.NONE;
      soundSettings = i_soundSettings;
      playMode = Mode.PLAY;
    }

    /**
     * Preload for this sound (plays at very low volume).
     * Can't be a static method because that's not allowed in inner class
     * @param i_resourceLocation the sound to be played
     * @param mode dummy argument.  Must always be PRELOAD
     */
    public BreathWeaponSound(ResourceLocation i_resourceLocation, Mode mode) {
      super(i_resourceLocation, SoundCategory.VOICE);
      checkArgument(mode == Mode.PRELOAD);
      repeat = false;
      final float VERY_LOW_VOLUME = 0.001F;
      volume = VERY_LOW_VOLUME;
      attenuationType = AttenuationType.NONE;
      soundSettings = null;
      playMode = Mode.PRELOAD;
      preloadTimeCountDown = 5;  // play for a few ticks only
    }

    private void setDonePlaying() {
      donePlaying = true;
    }

    private boolean donePlaying;
    private ComponentSoundSettings soundSettings;
    private Mode playMode;

    public int getPlayCountdown() {
      return playTimeCountDown;
    }

    public void setPlayCountdown(int countdown) {
      playTimeCountDown = countdown;
    }

    private int playTimeCountDown = -1;
    private int preloadTimeCountDown = 0;


    @Override
    public boolean isDonePlaying() {
      return donePlaying;
    }

    @Override
    public void update() {
      final float MINIMUM_VOLUME = 0.10F;
      final float MAXIMUM_VOLUME = 1.00F;
      final float INSIDE_VOLUME = 1.00F;
      final float OFF_VOLUME = 0.0F;

      if (playMode == Mode.PRELOAD) {
        if (--preloadTimeCountDown <= 0) {
          this.volume = OFF_VOLUME;
        }
        return;
      }

      --playTimeCountDown;
      if (!soundSettings.playing) {
        this.volume = OFF_VOLUME;
      } else {
        this.xPosF = (float)soundSettings.soundEpicentre.x;
        this.yPosF = (float)soundSettings.soundEpicentre.y;
        this.zPosF = (float)soundSettings.soundEpicentre.z;
        if (soundSettings.playerDistanceToEpicentre < 0.01F) {
          this.volume = INSIDE_VOLUME;
        } else {
          final float MINIMUM_VOLUME_DISTANCE = 40.0F;
          float fractionToMinimum = soundSettings.playerDistanceToEpicentre / MINIMUM_VOLUME_DISTANCE;
          this.volume = soundSettings.masterVolume *
                  MathHelper.clamp(MAXIMUM_VOLUME - fractionToMinimum * (MAXIMUM_VOLUME - MINIMUM_VOLUME), MINIMUM_VOLUME, MAXIMUM_VOLUME);
        }
      }
    }
  }
}
