package com.TheRPGAdventurer.ROTD.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EntityUtil {
    public static boolean addOrMergeEffect(EntityLivingBase entity, Potion effect, int duration, int amplifier, boolean ambient, boolean visible) {
        PotionEffect instance = entity.getActivePotionEffect(effect);
        if (instance == null) {
            entity.addPotionEffect(new PotionEffect(effect, duration, amplifier, ambient, visible));
            return true;
        }
        int oldAmplifier = instance.getAmplifier();
        if (oldAmplifier < amplifier) return false;
        entity.addPotionEffect(new PotionEffect(effect, oldAmplifier == amplifier ? duration + instance.getDuration() : duration, amplifier, ambient, visible));
        return true;
    }

    public static boolean addOrResetEffect(EntityLivingBase entity, Potion effect, int duration, int amplifier, boolean ambient, boolean visible, int threshold) {
        PotionEffect instance = entity.getActivePotionEffect(effect);
        if (instance != null && instance.getAmplifier() == amplifier && instance.getDuration() > threshold)
            return false;
        entity.addPotionEffect(new PotionEffect(effect, duration, amplifier, ambient, visible));
        return true;
    }
}
