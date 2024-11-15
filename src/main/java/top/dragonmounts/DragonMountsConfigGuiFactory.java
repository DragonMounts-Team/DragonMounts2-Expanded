/*
** 2016 March 18
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package top.dragonmounts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Arrays;
import java.util.Set;

public class DragonMountsConfigGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraft) {
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
    public GuiScreen createConfigGui(GuiScreen parent) {
        Configuration config = DragonMountsConfig.getConfig();
        return new GuiConfig(parent, Arrays.asList(
                new ConfigElement(config.getCategory(DragonMountsConfig.CATEGORY_MAIN)),
                new ConfigElement(config.getCategory(DragonMountsConfig.CATEGORY_WORLDGEN)),
                new ConfigElement(config.getCategory(DragonMountsConfig.CATEGORY_CLIENTDM2))
        ), DragonMountsTags.MOD_ID, false, false, DragonMountsTags.MOD_NAME);
	}
}
