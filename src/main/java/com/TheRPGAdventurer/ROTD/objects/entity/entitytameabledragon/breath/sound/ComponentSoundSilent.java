package com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.sound;

import net.minecraft.util.SoundCategory;

/**
 * Created by TGG on 24/06/2016.
 * Plays nothing
 */
public class ComponentSoundSilent extends ComponentSound
{
  public ComponentSoundSilent()
  {
    super(SoundEffectName.SILENCE.sound, SoundCategory.HOSTILE);
    final float OFF_VOLUME = 0.0F;
    volume = OFF_VOLUME;
  }

  @Override
  public void update()
  {
    final float OFF_VOLUME = 0.0F;
    this.volume = OFF_VOLUME;
    setDonePlaying();
  }
}
