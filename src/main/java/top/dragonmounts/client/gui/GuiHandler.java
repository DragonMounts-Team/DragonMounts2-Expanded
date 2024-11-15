package top.dragonmounts.client.gui;

import top.dragonmounts.inventory.ContainerDragon;
import top.dragonmounts.inventory.ContainerDragonShulker;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.tileentities.TileEntityDragonShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int GUI_DRAGON = 0;
    public static final int GUI_DRAGON_WAND = 1;
    public static final int GUI_DRAGON_SHULKER = 3;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Object entity;
        switch (id) {
            case GUI_DRAGON:
                entity = world.getEntityByID(x);
                if (entity instanceof EntityTameableDragon) {
                    return new ContainerDragon((EntityTameableDragon) entity, player);
                }
                break;
            case GUI_DRAGON_SHULKER:
                entity = world.getTileEntity(new BlockPos(x, y, z));
                if (entity instanceof TileEntityDragonShulker) {
                    return new ContainerDragonShulker(player.inventory, (TileEntityDragonShulker) entity, player);
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
                if (entity instanceof EntityTameableDragon) {
                    return new GuiDragon(player, (EntityTameableDragon) entity);
                }
                break;
            case GUI_DRAGON_SHULKER:
                entity = world.getTileEntity(new BlockPos(x, y, z));
                if (entity instanceof TileEntityDragonShulker) {
                    return new GuiDragonShulker(player.inventory, (TileEntityDragonShulker) entity, player);
                }
        }
        return null;
    }
}