package net.dragonmounts.client.gui;

import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.inventory.DragonContainer;
import net.dragonmounts.inventory.DragonCoreContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public enum GuiHandler implements IGuiHandler {
    INSTANCE;
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
                    return new DragonContainer<>((TameableDragonEntity) entity, player);
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
                if (entity instanceof ClientDragonEntity) {
                    return new DragonInventoryGui(player, new DragonContainer<>((ClientDragonEntity) entity, player));
                }
                break;
            case GUI_DRAGON_CORE:
                entity = world.getTileEntity(new BlockPos(x, y, z));
                if (entity instanceof DragonCoreBlockEntity) {
                    return new DragonCoreGui(player.inventory, (DragonCoreBlockEntity) entity, player);
                }
        }
        return null;
    }
}