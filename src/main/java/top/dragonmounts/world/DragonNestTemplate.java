package top.dragonmounts.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class DragonNestTemplate extends StructureComponentTemplate {
    private String identifier;
    private Rotation rotation;
    private ResourceLocation lootTable;

    public DragonNestTemplate(TemplateManager manager, String name, ResourceLocation lootTable, Rotation rot, BlockPos pos) {
        super(0);
        this.identifier = name;
        this.lootTable = lootTable;
        this.templatePosition = pos;
        this.rotation = rot;
        this.loadTemplate(manager);
    }

    private void loadTemplate(TemplateManager manager) {
        this.setup(
                manager.getTemplate(null, new ResourceLocation(this.identifier)),
                this.templatePosition,
                new PlacementSettings()
        );
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setString("TID", this.identifier);
        tag.setString("ROT", this.rotation.name());
        if (this.lootTable != null) {
            tag.setString("LOOT", this.lootTable.toString());
        }
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager manager) {
        super.readStructureFromNBT(tag, manager);
        this.identifier = tag.getString("TID");
        this.rotation = Rotation.valueOf(tag.getString("ROT"));
        if (tag.hasKey("LOOT")) {
            this.lootTable = new ResourceLocation(tag.getString("LOOT"));
        }
        this.loadTemplate(manager);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, World level, Random random, StructureBoundingBox box) {
        if (this.lootTable == null) return;
        if (function.startsWith("Chest")) {
            pos = pos.down();
            if (box.isVecInside(pos)) {
                TileEntity entity = level.getTileEntity(pos);
                if (entity instanceof TileEntityChest) {
                    ((TileEntityChest) entity).setLootTable(this.lootTable, random.nextLong());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public DragonNestTemplate() {}
}
