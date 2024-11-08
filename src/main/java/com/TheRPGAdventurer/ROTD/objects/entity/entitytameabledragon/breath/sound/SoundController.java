package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound;

import com.TheRPGAdventurer.ROTD.util.DMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: The Grey Ghost
 * Date: 17/04/2014
 * Was intended to encapsulate sound effects.  Doesn't do anything at the moment.
 */
public class SoundController {
    private HashMap<PositionedSound, SoundEffectTickLink> soundEffectsToTick = new HashMap<PositionedSound, SoundEffectTickLink>();

    public SoundController() {}

    public void playSound(PositionedSound sound) {
        DMUtils.getLogger().info("play sound 1");
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }

    public void playSound(PositionedSound sound, SoundEffectTickLink soundEffectTickLink) {
        DMUtils.getLogger().info("play sound 2");
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        soundEffectsToTick.put(sound, soundEffectTickLink);
    }

    public void stopSound(PositionedSound sound) {
        DMUtils.getLogger().info("stop sound");
        Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
        soundEffectsToTick.remove(sound);
    }

    public boolean isSoundPlaying(PositionedSound sound) {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound);
    }

    /**
     * tick all the sounds that need it
     */
    public void onTick() {
        Iterator<Map.Entry<PositionedSound, SoundEffectTickLink>> tickEntry = soundEffectsToTick.entrySet().iterator();
        while (tickEntry.hasNext()) {
            Map.Entry<PositionedSound, SoundEffectTickLink> entry = tickEntry.next();
            PositionedSound sound = entry.getKey();
            SoundEffectTickLink link = entry.getValue();
            boolean keepTicking = link.onTick(Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound));
            if (!keepTicking) {
                tickEntry.remove();
            }
        }
    }

//  public abstract class SoundControlLink
//  {
//    public abstract void startSound();
//    public abstract void stopSound();
//  }
//
//  private class SoundControlLinkDoNothing extends SoundControlLink
//  {
//    public  void startSound() {};
//    public  void stopSound() {};
//  }

    /**
     * The tick link is used by the controller to tick a sound
     */
    public interface SoundEffectTickLink {
        /**
         * called by the controller every tick
         *
         * @param stillPlaying true if the sound is still playing
         * @return true if the sound wants to keep ticking, false to stop
         */
        public boolean onTick(boolean stillPlaying);
    }
}
