package net.dragonmounts.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;

public class DragonMountMaterial extends Material {
    public DragonMountMaterial(MapColor color) {
        super(color);
    }
    public static final Material AETHER_DRAGON_SCALES =( new Material(MapColor.LIGHT_BLUE));
    public static final Material WATER_DRAGON_SCALES = new Material(MapColor.BLUE);
    public static final Material ICE_DRAGON_SCALES = new Material(MapColor.ICE);
    public static final Material FIRE_DRAGON_SCALES = new Material(MapColor.RED);
    public static final Material FOREST_DRAGON_SCALES = new Material(MapColor.GRASS);
    public static final Material NETHER_DRAGON_SCALES = new Material(MapColor.NETHERRACK);
    public static final Material ENDER_DRAGON_SCALES = new Material(MapColor.PURPLE);
    public static final Material ENCHANT_DRAGON_SCALES = new Material(MapColor.MAGENTA);
    public static final Material SUNLIGHT_DRAGON_SCALES = new Material(MapColor.YELLOW);
    public static final Material MOONLIGHT_DRAGON_SCALES = new Material(MapColor.SILVER);
    public static final Material STORM_DRAGON_SCALES = new Material(MapColor.CYAN);
    public static final Material TERRA_DRAGON_SCALES = new Material(MapColor.BROWN);
    public static final Material ZOMBIE_DRAGON_SCALES = new Material(MapColor.GREEN);
    public static final Material DARK_DRAGON_SCALES = new Material(MapColor.BLACK);


}