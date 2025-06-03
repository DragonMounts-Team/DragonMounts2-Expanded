package net.dragonmounts.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class DragonNestPiece extends StructureComponentTemplate {
    private ResourceLocation structure;

    public DragonNestPiece(
            TemplateManager manager,
            ResourceLocation structure,
            BlockPos pos,
            Rotation rotation,
            Mirror mirror
    ) {
        this.structure = structure;
        this.templatePosition = pos;
        this.placeSettings.setIgnoreEntities(false).setRotation(rotation).setMirror(mirror);
        this.loadTemplate(manager);
    }

    private void loadTemplate(TemplateManager manager) {
        this.setup(
                manager.getTemplate(null, this.structure),
                this.templatePosition,
                this.placeSettings
        );
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setString("TID", this.structure.toString());
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager manager) {
        super.readStructureFromNBT(tag, manager);
        this.structure = new ResourceLocation(tag.getString("TID"));
        this.loadTemplate(manager);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, World level, Random random, StructureBoundingBox box) {}

    @SuppressWarnings("unused")
    public DragonNestPiece() {}
}
