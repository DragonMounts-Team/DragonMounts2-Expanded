package net.dragonmounts.client.breath;


import net.minecraft.util.ResourceLocation;

import static net.dragonmounts.DragonMounts.makeId;

public interface BuiltinBreathTextures {
    ResourceLocation FLAME_BREATH = makeId("textures/entities/breath_fire.png");
    ResourceLocation AIRFLOW_BREATH = makeId("textures/entities/breath_air.png");
    ResourceLocation DARK_BREATH = makeId("textures/entities/breath_dark.png");
    ResourceLocation ENDER_BREATH = makeId("textures/entities/breath_acid.png");
    ResourceLocation WATER_BREATH = makeId("textures/entities/breath_hydro.png");
    ResourceLocation ICE_BREATH = makeId("textures/entities/breath_ice.png");
    ResourceLocation NETHER_BREATH = makeId("textures/entities/breath_nether.png");
    ResourceLocation SOUL_BREATH = makeId("textures/entities/breath_soul.png");
    ResourceLocation POISON_BREATH = makeId("textures/entities/breath_poison.png");
    ResourceLocation WITHER_BREATH = makeId("textures/entities/breath_wither.png");
}
