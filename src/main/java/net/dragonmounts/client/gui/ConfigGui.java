/*
 ** 2016 March 18
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.client.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.config.DMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.Set;

public class ConfigGui implements IModGuiFactory {
    private static boolean showDebug; // until restart

    static void include(ObjectArrayList<IConfigElement> elements, ConfigCategory category) {
        for (Property prop : category.getOrderedValues()) {
            if (prop.showInGui()) {
                elements.add(new ConfigElement(prop));
            }
        }
        for (ConfigCategory sub : category.getChildren()) {
            if (sub.showInGui()) {
                elements.add(new ConfigElement(category));
            }
        }
    }

    @Override
    public void initialize(Minecraft minecraft) {}

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
        Configuration config = DMConfig.getConfig();
        ObjectArrayList<IConfigElement> elements = new ObjectArrayList<>();
        ConfigCategory debug = config.getCategory(DMConfig.CATEGORY_DEBUG);
        if (DMConfig.DEBUG_MODE.value) {
            showDebug = true;
            include(elements, debug);
        } else if (showDebug) {
            elements.add(new ConfigElement(DMConfig.DEBUG_MODE.getOrCreate(debug)));
        }
        include(elements, config.getCategory(DMConfig.CATEGORY_CLIENT));
        elements.add(new ConfigElement(config.getCategory(DMConfig.CATEGORY_ATTRIBUTES)));
        elements.add(new ConfigElement(config.getCategory(DMConfig.CATEGORY_GAMEPLAY)));
        elements.add(new ConfigElement(config.getCategory(DMConfig.CATEGORY_WORLD_GEN)));
        return new GuiConfig(parent, elements, DragonMountsTags.MOD_ID, false, false, DragonMountsTags.MOD_NAME);
    }
}
