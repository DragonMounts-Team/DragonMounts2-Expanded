package net.dragonmounts.client.gui;

import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.inventory.ContainerDragon;
import net.dragonmounts.inventory.DragonCoreContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int GUI_DRAGON = 0;
    public static final int GUI_DRAGON_WAND = 1;
    public static final int GUI_DRAGON_CORE = 3;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Object entity;
        switch (id) {
            case GUI_DRAGON:
                entity = world.getEntityByID(x);
                if (entity instanceof TameableDragonEntity) {
                    return new ContainerDragon((TameableDragonEntity) entity, player);
                }
                break;
            case GUI_DRAGON_CORE:
                entity = world.getTileEntity(new BlockPos(x, y, z));
                if (entity instanceof DragonCoreBlockEntity) {
                    return new DragonCoreContainer(player.inventory, (DragonCoreBlockEntity) entity, player);
                }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Object entity;
        switch (id) {
            case GUI_DRAGON:
                entity = world.getEntityByID(x);
                if (entity instanceof TameableDragonEntity) {
                    return new GuiDragon(player, (TameableDragonEntity) entity);
                }
                break;
            case GUI_DRAGON_CORE:
                entity = world.getTileEntity(new BlockPos(x, y, z));
                if (entity instanceof DragonCoreBlockEntity) {
                    return new GuiDragonCore(player.inventory, (DragonCoreBlockEntity) entity, player);
                }
        }
        return null;
    }
}