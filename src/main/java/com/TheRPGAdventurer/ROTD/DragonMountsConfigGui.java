/*
** 2016 March 18
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD;

import com.TheRPGAdventurer.ROTD.dragonmounts.Tags;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.Arrays;
import java.util.List;

public class DragonMountsConfigGui extends GuiConfig {
    private static List<IConfigElement> getConfigElements() {
        Configuration config = DragonMountsConfig.getConfig();
        return Arrays.asList(
                new ConfigElement(config.getCategory(DragonMountsConfig.CATEGORY_MAIN)),
                new ConfigElement(config.getCategory(DragonMountsConfig.CATEGORY_WORLDGEN)),
                new ConfigElement(config.getCategory(DragonMountsConfig.CATEGORY_CLIENTDM2))
        );
    }

    public DragonMountsConfigGui(GuiScreen parent) {
        super(parent, getConfigElements(), Tags.MOD_ID, false, false, Tags.MOD_NAME);
    }
}