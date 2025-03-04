package net.dragonmounts.block;

import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

public class DragonScaleBlock extends Block {
	public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_scale_block";
	public final DragonType type;

	public DragonScaleBlock(DragonType type, Material material, MapColor color, BlockProperties props) {
		super(material, color);
		this.type = type;
		this.setSoundType(props.sound)
				.setHardness(props.hardness)
				.setResistance(props.resistance)
				.setLightLevel(props.light);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
		tooltips.add(this.type.getName());
	}
}
